package com.letscode.itau.bancoitau.repository;

import com.letscode.itau.bancoitau.model.ChavePix;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ChavePixRepository extends R2dbcRepository<ChavePix, Long> {
    Mono<ChavePix> findByChave(String chave);
}
