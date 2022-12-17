package com.letscode.itau.bancoada.dto;

import com.letscode.itau.bancoada.enumeration.TipoChavePix;
import com.letscode.itau.bancoada.model.Requerente;
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
