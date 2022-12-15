package com.letscode.itau.bancoada.dto;

import com.letscode.itau.bancoada.enumeration.Status;
import com.letscode.itau.bancoada.enumeration.TipoChavePix;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CadastroBacenDTOResponse {
    private Long reqId;
    private String chave;
    private TipoChavePix tipo;
    private final String banco;
    private String agencia;
    private String conta;
    private Status status;
}
