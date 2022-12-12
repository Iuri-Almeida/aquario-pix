package com.letscode.itau.bancoitau.model;

import lombok.Getter;
import lombok.ToString;
//import org.hibernate.validator.constraints.br.CPF;

@Getter
public class Requerente {
    private Long conta;
    private Long agencia;
    private Long cpf;
}
