package com.letscode.itau.bancoitau.dto;

import com.letscode.itau.bancoitau.enumeration.TipoChavePix;
import com.letscode.itau.bancoitau.model.Requerente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChavePixDTO {
    private Long reqId;
    private TipoChavePix tipoDeChave;
    private Requerente requerente;

}
