package com.letscode.itau.bancoada.model;

import com.letscode.itau.bancoada.enumeration.Status;
import com.letscode.itau.bancoada.enumeration.TipoChavePix;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PIX")
public class ChavePix {
    @Id
    private Long id;
    @Column("reqId")
    private Long reqId;
    @Column("tipo")
    private TipoChavePix tipoDeChave;
    private String agencia;
    @Column("conta")
    private String numeroConta;
    private String chave;
    @Column("status")
    private Status status = Status.Pendente;

    public ChavePix(Long reqId, TipoChavePix tipoDeChave, String agencia, String numeroConta, String chave, Status status) {
        this.reqId = reqId;
        this.tipoDeChave = tipoDeChave;
        this.agencia = agencia;
        this.numeroConta = numeroConta;
        this.chave = chave;
        this.status = status;
    }
}
