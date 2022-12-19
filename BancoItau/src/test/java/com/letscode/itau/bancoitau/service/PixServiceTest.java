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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PixServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private ContaRepository contaRepository;
    @Mock
    private TransferenciaRepository transferenciaRepository;
    @InjectMocks
    private PixService pixService;


    @Test
    public void enviaPixComSaldoTest() {

        PixDTORequest pixDTO = new PixDTORequest("123", new BigDecimal(100), "123456", "9876");

        Conta contaRemetente = new Conta(1L, "Teste", "1234", "teste@gmail.com", "123456", "9876", new BigDecimal(100));


        when(contaRepository.findByNumeroContaAndAgencia(pixDTO.getContaRemetente(), pixDTO.getAgenciaRemetente())).thenReturn(Mono.just(contaRemetente));
        when(transferenciaRepository.save(any())).thenReturn(Mono.just(new PixTransferencia()));
        when(contaRepository.save(any())).thenReturn(Mono.just(contaRemetente));


        pixService.enviaPix(pixDTO).subscribe(response -> {
            Assert.assertEquals(new BigDecimal(0), Objects.requireNonNull(response.getBody()).getSaldo());
            Assert.assertEquals(200, response.getStatusCodeValue());
        });
    }

    @Test
    public void enviaPixSemSaldoTest() {
        PixDTORequest pixDTO = new PixDTORequest("123", new BigDecimal(100), "123456", "9876");

        Conta contaRemetente = new Conta(1L, "Teste", "1234", "teste@gmail.com", "123456", "9876", new BigDecimal(0));


        when(contaRepository.findByNumeroContaAndAgencia(pixDTO.getContaRemetente(), pixDTO.getAgenciaRemetente())).thenReturn(Mono.just(contaRemetente));


        pixService.enviaPix(pixDTO).doOnError(e -> {
            Assert.assertEquals(e.getClass(), RuntimeException.class);
        }).subscribe();
    }

    @Test
    public void getStatusBacenPixTestRecusado() {
        PixTransferencia pixTransferencia = new PixTransferencia();
        pixTransferencia.setAgenciaRemetente("001");
        pixTransferencia.setValor(new BigDecimal(100));
        pixTransferencia.setAgenciaRemetente("001");

        Conta contaRemetente = new Conta(1L, "Teste", "1234", "teste@gmail.com", "123456", "9876", new BigDecimal(0));

        Assert.assertEquals(new BigDecimal(0),contaRemetente.getSaldo());

        when(transferenciaRepository.findByReqId(any())).thenReturn(Mono.just(pixTransferencia));
        when(transferenciaRepository.deleteByReqId(any())).thenReturn(Mono.just(new PixTransferencia()));
        when(transferenciaRepository.save(any())).thenReturn(Mono.just(pixTransferencia));
        when(contaRepository.findByNumeroContaAndAgencia(any(),any())).thenReturn(Mono.just(contaRemetente));
        when(contaRepository.save(any())).thenReturn(Mono.just(contaRemetente));

        PixDTOResponse pixDTOResponse = new PixDTOResponse(Status.Recusado, "123");

        String mensagem = new Gson().toJson(pixDTOResponse);
        pixService.getStatusBacenPix(mensagem);

        Assert.assertEquals(new BigDecimal(100),contaRemetente.getSaldo());
        Assert.assertNotEquals(new BigDecimal(0), contaRemetente.getSaldo());

    }

    @Test
    public void getStatusBacenPixTestAceito() {
        PixTransferencia pixTransferencia = new PixTransferencia();
        pixTransferencia.setAgenciaRemetente("001");
        pixTransferencia.setValor(new BigDecimal(100));
        pixTransferencia.setAgenciaRemetente("001");

        Conta contaRemetente = new Conta(1L, "Teste", "1234", "teste@gmail.com", "123456", "9876", new BigDecimal(0));

        Assert.assertEquals(new BigDecimal(0),contaRemetente.getSaldo());

        when(transferenciaRepository.findByReqId(any())).thenReturn(Mono.just(pixTransferencia));
        when(transferenciaRepository.deleteByReqId(any())).thenReturn(Mono.just(new PixTransferencia()));
        when(transferenciaRepository.save(any())).thenReturn(Mono.just(pixTransferencia));

        PixDTOResponse pixDTOResponse = new PixDTOResponse(Status.Aceito, "123");

        String mensagem = new Gson().toJson(pixDTOResponse);
        pixService.getStatusBacenPix(mensagem);

        Assert.assertEquals(new BigDecimal(0),contaRemetente.getSaldo());

    }

    @Test
    public void getSolicitacaoPixTest() {

        PixSolicitacaoDTORequest pixSolicitacaoDTORequest = new PixSolicitacaoDTORequest("1","123", new BigDecimal(100), LocalDateTime.now(), "Itau", "001", "001");
        Conta contaRemetente = new Conta(1L, "Teste", "1234", "teste@gmail.com", "123456", "9876", new BigDecimal(0));
        String mensagem = new Gson().toJson(pixSolicitacaoDTORequest);

        when(contaRepository.findByCpf(any())).thenReturn(Mono.just(contaRemetente));
        when(contaRepository.save(any())).thenReturn(Mono.just(contaRemetente));
        pixService.getSolicitacaoPix(mensagem);

        Assert.assertEquals(new BigDecimal(100),contaRemetente.getSaldo());
    }
}