package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.dto.MensagemKafkaDto;
import br.com.itau.ada.aquariopix.bacen.dto.chavePix.ChavePixDto;
import br.com.itau.ada.aquariopix.bacen.dto.transferenciaPix.PixSolicitacaoDto;
import br.com.itau.ada.aquariopix.bacen.kafka.producer.BacenProducer;
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

import static org.mockito.Mockito.when;

class PixSolicitacaoServiceTest {

    @Mock
    private PixTransferenciaRepository pixTransferenciaRepository;

    @Mock
    private BacenProducer producer;

    @Mock
    private ChavePixService chavePixService;

    @InjectMocks
    private PixSolicitacaoService pixSolicitacaoService;

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void pixSolicitacaoItauTest() {
        ChavePixDto chavePixMock = new ChavePixDto(
                "44809313840",
                "CPF",
                "Itau",
                "021",
                "25119"

        );

        PixSolicitacaoDto solicitacaoMock = new PixSolicitacaoDto(
                "123",
                "44809313840",
                new BigDecimal(50.00),
                LocalDateTime.now(),
                "Ada",
                "001",
                "25120"
        );

        String mensagemEsperada = new Gson().toJson(solicitacaoMock);
        when(chavePixService.consultarChavePix(solicitacaoMock.getChave())).thenReturn(chavePixMock);

        MensagemKafkaDto mensagemEnviada = pixSolicitacaoService.enviarPix(solicitacaoMock);

        Assertions.assertEquals("pix-solicitacao-itau", mensagemEnviada.getTopic());
        Assertions.assertEquals(solicitacaoMock.getReqId(), mensagemEnviada.getKey());
        Assertions.assertEquals(mensagemEsperada, mensagemEnviada.getMessage());
    }

    @Test
    void pixSolicitacaoAdaTest() {
        ChavePixDto chavePixMock = new ChavePixDto(
                "44809313840",
                "CPF",
                "Ada",
                "001",
                "25120"
        );

        PixSolicitacaoDto solicitacaoMock = new PixSolicitacaoDto(
                "123",
                "44809313840",
                new BigDecimal(50.00),
                LocalDateTime.now(),
                "Itau",
                "021",
                "25119"
        );

        when(chavePixService.consultarChavePix(solicitacaoMock.getChave())).thenReturn(chavePixMock);

        MensagemKafkaDto mensagemEnviada = pixSolicitacaoService.enviarPix(solicitacaoMock);

        String mensagemEsperada = new Gson().toJson(solicitacaoMock);

        Assertions.assertEquals("pix-solicitacao-ada", mensagemEnviada.getTopic());
        Assertions.assertEquals(solicitacaoMock.getReqId(), mensagemEnviada.getKey());
        Assertions.assertEquals(mensagemEsperada, mensagemEnviada.getMessage());
    }

    @Test
    void chavePixNaoEncontradaTest() {
        PixSolicitacaoDto solicitacaoMock = new PixSolicitacaoDto(
                "123",
                "44809313840",
                new BigDecimal(50.00),
                LocalDateTime.now(),
                "Itau",
                "021",
                "25119"
        );

        Assertions.assertThrows(RuntimeException.class, () -> pixSolicitacaoService.enviarPix(solicitacaoMock), "Chave não encontrada");
    }

    @Test
    void bancoNaoEncontradoTest() {
        ChavePixDto chavePixMock = new ChavePixDto(
                "44809313840",
                "CPF",
                "ada",
                "001",
                "25120"
        );

        PixSolicitacaoDto solicitacaoMock = new PixSolicitacaoDto(
                "123",
                "44809313840",
                new BigDecimal(50.00),
                LocalDateTime.now(),
                "Itau",
                "021",
                "25119"
        );

        when(chavePixService.consultarChavePix(solicitacaoMock.getChave())).thenReturn(chavePixMock);

        Assertions.assertThrows(RuntimeException.class, () -> pixSolicitacaoService.enviarPix(solicitacaoMock), "Banco não encontrado");
    }
}