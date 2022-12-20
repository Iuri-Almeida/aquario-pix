package com.letscode.itau.bancoada.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
//import org.hibernate.validator.constraints.br.CPF;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Requerente {
    private String conta;
    private String agencia;
    private String cpf;
}
