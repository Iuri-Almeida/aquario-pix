package com.letscode.itau.bancoitau.dto;

import com.letscode.itau.bancoitau.enumeration.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PixDTOResponse {
    private Status status;
    private String reqId;
}
