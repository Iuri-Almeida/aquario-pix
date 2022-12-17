package com.letscode.itau.bancoada.model;

import com.letscode.itau.bancoada.enumeration.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transferencias")
public class PixTransferencia {

    @Column("reqId")
    private String reqId;
    private String chave;
    private BigDecimal valor;
    @Column("dataHora")
    private LocalDateTime dataHora;
    @Column("bancoRemetente")
    private String bancoRemetente;
    @Column("contaRemetente")
    private String contaRemetente;
    @Column("agenciaRemetente")
    private String agenciaRemetente;
    private Status status = Status.Pendente;

    public PixTransferencia(String reqId, String chave, BigDecimal valor, LocalDateTime dataHora, String contaRemetente, String agenciaRemetente) {
        this.reqId = reqId;
        this.chave = chave;
        this.valor = valor;
        this.dataHora = dataHora;
        this.contaRemetente = contaRemetente;
        this.agenciaRemetente = agenciaRemetente;
    }
}

