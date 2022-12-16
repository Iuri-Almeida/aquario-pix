package com.letscode.itau.bancoitau.service;

import com.google.gson.Gson;
import com.letscode.itau.bancoitau.dto.PixDTORequest;
import com.letscode.itau.bancoitau.dto.PixDTOResponse;
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
import reactor.core.scheduler.Schedulers;

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
                        PixTransferencia pixTransferencia = new PixTransferencia(pixDTO.getReqId(), pixDTO.getChave(), pixDTO.getValor(), pixDTO.getData(), pixDTO.getContaRemetente(), pixDTO.getAgenciaRemetente());
                        transferenciaRepository.save(pixTransferencia).subscribe(System.out::println);
                        BigDecimal novoSaldo = conta.getSaldo().subtract(pixDTO.getValor());
                        conta.setSaldo(novoSaldo);
                        return contaRepository.save(conta).map(ResponseEntity::ok);
                    }
                    return Mono.error(new RuntimeException("Sem saldo suficiente"));
                }
        );
    }

    @KafkaListener(id = "myId2", topics = "pix-solicitacao-itau")
    public void getStatusBacenPix(String mensagem) {
        PixDTOResponse pixDTOResponse = new Gson().fromJson(mensagem, PixDTOResponse.class);
        if (Status.Recusado.equals(pixDTOResponse.getStatus())) {
            System.out.println("Rollback de pix");
            String reqId = pixDTOResponse.getReqId();
            transferenciaRepository.findById(reqId).subscribe(transferencia -> {
                transferencia.setStatus(Status.Recusado);
                String agencia = transferencia.getAgenciaRemetente();
                String numeroConta = transferencia.getContaRemetente();
                BigDecimal valor = transferencia.getValor();

                contaRepository.findByNumeroContaAndAgencia(numeroConta, agencia).subscribe(
                        conta -> {
                            BigDecimal saldo = conta.getSaldo();
                            BigDecimal novoSaldo = saldo.add(valor);
                            conta.setSaldo(novoSaldo);
                            contaRepository.save(conta);
                        }
                );
            });
        } else if (Status.Aceito.equals(pixDTOResponse.getStatus())) {
            transferenciaRepository.findById(pixDTOResponse.getReqId()).subscribe(
                    transferencia -> {
                        transferencia.setStatus(Status.Aceito);
                    }
            );
        }
    }
}

