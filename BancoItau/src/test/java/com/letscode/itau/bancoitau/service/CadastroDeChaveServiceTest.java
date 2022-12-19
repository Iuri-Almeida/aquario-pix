package com.letscode.itau.bancoitau.service;

import com.google.gson.Gson;
import com.letscode.itau.bancoitau.dto.CadastroBacenDTOResponse;
import com.letscode.itau.bancoitau.dto.ChavePixDTO;
import com.letscode.itau.bancoitau.enumeration.Status;
import com.letscode.itau.bancoitau.enumeration.TipoChavePix;
import com.letscode.itau.bancoitau.model.ChavePix;
import com.letscode.itau.bancoitau.model.Conta;
import com.letscode.itau.bancoitau.model.Requerente;
import com.letscode.itau.bancoitau.repository.ChavePixRepository;
import com.letscode.itau.bancoitau.repository.ContaRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CadastroDeChaveServiceTest {
    @InjectMocks
    public CadastroDeChaveService cadastroDeChaveService;
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private ChavePixRepository pixRepository;
    @Mock
    private ContaRepository contaRepository;

    @Before
    public void setup() {
        cadastroDeChaveService.setPixRepository(pixRepository);
        cadastroDeChaveService.setContaRepository(contaRepository);
    }

    @Test
    public void cadastrarChavePixTest() {
        ChavePixDTO chavePixDTO = new ChavePixDTO(1l, TipoChavePix.CPF, new Requerente("001", "001", "123"));

        Conta conta = new Conta(1L, "Teste", "123", "oi@gmail", "001", "001", new BigDecimal(0));

        when(pixRepository.save(any())).thenReturn(Mono.just(new ChavePix(chavePixDTO.getReqId(), chavePixDTO.getTipoDeChave(), chavePixDTO.getRequerente().getAgencia(), chavePixDTO.getRequerente().getConta(), chavePixDTO.getRequerente().getCpf(), Status.Pendente)));


        cadastroDeChaveService.cadastrarChavePix(chavePixDTO).subscribe(
                chavePixResponseEntity -> {
                    Assert.assertEquals(201, chavePixResponseEntity.getStatusCodeValue());
                    Assert.assertEquals("123", chavePixResponseEntity.getBody().getChave());
                }
        );
    }

    @Test
    public void getStatusBacenTestAceito() {
        ChavePix chavePix = new ChavePix();
        CadastroBacenDTOResponse cadastroBacenDTOResponse = new CadastroBacenDTOResponse(1L, "123", TipoChavePix.CPF, "Itau", "001", "001", Status.Aceito);

        when(pixRepository.findByChave("123")).thenReturn(Mono.just(chavePix));
        when(pixRepository.save(chavePix)).thenReturn(Mono.just(chavePix));


        cadastroDeChaveService.getStatusBacen(new Gson().toJson(cadastroBacenDTOResponse));

        Assert.assertEquals(Status.Aceito,chavePix.getStatus());


    }

    @Test
    public void getStatusBacenTestRecusado() {
        ChavePix chavePix = new ChavePix();
        CadastroBacenDTOResponse cadastroBacenDTOResponse = new CadastroBacenDTOResponse(1L, "123", TipoChavePix.CPF, "Itau", "001", "001", Status.Recusado);

        when(pixRepository.findByChave("123")).thenReturn(Mono.just(chavePix));
        when(pixRepository.save(chavePix)).thenReturn(Mono.just(chavePix));


        cadastroDeChaveService.getStatusBacen(new Gson().toJson(cadastroBacenDTOResponse));

        Assert.assertEquals(Status.Recusado,chavePix.getStatus());


    }
}

