package com.letscode.itau.bancoada.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PixDTORequest {

    private final String reqId = String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
    private String chave;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private BigDecimal valor;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final LocalDateTime dataHora = LocalDateTime.now();
    private String contaRemetente;
    private String agenciaRemetente;
    private final String bancoRemetente = "Ada";

}
