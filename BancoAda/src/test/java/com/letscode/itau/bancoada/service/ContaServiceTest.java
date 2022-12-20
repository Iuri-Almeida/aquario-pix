package com.letscode.itau.bancoada.service;

import com.letscode.itau.bancoada.kafka.producer.AdaKafkaProducer;
import com.letscode.itau.bancoada.model.Conta;
import com.letscode.itau.bancoada.repository.ContaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
public class ContaServiceTest {

    @InjectMocks
    private ContaService contaService;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private AdaKafkaProducer adaKafkaProducer;

    private Conta getConta(BigDecimal saldo) {
        return new Conta(1L, "Iuri", "12345678910", "iuri@itau.com.br", "2654", "021", saldo);
    }

    @Test
    @DisplayName("Find all test.")
    public void findAllTest() {
        contaService.findAll();

        Mockito.verify(contaRepository, Mockito.times(1)).findAll();
    }

    @Test
    @DisplayName("Find by id test.")
    public void findByIdTest() {
        Mockito.when(contaRepository.findById(Mockito.any(Long.class))).thenReturn(Mono.just(new Conta()));

        contaService.findById(1L).subscribe(conta -> Mockito.verify(contaRepository, Mockito.times(1)).findById(Mockito.any(Long.class)));
    }

    @Test
    @DisplayName("Find by numero conta and agencia test.")
    public void findByNumeroContaAndAgenciaTest() {
        Mockito.when(contaRepository.findByNumeroContaAndAgencia(Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.just(new Conta()));

        contaService.findByNumeroContaAndAgencia("1234", "021").subscribe(conta -> Mockito.verify(contaRepository, Mockito.times(1)).findByNumeroContaAndAgencia(Mockito.anyString(), Mockito.anyString()));
    }

    @Test
    @DisplayName("Insert test.")
    public void insertTest() {
        Conta conta = this.getConta(new BigDecimal(0));

        Mockito.when(contaRepository.save(Mockito.any(Conta.class))).thenReturn(Mono.just(conta));

        contaService.insert(conta).subscribe(conta1 -> Mockito.verify(contaRepository, Mockito.times(1)).save(Mockito.any()));
    }

    @Test
    @DisplayName("Update test.")
    public void updateTest() {
        Conta conta = this.getConta(new BigDecimal(0));
        Conta conta1 = this.getConta(new BigDecimal(0));
        conta1.setEmail("joaquim@gmail.com");

        Mockito.when(contaRepository.save(Mockito.any())).thenReturn(Mono.just(conta));
        Mockito.when(contaRepository.findById(Mockito.any(Long.class))).thenReturn(Mono.just(conta));

        contaService.insert(conta).subscribe(conta2 -> {
            Mockito.verify(contaRepository, Mockito.times(1)).save(Mockito.any());

            contaService.update(Objects.requireNonNull(conta2.getBody()).getId(), conta1).subscribe(conta3 -> {
                Mockito.verify(contaRepository, Mockito.times(1)).findById(Mockito.any(Long.class));
                Mockito.verify(contaRepository, Mockito.times(2)).save(Mockito.any());
            });
        });
    }

    @Test
    @DisplayName("Delete test.")
    public void deleteTest() {
        Conta conta = this.getConta(new BigDecimal(0));

        Mockito.when(contaRepository.save(Mockito.any())).thenReturn(Mono.just(conta));
        Mockito.when(contaRepository.findById(Mockito.any(Long.class))).thenReturn(Mono.just(conta));
        Mockito.when(contaRepository.delete(Mockito.any())).thenReturn(Mono.just(Void.class).then());

        contaService.insert(conta).subscribe(conta2 -> {
            Mockito.verify(contaRepository, Mockito.times(1)).save(Mockito.any());

            contaService.delete(Objects.requireNonNull(conta2.getBody()).getId()).subscribe(conta3 -> {
                Mockito.verify(contaRepository, Mockito.times(1)).findById(Mockito.any(Long.class));
                Mockito.verify(contaRepository, Mockito.times(1)).delete(Mockito.any());
            });
        });
    }

}