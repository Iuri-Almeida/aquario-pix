package com.letscode.itau.bancoitau.service;

import com.google.gson.Gson;
import com.letscode.itau.bancoitau.dto.PixDTORequest;
import com.letscode.itau.bancoitau.dto.PixDTOResponse;
import com.letscode.itau.bancoitau.dto.PixSolicitacaoDTORequest;
import com.letscode.itau.bancoitau.enumeration.Status;
import com.letscode.itau.bancoitau.model.Conta;
import com.letscode.itau.bancoitau.model.PixTransferencia;
import com.letscode.itau.bancoitau.repository.ContaRepository;
import com.letscode.itau.bancoitau.repository.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PixService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ContaRepository contaRepository;
    private final TransferenciaRepository transferenciaRepository;


    public Mono<ResponseEntity<Conta>> enviaPix(PixDTORequest pixDTO) {
        //TODO confirmacao existencia chave
        //GET /api/bacen/pix/cahves?tipo=CPF&cahve=465468456
        String mensagem = new Gson().toJson(pixDTO);
        return contaRepository.findByNumeroContaAndAgencia(pixDTO.getContaRemetente(), pixDTO.getAgenciaRemetente()).flatMap(
                conta -> {
                    if (conta.getSaldo().compareTo(pixDTO.getValor()) >= 0) {
                        kafkaTemplate.send("itau-pix-solicitacao", mensagem);
                        PixTransferencia pixTransferencia = new PixTransferencia(pixDTO.getReqId(), pixDTO.getChave(), pixDTO.getValor(), pixDTO.getDataHora(), pixDTO.getBancoRemetente(), pixDTO.getContaRemetente(), pixDTO.getAgenciaRemetente());
                        transferenciaRepository.save(pixTransferencia).subscribe(System.out::println);
                        BigDecimal novoSaldo = conta.getSaldo().subtract(pixDTO.getValor());
                        conta.setSaldo(novoSaldo);
                        return contaRepository.save(conta).map(ResponseEntity::ok);
                    }
                    return Mono.error(new RuntimeException("Sem saldo suficiente"));
                }
        );
    }

    @KafkaListener(id = "myId2", topics = "pix-confirmacao-itau")
    public void getStatusBacenPix(String mensagem) {
        PixDTOResponse pixDTOResponse = new Gson().fromJson(mensagem, PixDTOResponse.class);
        if (Status.Recusado.equals(pixDTOResponse.getStatus())) {
            System.out.println("Rollback de pix");
            String reqId = pixDTOResponse.getReqId();
            transferenciaRepository.findById(reqId).subscribe(transferencia -> {
                transferencia.setStatus(Status.Recusado);
                transferenciaRepository.deleteByReqId(transferencia.getReqId()).subscribe();
                transferenciaRepository.save(transferencia).subscribe();

                String agencia = transferencia.getAgenciaRemetente();
                String numeroConta = transferencia.getContaRemetente();
                BigDecimal valor = transferencia.getValor();

                contaRepository.findByNumeroContaAndAgencia(numeroConta, agencia).subscribe(
                        conta -> {
                            BigDecimal saldo = conta.getSaldo();
                            BigDecimal novoSaldo = saldo.add(valor);
                            conta.setSaldo(novoSaldo);
                            contaRepository.save(conta).subscribe();
                        }
                );
            });
        } else if (Status.Aceito.equals(pixDTOResponse.getStatus())) {
            transferenciaRepository.findByReqId(pixDTOResponse.getReqId()).subscribe(
                    transferencia -> {
                        transferencia.setStatus(Status.Aceito);
                        // TODO corrigir o erro ao adicionar o .subscribe() (sem ele não é atualizado no banco)
                        transferenciaRepository.deleteByReqId(transferencia.getReqId()).subscribe();
                        transferenciaRepository.save(transferencia).subscribe();
                    }
            );
        }
    }

    @KafkaListener(groupId = "myId5", topics = "pix-solicitacao-itau")
    public void getSolicitacaoPix(String msg) {
        PixSolicitacaoDTORequest pixSolicitacaoDTORequest = new Gson().fromJson(msg, PixSolicitacaoDTORequest.class);
        contaRepository.findByCpf(pixSolicitacaoDTORequest.getChave())
                // TODO Se a conta não existir?
                .subscribe(conta -> {
                    conta.setSaldo(conta.getSaldo().add(pixSolicitacaoDTORequest.getValor()));
                    contaRepository.save(conta).subscribe();

                    PixTransferencia pixTransferencia = pixSolicitacaoDTORequest.mapperToEntity(Status.Aceito);
                    transferenciaRepository.save(pixTransferencia).subscribe();

                    kafkaTemplate.send("itau-pix-confirmacao", new Gson().toJson(new PixDTOResponse(pixTransferencia.getStatus(), pixTransferencia.getReqId())));

                    System.out.println("Transferência aceita!");
                });
    }
}

