package com.letscode.itau.bancoitau.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
@ToString
public class PixDTORequest {
    private String chave;
    private BigDecimal valor;
    private final LocalDateTime data = LocalDateTime.now();
    private String contaRemetente;
    private String agenciaRemetente;
}
