package com.letscode.itau.bancoitau.dto;

import com.letscode.itau.bancoitau.enumeration.Status;
import com.letscode.itau.bancoitau.model.PixTransferencia;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PixSolicitacaoDTORequest {

    private String reqId;
    private String chave;
    private BigDecimal valor;
    private LocalDateTime dataHora;
    private String bancoRemetente;
    private String contaRemetente;
    private String agenciaRemetente;

    public PixTransferencia mapperToEntity(Status status) {
        return new PixTransferencia(this.reqId, this.chave, this.valor, this.dataHora, this.bancoRemetente, this.contaRemetente, this.agenciaRemetente, status);
    }

}
