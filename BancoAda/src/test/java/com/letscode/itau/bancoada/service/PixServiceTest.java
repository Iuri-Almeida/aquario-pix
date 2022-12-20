package com.letscode.itau.bancoada.service;

import com.letscode.itau.bancoada.dto.PixDTORequest;
import com.letscode.itau.bancoada.dto.PixDTOResponse;
import com.letscode.itau.bancoada.dto.PixSolicitacaoDTORequest;
import com.letscode.itau.bancoada.enumeration.Status;
import com.letscode.itau.bancoada.kafka.producer.AdaKafkaProducer;
import com.letscode.itau.bancoada.model.Conta;
import com.letscode.itau.bancoada.model.PixTransferencia;
import com.letscode.itau.bancoada.repository.ContaRepository;
import com.letscode.itau.bancoada.repository.TransferenciaRepository;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PixServiceTest {

    @Mock
    private AdaKafkaProducer adaKafkaProducer;
    @Mock
    private ContaRepository contaRepository;
    @Mock
    private TransferenciaRepository transferenciaRepository;
    @InjectMocks
    private PixService pixService;

    private Conta getConta(BigDecimal saldo) {
        return new Conta(1L, "Iuri", "12345678910", "iuri@itau.com.br", "2654", "021", saldo);
    }

    private PixDTORequest getPixDTORequest(BigDecimal valor, String conta, String agencia) {
        return new PixDTORequest("12345678910", valor, conta, agencia);
    }

    private PixDTOResponse getPixDTOResponse(Status status) {
        return new PixDTOResponse(String.valueOf(new Random().nextInt(Integer.MAX_VALUE)), status);
    }

    private PixSolicitacaoDTORequest getPixSolicitacaoDTORequest(BigDecimal valor) {
        return new PixSolicitacaoDTORequest(String.valueOf(new Random().nextInt(Integer.MAX_VALUE)), "12345678910", valor, LocalDateTime.now(), "Itau", "2654", "021");
    }

    @Test
    @DisplayName("Deve enviar o pix de um banco para outro.")
    public void enviarPixTest() {
        Conta conta = this.getConta(new BigDecimal(1000));
        PixDTORequest pixDTO = this.getPixDTORequest(new BigDecimal(100), conta.getNumeroConta(), conta.getAgencia());

        when(contaRepository.save(conta)).thenReturn(Mono.just(conta));
        when(contaRepository.findByNumeroContaAndAgencia(pixDTO.getContaRemetente(), pixDTO.getAgenciaRemetente())).thenReturn(Mono.just(conta));
        when(transferenciaRepository.save(any())).thenReturn(Mono.just(new PixTransferencia()));

        pixService.enviaPix(pixDTO).subscribe(response -> {
            Assert.assertEquals(new BigDecimal(900), Objects.requireNonNull(response.getBody()).getSaldo());
            Assert.assertEquals(200, response.getStatusCodeValue());
        });
    }

    @Test
    @DisplayName("Deve gerar um erro ao enviar o pix de um banco para outro.")
    public void enviaPixErroTest() {
        Conta conta = this.getConta(new BigDecimal(0));
        PixDTORequest pixDTO = this.getPixDTORequest(new BigDecimal(100), conta.getNumeroConta(), conta.getAgencia());

        when(contaRepository.findByNumeroContaAndAgencia(pixDTO.getContaRemetente(), pixDTO.getAgenciaRemetente())).thenReturn(Mono.just(conta));

        pixService.enviaPix(pixDTO).doOnError(e -> Assert.assertEquals(e.getClass(), RuntimeException.class)).subscribe();
    }

    @Test
    @DisplayName("Deve gerar o rollback de um pix.")
    public void getStatusBacenPixRecusadoTest() {
        PixTransferencia pixTransferencia = new PixTransferencia();
        pixTransferencia.setAgenciaRemetente("001");
        pixTransferencia.setValor(new BigDecimal(100));

        Conta conta = this.getConta(new BigDecimal(0));

        Assert.assertEquals(new BigDecimal(0),conta.getSaldo());

        when(transferenciaRepository.findByReqId(any())).thenReturn(Mono.just(pixTransferencia));
        when(transferenciaRepository.save(any())).thenReturn(Mono.just(pixTransferencia));
        when(contaRepository.findByNumeroContaAndAgencia(any(), any())).thenReturn(Mono.just(conta));
        when(contaRepository.save(any())).thenReturn(Mono.just(conta));

        PixDTOResponse pixDTOResponse = this.getPixDTOResponse(Status.Recusado);

        pixService.getStatusBacenPix(pixDTOResponse);

        Assert.assertEquals(new BigDecimal(100),conta.getSaldo());
        Assert.assertNotEquals(new BigDecimal(0), conta.getSaldo());
    }

    @Test
    @DisplayName("Deve gerar um pix aceito.")
    public void getStatusBacenPixAceitoTest() {
        PixTransferencia pixTransferencia = new PixTransferencia();
        pixTransferencia.setAgenciaRemetente("001");
        pixTransferencia.setValor(new BigDecimal(100));

        Conta contaRemetente = this.getConta(new BigDecimal(0));

        Assert.assertEquals(new BigDecimal(0),contaRemetente.getSaldo());

        when(transferenciaRepository.findByReqId(any())).thenReturn(Mono.just(pixTransferencia));
        when(transferenciaRepository.deleteByReqId(any())).thenReturn(Mono.just(new PixTransferencia()));
        when(transferenciaRepository.save(any())).thenReturn(Mono.just(pixTransferencia));

        PixDTOResponse pixDTOResponse = this.getPixDTOResponse(Status.Aceito);

        pixService.getStatusBacenPix(pixDTOResponse);

        Assert.assertEquals(new BigDecimal(0), contaRemetente.getSaldo());
    }

    @Test
    @DisplayName("Deve gerar um pix aceito.")
    public void getSolicitacaoPixTest() {

        PixSolicitacaoDTORequest pixSolicitacaoDTORequest = this.getPixSolicitacaoDTORequest(new BigDecimal(100));
        Conta conta = this.getConta(new BigDecimal(0));

        when(contaRepository.findByCpf(any())).thenReturn(Mono.just(conta));
        when(contaRepository.save(any())).thenReturn(Mono.just(conta));
        when(transferenciaRepository.save(any())).thenReturn(Mono.just(new PixTransferencia()));

        pixService.getSolicitacaoPix(pixSolicitacaoDTORequest);

        Assert.assertEquals(new BigDecimal(100),conta.getSaldo());
    }

}