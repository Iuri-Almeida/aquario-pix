package com.letscode.itau.bancoitau.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Conta {

    @Id
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private Long numeroConta;
    private Long agencia;

}
