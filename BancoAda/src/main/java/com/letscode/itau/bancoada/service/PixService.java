package com.letscode.itau.bancoada.service;

import com.google.gson.Gson;
import com.letscode.itau.bancoada.dto.PixDTOResponse;
import com.letscode.itau.bancoada.dto.PixSolicitacaoDTORequest;
import com.letscode.itau.bancoada.enumeration.Status;
import com.letscode.itau.bancoada.model.Conta;
import com.letscode.itau.bancoada.dto.PixDTORequest;
import com.letscode.itau.bancoada.model.PixTransferencia;
import com.letscode.itau.bancoada.repository.ContaRepository;
import com.letscode.itau.bancoada.repository.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PixService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ContaRepository contaRepository;
    private final TransferenciaRepository transferenciaRepository;

    public Mono<ResponseEntity<Conta>> enviaPix(PixDTORequest pixDTORequest) {
        String msg = new Gson().toJson(pixDTORequest);
        return contaRepository.findByNumeroContaAndAgencia(pixDTORequest.getContaRemetente(), pixDTORequest.getAgenciaRemetente())
                .flatMap(conta -> {
                    if (conta.getSaldo().compareTo(pixDTORequest.getValor()) >= 0) {
                        kafkaTemplate.send("ada-pix-solicitacao", msg);
                        PixTransferencia pixTransferencia = new PixTransferencia(pixDTORequest.getReqId(), pixDTORequest.getChave(), pixDTORequest.getValor(), pixDTORequest.getData(), "Ada", pixDTORequest.getContaRemetente(), pixDTORequest.getAgenciaRemetente());
                        transferenciaRepository.save(pixTransferencia).subscribe(System.out::println);
                        conta.setSaldo(conta.getSaldo().subtract(pixDTORequest.getValor()));
                        return contaRepository.save(conta).map(ResponseEntity::ok);
                    }
                    return Mono.error(new RuntimeException("Sem saldo suficiente"));
                }
        );
    }

    @KafkaListener(groupId = "myId3", topics = "pix-confirmacao-ada")
    public void getStatusBacenPix(String msg) {
        PixDTOResponse pixDTOResponse = new Gson().fromJson(msg, PixDTOResponse.class);
        if (Status.Recusado.equals(pixDTOResponse.getStatus())) {
            System.out.println("Rollback de pix");
            transferenciaRepository.findById(pixDTOResponse.getReqId()).subscribe(transferencia -> {
                transferencia.setStatus(Status.Recusado);
                transferenciaRepository.save(transferencia).subscribe();
                contaRepository.findByNumeroContaAndAgencia(transferencia.getContaRemetente(), transferencia.getAgenciaRemetente())
                        .subscribe(conta -> {
                            conta.setSaldo(conta.getSaldo().add(transferencia.getValor()));
                            contaRepository.save(conta).subscribe();
                        }
                );
            });
        } else if (Status.Aceito.equals(pixDTOResponse.getStatus())) {
            System.out.println("Pix aceito!");
            transferenciaRepository.findById(pixDTOResponse.getReqId()).subscribe(
                    transferencia -> {
                        transferencia.setStatus(Status.Aceito);
                        transferenciaRepository.save(transferencia).subscribe();
                    }
            );
        }
    }

    @KafkaListener(groupId = "myId4", topics = "pix-solicitacao-ada")
    public void getSolicitacaoPix(String msg) {
        PixSolicitacaoDTORequest pixSolicitacaoDTORequest = new Gson().fromJson(msg, PixSolicitacaoDTORequest.class);
        contaRepository.findByCpf(pixSolicitacaoDTORequest.getChave())
                // TODO Se a conta não existir?
                .subscribe(conta -> {
                    conta.setSaldo(conta.getSaldo().add(pixSolicitacaoDTORequest.getValor()));
                    contaRepository.save(conta).subscribe();

                    PixTransferencia pixTransferencia = pixSolicitacaoDTORequest.mapperToEntity(Status.Aceito);
                    transferenciaRepository.save(pixTransferencia).subscribe();

                    kafkaTemplate.send("ada-pix-confirmacao", new Gson().toJson(new PixDTOResponse(pixTransferencia.getReqId(), pixTransferencia.getStatus())));

                    System.out.println("Transferência aceita!");
                });
    }

}
