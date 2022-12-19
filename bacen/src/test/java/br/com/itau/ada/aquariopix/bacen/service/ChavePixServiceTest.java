package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.dto.MensagemKafkaDto;
import br.com.itau.ada.aquariopix.bacen.dto.chavePix.ChavePixSolicitacaoDto;
import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import br.com.itau.ada.aquariopix.bacen.kafka.producer.BacenProducer;
import br.com.itau.ada.aquariopix.bacen.model.ContaBacen;
import br.com.itau.ada.aquariopix.bacen.repository.ChavePixRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.when;

class ChavePixServiceTest {

    @Mock
    private ChavePixRepository chavePixRepository;

    @Mock
    private BacenProducer producer;

    @Mock
    private ContaBacenService contaBacenService;

    @InjectMocks
    private ChavePixService chavePixService;

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void cadastrarItauChavePixTest(){
        ContaBacen contaBacenMock = new ContaBacen(
                1L,
                "Maria",
                "44809313840",
                "maria@gmail.com",
                "25119",
                "021",
                "Itau"
        );

        ChavePixSolicitacaoDto solicitacaoMock = new ChavePixSolicitacaoDto(
                "123",
                "44809313840",
                "CPF",
                "Itau",
                "021",
                "25119"
        );

        String mensagemEsperada = new Gson().toJson(solicitacaoMock.mapperToConfirmacaoDto(StatusSolicitacao.Aceito));
        when(contaBacenService.findByBancoContaAndAgencia(solicitacaoMock.getBanco(), solicitacaoMock.getConta(), solicitacaoMock.getAgencia())).thenReturn(Optional.of(contaBacenMock));

        MensagemKafkaDto mensagemEnviada = chavePixService.cadastrarChavePix(solicitacaoMock);

        Assertions.assertEquals("confirmacao-cadastro-chavepix-itau", mensagemEnviada.getTopic());
        Assertions.assertEquals(solicitacaoMock.getReqId(), mensagemEnviada.getKey());
        Assertions.assertEquals(mensagemEsperada, mensagemEnviada.getMessage());
    }

    @Test
    void cadastrarAdaChavePixTest(){
        ContaBacen contaBacenMock = new ContaBacen(
                1L,
                "Maria",
                "44809313840",
                "maria@gmail.com",
                "25120",
                "01",
                "Ada"
        );

        ChavePixSolicitacaoDto solicitacaoMock = new ChavePixSolicitacaoDto(
                "123",
                "44809313840",
                "CPF",
                "Ada",
                "01",
                "25120"
        );

        String mensagemEsperada = new Gson().toJson(solicitacaoMock.mapperToConfirmacaoDto(StatusSolicitacao.Aceito));
        when(contaBacenService.findByBancoContaAndAgencia(solicitacaoMock.getBanco(), solicitacaoMock.getConta(), solicitacaoMock.getAgencia())).thenReturn(Optional.of(contaBacenMock));

        MensagemKafkaDto mensagemEnviada = chavePixService.cadastrarChavePix(solicitacaoMock);

        Assertions.assertEquals("confirmacao-cadastro-chavepix-ada", mensagemEnviada.getTopic());
        Assertions.assertEquals(solicitacaoMock.getReqId(), mensagemEnviada.getKey());
        Assertions.assertEquals(mensagemEsperada, mensagemEnviada.getMessage());
    }

    @Test
    void chavePixJaExistenteTest(){
        ChavePixSolicitacaoDto solicitacaoMock = new ChavePixSolicitacaoDto(
                "123",
                "44809313840",
                "CPF",
                "Itau",
                "021",
                "25119"
        );

        String mensagemEsperada = new Gson().toJson(solicitacaoMock.mapperToConfirmacaoDto(StatusSolicitacao.Recusado));

        when(chavePixRepository.findById(solicitacaoMock.getTipo(), solicitacaoMock.getChave())).thenReturn(Optional.of(solicitacaoMock.mapperToEntity()));

        MensagemKafkaDto mensagemEnviada = chavePixService.cadastrarChavePix(solicitacaoMock);

        Assertions.assertEquals("confirmacao-cadastro-chavepix-itau", mensagemEnviada.getTopic());
        Assertions.assertEquals(solicitacaoMock.getReqId(), mensagemEnviada.getKey());
        Assertions.assertEquals(mensagemEsperada, mensagemEnviada.getMessage());
    }

    @Test
    void donoChaveInvalidoTest(){
        ContaBacen contaBacenMock = new ContaBacen(
                1L,
                "Maria",
                "12345678901",
                "maria@gmail.com",
                "25120",
                "01",
                "Ada"
        );

        ChavePixSolicitacaoDto solicitacaoMock = new ChavePixSolicitacaoDto(
                "123",
                "44809313840",
                "CPF",
                "Ada",
                "01",
                "25120"
        );

        String mensagemEsperada = new Gson().toJson(solicitacaoMock.mapperToConfirmacaoDto(StatusSolicitacao.Recusado));
        when(contaBacenService.findByBancoContaAndAgencia(solicitacaoMock.getBanco(), solicitacaoMock.getConta(), solicitacaoMock.getAgencia())).thenReturn(Optional.of(contaBacenMock));

        MensagemKafkaDto mensagemEnviada = chavePixService.cadastrarChavePix(solicitacaoMock);

        Assertions.assertEquals("confirmacao-cadastro-chavepix-ada", mensagemEnviada.getTopic());
        Assertions.assertEquals(solicitacaoMock.getReqId(), mensagemEnviada.getKey());
        Assertions.assertEquals(mensagemEsperada, mensagemEnviada.getMessage());
    }

    @Test
    void tipoChaveNaoEncontradoTest(){
        ContaBacen contaBacenMock = new ContaBacen(
                1L,
                "Maria",
                "44809313840",
                "maria@gmail.com",
                "25120",
                "01",
                "Ada"
        );

        ChavePixSolicitacaoDto solicitacaoMock = new ChavePixSolicitacaoDto(
                "123",
                "maria@gmail.com",
                "E-mail",
                "Ada",
                "01",
                "25120"
        );

        when(contaBacenService.findByBancoContaAndAgencia(solicitacaoMock.getBanco(), solicitacaoMock.getConta(), solicitacaoMock.getAgencia())).thenReturn(Optional.of(contaBacenMock));

        Assertions.assertThrows(RuntimeException.class, () -> chavePixService.cadastrarChavePix(solicitacaoMock), "Tipo de chave pix não encontrado");
    }

    @Test
    void bancoNaoEncontradoTest(){
        ContaBacen contaBacenMock = new ContaBacen(
                1L,
                "Maria",
                "44809313840",
                "maria@gmail.com",
                "25120",
                "01",
                "itau"
        );

        ChavePixSolicitacaoDto solicitacaoMock = new ChavePixSolicitacaoDto(
                "123",
                "maria@gmail.com",
                "E-mail",
                "itau",
                "01",
                "25120"
        );

        when(contaBacenService.findByBancoContaAndAgencia(solicitacaoMock.getBanco(), solicitacaoMock.getConta(), solicitacaoMock.getAgencia())).thenReturn(Optional.of(contaBacenMock));

        Assertions.assertThrows(RuntimeException.class, () -> chavePixService.cadastrarChavePix(solicitacaoMock), "Banco não cadastrado");
    }

    @Test
    void contaNaoEncontradaTest(){
        ChavePixSolicitacaoDto solicitacaoMock = new ChavePixSolicitacaoDto(
                "123",
                "maria@gmail.com",
                "E-mail",
                "itau",
                "01",
                "25120"
        );

        Assertions.assertThrows(RuntimeException.class, () -> chavePixService.cadastrarChavePix(solicitacaoMock), "Conta não cadastrada");
    }

}