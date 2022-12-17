package com.letscode.itau.bancoada.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("CONTA")
public class Conta {

    @Id
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    @Column("numeroConta")
    private String numeroConta;
    private String agencia;
    private BigDecimal saldo;

}
