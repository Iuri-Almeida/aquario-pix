package com.letscode.itau.bancoada.repository;

import com.letscode.itau.bancoada.model.PixTransferencia;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface TransferenciaRepository extends R2dbcRepository<PixTransferencia, String> {

    Mono<PixTransferencia> findByReqId(String reqId);

}
