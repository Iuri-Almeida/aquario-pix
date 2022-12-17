package com.letscode.itau.bancoitau.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Getter
@AllArgsConstructor
@ToString
public class PixDTORequest {
    private final String reqId = String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
    private String chave;
    private BigDecimal valor;
    private final LocalDateTime data = LocalDateTime.now();
    private String contaRemetente;
    private String agenciaRemetente;
    private final String bancoRemetente = "Itau";
}
