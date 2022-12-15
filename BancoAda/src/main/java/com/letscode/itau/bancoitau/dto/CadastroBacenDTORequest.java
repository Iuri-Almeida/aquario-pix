package com.letscode.itau.bancoitau.dto;

import com.letscode.itau.bancoitau.enumeration.TipoChavePix;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CadastroBacenDTORequest {
    private Long reqId;
    private String chave;
    private TipoChavePix tipo;
    private final String banco = "Itau";
    private String agencia;
    private String conta;
}
