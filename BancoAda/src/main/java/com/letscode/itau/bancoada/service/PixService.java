package com.letscode.itau.bancoada.service;

import com.google.gson.Gson;
import com.letscode.itau.bancoada.dto.PixDTOResponse;
import com.letscode.itau.bancoada.enumeration.Status;
import com.letscode.itau.bancoada.model.Conta;
import com.letscode.itau.bancoada.dto.PixDTORequest;
import com.letscode.itau.bancoada.repository.ContaRepository;
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

    public Mono<ResponseEntity<Conta>> enviaPix(PixDTORequest pixDTORequest) {
        String msg = new Gson().toJson(pixDTORequest);
        return contaRepository.findByNumeroContaAndAgencia(pixDTORequest.getContaRemetente(), pixDTORequest.getAgenciaRemetente())
                .flatMap(conta -> {
                    if (conta.getSaldo().compareTo(pixDTORequest.getValor()) >= 0) {
                        kafkaTemplate.send("ada-pix-solicitacao", msg);
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
            this.realizaTransacao(pixDTOResponse);
            System.out.println("Rollback de pix");
        } else {
            System.out.println("Pix aceito!");
        }
    }

    @KafkaListener(groupId = "myId4",topics = "pix-solicitacao-ada")
    public void getSolicitacaoPix(String msg) {
        PixDTOResponse pixDTOResponse = new Gson().fromJson(msg, PixDTOResponse.class);
        if (!Status.Recusado.equals(pixDTOResponse.getStatus())) {
            this.realizaTransacao(pixDTOResponse);
            pixDTOResponse.setStatus(Status.Aceito);
        } else {
            System.out.println("Pix já está negado!");
        }
        kafkaTemplate.send("ada-pix-confirmacao", new Gson().toJson(pixDTOResponse));
    }

    private void realizaTransacao(PixDTOResponse pix) {
        contaRepository.findByNumeroContaAndAgencia(pix.getContaRemetente(), pix.getAgenciaRemetente())
                .subscribe(conta -> {
                    conta.setSaldo(conta.getSaldo().add(pix.getValor()));
                    contaRepository.save(conta);
                });
    }

}
