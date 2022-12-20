package com.letscode.itau.bancoada.service;

import com.google.gson.Gson;
import com.letscode.itau.bancoada.dto.PixDTOResponse;
import com.letscode.itau.bancoada.dto.PixSolicitacaoDTORequest;
import com.letscode.itau.bancoada.enumeration.Status;
import com.letscode.itau.bancoada.kafka.producer.AdaKafkaProducer;
import com.letscode.itau.bancoada.model.Conta;
import com.letscode.itau.bancoada.dto.PixDTORequest;
import com.letscode.itau.bancoada.model.PixTransferencia;
import com.letscode.itau.bancoada.repository.ContaRepository;
import com.letscode.itau.bancoada.repository.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PixService {

    private final AdaKafkaProducer adaKafkaProducer;
    private final ContaRepository contaRepository;
    private final TransferenciaRepository transferenciaRepository;

    public Mono<ResponseEntity<Conta>> enviaPix(PixDTORequest pixDTORequest) {
        String msg = this.object2Json(pixDTORequest);

        return contaRepository.findByNumeroContaAndAgencia(pixDTORequest.getContaRemetente(), pixDTORequest.getAgenciaRemetente())
                .flatMap(conta -> {
                    if (this.isSaldoMaiorOuIgualQueSaldoSolicitado(conta.getSaldo(), pixDTORequest.getValor())) {
                        this.publicarMensagem("ada-pix-solicitacao", msg);

                        PixTransferencia pixTransferencia = new PixTransferencia(pixDTORequest.getReqId(), pixDTORequest.getChave(), pixDTORequest.getValor(), pixDTORequest.getDataHora(), "Ada", pixDTORequest.getContaRemetente(), pixDTORequest.getAgenciaRemetente());
                        transferenciaRepository.save(pixTransferencia).subscribe();

                        this.subtrairSaldo(conta, pixDTORequest.getValor());

                        return contaRepository.save(conta).map(ResponseEntity::ok);
                    }

                    return Mono.error(new RuntimeException("Sem saldo suficiente"));
                }
        );
    }

    public void getStatusBacenPix(PixDTOResponse pixDTOResponse) {
        if (this.isStatusRecusado(pixDTOResponse.getStatus())) {
            System.out.println("Rollback de pix");

            transferenciaRepository.findByReqId(pixDTOResponse.getReqId()).subscribe(transferencia -> {

                transferencia.setStatus(Status.Recusado);
                transferenciaRepository.save(transferencia).subscribe();

                contaRepository.findByNumeroContaAndAgencia(transferencia.getContaRemetente(), transferencia.getAgenciaRemetente())
                        .subscribe(conta -> {
                            this.adicionarSaldo(conta, transferencia.getValor());
                            contaRepository.save(conta).subscribe();
                        }
                );
            });

        } else if (Status.Aceito.equals(pixDTOResponse.getStatus())) {
            System.out.println("Pix aceito!");

            transferenciaRepository.findByReqId(pixDTOResponse.getReqId()).subscribe(
                    transferencia -> {
                        transferencia.setStatus(Status.Aceito);
                        transferenciaRepository.deleteByReqId(transferencia.getReqId()).subscribe();
                        transferenciaRepository.save(transferencia).subscribe();
                    }
            );
        }
    }

    public void getSolicitacaoPix(PixSolicitacaoDTORequest pixSolicitacaoDTORequest) {
        contaRepository.findByCpf(pixSolicitacaoDTORequest.getChave())
                // TODO Se a conta não existir?
                .subscribe(conta -> {
                    this.adicionarSaldo(conta, pixSolicitacaoDTORequest.getValor());
                    contaRepository.save(conta).subscribe();

                    PixTransferencia pixTransferencia = pixSolicitacaoDTORequest.mapperToEntity(Status.Aceito);
                    transferenciaRepository.save(pixTransferencia).subscribe();

                    PixDTOResponse pixDTOResponse = new PixDTOResponse(pixTransferencia.getReqId(), pixTransferencia.getStatus());
                    this.publicarMensagem("ada-pix-confirmacao", new Gson().toJson(pixDTOResponse));

                    System.out.println("Transferência aceita!");
                });
    }

    private String object2Json(Object obj) {
        return new Gson().toJson(obj);
    }

    private boolean isSaldoMaiorOuIgualQueSaldoSolicitado(BigDecimal saldoDaConta, BigDecimal saldoSolicitado) {
        return saldoDaConta.compareTo(saldoSolicitado) >= 0;
    }

    private void publicarMensagem(String topic, String msg) {
        adaKafkaProducer.publicar(topic, msg);
    }

    private void subtrairSaldo(Conta conta, BigDecimal valor) {
        conta.setSaldo(conta.getSaldo().subtract(valor));
    }

    private void adicionarSaldo(Conta conta, BigDecimal valor) {
        conta.setSaldo(conta.getSaldo().add(valor));
    }

    private boolean isStatusRecusado(Status status) {
        return Status.Recusado.equals(status);
    }

}
