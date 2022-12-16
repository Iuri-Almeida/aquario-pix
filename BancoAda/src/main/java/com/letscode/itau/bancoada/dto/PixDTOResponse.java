package com.letscode.itau.bancoada.dto;

import com.letscode.itau.bancoada.enumeration.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PixDTOResponse {

    private Status status;
    private String contaRemetente;
    private String agenciaRemetente;
    private BigDecimal valor;

}
