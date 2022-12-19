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

    private String criaMensagemKafka(PixDTORequest pixDTO) {
        return new Gson().toJson(pixDTO);
    }

    private boolean hasSaldoSuficiente(BigDecimal valorPix, BigDecimal saldoEmConta) {
        return saldoEmConta.compareTo(valorPix) >= 0;
    }

    public Mono<ResponseEntity<Conta>> enviaPix(PixDTORequest pixDTO) {

        String mensagem = criaMensagemKafka(pixDTO);
        return contaRepository.findByNumeroContaAndAgencia(pixDTO.getContaRemetente(), pixDTO.getAgenciaRemetente()).flatMap(
                conta -> {

                    BigDecimal valorPix = pixDTO.getValor();
                    BigDecimal saldoEmConta = conta.getSaldo();
                    if (hasSaldoSuficiente(valorPix, saldoEmConta)) {
                        enviaSolicitacaoKafkaPix(mensagem);
                        PixTransferencia pixTransferencia = criaTransferencia(pixDTO, valorPix);
                        salvaTransferencia(pixTransferencia);
                        BigDecimal novoSaldo = calculaNovoSaldo(valorPix, saldoEmConta);

                        conta.setSaldo(novoSaldo);
                        return atualizaContaComNovoSaldo(conta);
                    }
                    return Mono.error(new RuntimeException("Sem saldo suficiente"));
                }
        );
    }

    private Mono<ResponseEntity<Conta>> atualizaContaComNovoSaldo(Conta conta) {
        return contaRepository.save(conta).map(ResponseEntity::ok);
    }

    private BigDecimal calculaNovoSaldo(BigDecimal valorPix, BigDecimal saldoEmConta) {
        return saldoEmConta.subtract(valorPix);
    }

    private void salvaTransferencia(PixTransferencia pixTransferencia) {
        transferenciaRepository.save(pixTransferencia).subscribe(System.out::println);
    }

    private PixTransferencia criaTransferencia(PixDTORequest pixDTO, BigDecimal valorPix) {
        return PixTransferencia.builder()
                .reqId(pixDTO.getReqId())
                .chave(pixDTO.getChave())
                .valor(valorPix)
                .dataHora(pixDTO.getDataHora())
                .bancoRemetente(pixDTO.getBancoRemetente())
                .contaRemetente(pixDTO.getContaRemetente())
                .agenciaRemetente(pixDTO.getAgenciaRemetente())
                .build();
    }

    private void enviaSolicitacaoKafkaPix(String mensagem) {
        kafkaTemplate.send("itau-pix-solicitacao", mensagem);
    }

    @KafkaListener(id = "myId2", topics = "pix-confirmacao-itau")
    public void getStatusBacenPix(String mensagem) {
        PixDTOResponse pixDTOResponse = criaPixDTOResponse(mensagem);
        if (pixRecusado(pixDTOResponse)) {
            System.out.println("Rollback de pix");
            String reqId = getReqId(pixDTOResponse);
            transferenciaRepository.findByReqId(reqId).subscribe(transferencia -> {
                mudaStatusDaTransferencia(transferencia, Status.Recusado);
                deletaTransferenciaComStatusPendente(transferencia);
                salvaTransferenciaComNovoStatus(transferencia);

                String agencia = transferencia.getAgenciaRemetente();
                String numeroConta = transferencia.getContaRemetente();
                BigDecimal valor = transferencia.getValor();

                rollbackDoPix(agencia, numeroConta, valor);
            });
        } else if (pixAceito(pixDTOResponse)) {
            transferenciaRepository.findByReqId(getReqId(pixDTOResponse)).subscribe(
                    transferencia -> {
                        mudaStatusDaTransferencia(transferencia, Status.Aceito);
                        // TODO corrigir o erro ao adicionar o .subscribe() (sem ele não é atualizado no banco)
                        deletaTransferenciaComStatusPendente(transferencia);
                        salvaTransferenciaComNovoStatus(transferencia);
                    }
            );
        }
    }

    private boolean pixAceito(PixDTOResponse pixDTOResponse) {
        return Status.Aceito.equals(pixDTOResponse.getStatus());
    }

    private void rollbackDoPix(String agencia, String numeroConta, BigDecimal valor) {
        contaRepository.findByNumeroContaAndAgencia(numeroConta, agencia).subscribe(
                conta -> {
                    BigDecimal saldo = conta.getSaldo();
                    BigDecimal novoSaldo = saldo.add(valor);
                    conta.setSaldo(novoSaldo);
                    contaRepository.save(conta).subscribe();
                }
        );
    }

    private void salvaTransferenciaComNovoStatus(PixTransferencia transferencia) {
        transferenciaRepository.save(transferencia).subscribe();
    }

    private void deletaTransferenciaComStatusPendente(PixTransferencia transferencia) {
        transferenciaRepository.deleteByReqId(transferencia.getReqId()).subscribe();
    }

    private void mudaStatusDaTransferencia(PixTransferencia transferencia, Status recusado) {
        transferencia.setStatus(recusado);
    }

    private String getReqId(PixDTOResponse pixDTOResponse) {
        return pixDTOResponse.getReqId();
    }

    private boolean pixRecusado(PixDTOResponse pixDTOResponse) {
        return Status.Recusado.equals(pixDTOResponse.getStatus());
    }

    private PixDTOResponse criaPixDTOResponse(String mensagem) {
        return new Gson().fromJson(mensagem, PixDTOResponse.class);
    }

    @KafkaListener(groupId = "myId5", topics = "pix-solicitacao-itau")
    public void getSolicitacaoPix(String msg) {
        PixSolicitacaoDTORequest pixSolicitacaoDTORequest = new Gson().fromJson(msg, PixSolicitacaoDTORequest.class);
        contaRepository.findByCpf(pixSolicitacaoDTORequest.getChave())
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

