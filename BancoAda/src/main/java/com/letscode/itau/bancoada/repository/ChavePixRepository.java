package com.letscode.itau.bancoada.repository;

import com.letscode.itau.bancoada.model.ChavePix;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ChavePixRepository extends R2dbcRepository<ChavePix, Long> {
    Mono<ChavePix> findByChave(String chave);
}
