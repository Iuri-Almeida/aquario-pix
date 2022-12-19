package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.dto.MensagemKafkaDto;
import br.com.itau.ada.aquariopix.bacen.dto.chavePix.ChavePixDto;
import br.com.itau.ada.aquariopix.bacen.dto.transferenciaPix.PixConfirmacaoDto;
import br.com.itau.ada.aquariopix.bacen.dto.transferenciaPix.PixSolicitacaoDto;
import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import br.com.itau.ada.aquariopix.bacen.kafka.producer.BacenProducer;
import br.com.itau.ada.aquariopix.bacen.model.PixTransferencia;
import br.com.itau.ada.aquariopix.bacen.repository.PixTransferenciaRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PixConfirmacaoServiceTest {

    @Mock
    private PixTransferenciaRepository pixTransferenciaRepository;

    @Mock
    private BacenProducer producer;

    @InjectMocks
    private PixConfirmacaoService pixConfirmacaoService;

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void pixConfirmacaoAdaTest(){
        PixConfirmacaoDto pixConfirmacaoMock = new PixConfirmacaoDto(
                "123",
                StatusSolicitacao.Pendente
        );

        PixTransferencia pixTransferenciaMock = new PixTransferencia(
                "123",
                "44809313840",
                new BigDecimal(50.00),
                LocalDateTime.now(),
                "Ada",
                "001",
                "25120",
                StatusSolicitacao.Pendente
        );

        String mensagemEsperada = new Gson().toJson(pixConfirmacaoMock);
        when(pixTransferenciaRepository.findById(pixTransferenciaMock.getReqId())).thenReturn(Optional.of(pixTransferenciaMock));

        MensagemKafkaDto mensagemEnviada = pixConfirmacaoService.confirmarPixParaRemetente(pixConfirmacaoMock);

        assertEquals("pix-confirmacao-ada", mensagemEnviada.getTopic());
        Assertions.assertEquals(pixConfirmacaoMock.getReqId(), mensagemEnviada.getKey());
        assertEquals(mensagemEsperada, mensagemEnviada.getMessage());
    }

    @Test
    void pixConfirmacaoItauTest(){
        PixConfirmacaoDto pixConfirmacaoMock = new PixConfirmacaoDto(
                "123",
                StatusSolicitacao.Pendente
        );

        PixTransferencia pixTransferenciaMock = new PixTransferencia(
                "123",
                "44809313840",
                new BigDecimal(50.00),
                LocalDateTime.now(),
                "Itau",
                "021",
                "25119",
                StatusSolicitacao.Pendente
        );

        String mensagemEsperada = new Gson().toJson(pixConfirmacaoMock);
        when(pixTransferenciaRepository.findById(pixTransferenciaMock.getReqId())).thenReturn(Optional.of(pixTransferenciaMock));

        MensagemKafkaDto mensagemEnviada = pixConfirmacaoService.confirmarPixParaRemetente(pixConfirmacaoMock);

        assertEquals("pix-confirmacao-itau", mensagemEnviada.getTopic());
        Assertions.assertEquals(pixConfirmacaoMock.getReqId(), mensagemEnviada.getKey());
        assertEquals(mensagemEsperada, mensagemEnviada.getMessage());
    }

}