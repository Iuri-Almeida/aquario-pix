package com.letscode.itau.bancoitau.repository;

import com.letscode.itau.bancoitau.model.Conta;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ContaRepository extends R2dbcRepository<Conta, Long> {
}
