package com.letscode.itau.bancoada.model;

import lombok.Getter;
//import org.hibernate.validator.constraints.br.CPF;

@Getter
public class Requerente {
    private String conta;
    private String agencia;
    private String cpf;
}
