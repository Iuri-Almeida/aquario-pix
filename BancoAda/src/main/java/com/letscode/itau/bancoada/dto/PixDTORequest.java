package com.letscode.itau.bancoada.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.letscode.itau.bancoada.enumeration.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PixDTORequest {

    private String chave;
    private String contaRemetente;
    private String agenciaRemetente;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private BigDecimal valor;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final LocalDateTime data = LocalDateTime.now();
    private Status status = Status.Pendente;

}
