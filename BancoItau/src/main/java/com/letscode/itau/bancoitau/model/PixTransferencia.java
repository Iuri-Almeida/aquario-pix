package com.letscode.itau.bancoitau.model;

import com.letscode.itau.bancoitau.enumeration.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transferenciasItau")
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

    public PixTransferencia(String reqId, String chave, BigDecimal valor, LocalDateTime dataHora, String bancoRemetente, String contaRemetente, String agenciaRemetente) {
        this.reqId = reqId;
        this.chave = chave;
        this.valor = valor;
        this.dataHora = dataHora;
        this.bancoRemetente = bancoRemetente;
        this.contaRemetente = contaRemetente;
        this.agenciaRemetente = agenciaRemetente;
    }
}
