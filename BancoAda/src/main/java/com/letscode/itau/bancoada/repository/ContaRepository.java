package com.letscode.itau.bancoada.repository;

import com.letscode.itau.bancoada.model.Conta;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ContaRepository extends R2dbcRepository<Conta, Long> {

    Mono<Conta> findByNumeroContaAndAgencia(String numeroConta, String agencia);

}
