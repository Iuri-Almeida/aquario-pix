package com.letscode.itau.bancoada.dto;

import com.letscode.itau.bancoada.enumeration.TipoChavePix;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CadastroBacenDTORequest {
    private Long reqId;
    private String chave;
    private TipoChavePix tipo;
    private final String banco = "Ada";
    private String agencia;
    private String conta;
}
