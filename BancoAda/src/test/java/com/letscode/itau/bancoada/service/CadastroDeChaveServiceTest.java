package com.letscode.itau.bancoada.service;

import com.letscode.itau.bancoada.dto.*;
import com.letscode.itau.bancoada.enumeration.Status;
import com.letscode.itau.bancoada.enumeration.TipoChavePix;
import com.letscode.itau.bancoada.kafka.producer.AdaKafkaProducer;
import com.letscode.itau.bancoada.model.ChavePix;
import com.letscode.itau.bancoada.model.Conta;
import com.letscode.itau.bancoada.model.Requerente;
import com.letscode.itau.bancoada.repository.ChavePixRepository;
import com.letscode.itau.bancoada.repository.ContaRepository;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CadastroDeChaveServiceTest {

    @Mock
    private AdaKafkaProducer adaKafkaProducer;
    @Mock
    private ContaRepository contaRepository;
    @Mock
    private ChavePixRepository chavePixRepository;
    @InjectMocks
    private CadastroDeChaveService cadastroDeChaveService;

    private ChavePixDTO getChavePixDTO() {
        return new ChavePixDTO(1L, TipoChavePix.CPF, new Requerente("001", "001", "123"));
    }

    private Conta getConta(BigDecimal saldo) {
        return new Conta(1L, "Iuri", "12345678910", "iuri@itau.com.br", "2654", "021", saldo);
    }

    private CadastroBacenDTOResponse getCadastroBacenDTOResponse(Status status) {
        return new CadastroBacenDTOResponse(1L, "12345678910", TipoChavePix.CPF, "Itau", "001", "001", status);
    }

    @Test
    @DisplayName("Find all test.")
    public void findAllTest() {
        cadastroDeChaveService.findAll();

        Mockito.verify(chavePixRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Find by chave test.")
    public void findByChaveTest() {
        when(chavePixRepository.findByChave(anyString())).thenReturn(Mono.just(new ChavePix()));

        cadastroDeChaveService.findByChave("1234").subscribe(conta -> Mockito.verify(chavePixRepository, times(1)).findByChave(anyString()));
    }


    @Test
    @DisplayName("Deve cadastrar uma chave pix.")
    public void cadastrarChavePixTest() {
        ChavePixDTO chavePixDTO = this.getChavePixDTO();

        Conta conta = this.getConta(new BigDecimal(0));

        when(contaRepository.findByNumeroContaAndAgencia(any(), any())).thenReturn(Mono.just(conta));
        when(chavePixRepository.save(any())).thenReturn(Mono.just(new ChavePix(chavePixDTO.getReqId(), chavePixDTO.getTipoDeChave(), chavePixDTO.getRequerente().getAgencia(), chavePixDTO.getRequerente().getConta(), chavePixDTO.getRequerente().getCpf(), Status.Pendente)));

        cadastroDeChaveService.cadastrarChavePix(chavePixDTO).subscribe(
                chavePixResponseEntity -> {
                    Assert.assertEquals(201, chavePixResponseEntity.getStatusCodeValue());
                    Assert.assertEquals("123", Objects.requireNonNull(chavePixResponseEntity.getBody()).getChave());
                }
        );
    }

    @Test
    @DisplayName("Deve atualizar o status do cadastro da chave para aceito.")
    public void getStatusBacenTestAceito() {
        ChavePix chavePix = new ChavePix();
        CadastroBacenDTOResponse cadastroBacenDTOResponse = this.getCadastroBacenDTOResponse(Status.Aceito);

        when(chavePixRepository.findByChave(cadastroBacenDTOResponse.getChave())).thenReturn(Mono.just(chavePix));
        when(chavePixRepository.save(chavePix)).thenReturn(Mono.just(chavePix));

        cadastroDeChaveService.getStatusBacen(cadastroBacenDTOResponse);

        Assert.assertEquals(Status.Aceito, chavePix.getStatus());


    }

    @Test
    @DisplayName("Deve atualizar o status do cadastro da chave para recusado.")
    public void getStatusBacenTestRecusado() {
        ChavePix chavePix = new ChavePix();
        CadastroBacenDTOResponse cadastroBacenDTOResponse = this.getCadastroBacenDTOResponse(Status.Recusado);

        when(chavePixRepository.findByChave(cadastroBacenDTOResponse.getChave())).thenReturn(Mono.just(chavePix));
        when(chavePixRepository.save(chavePix)).thenReturn(Mono.just(chavePix));

        cadastroDeChaveService.getStatusBacen(cadastroBacenDTOResponse);

        Assert.assertEquals(Status.Recusado, chavePix.getStatus());
    }

}