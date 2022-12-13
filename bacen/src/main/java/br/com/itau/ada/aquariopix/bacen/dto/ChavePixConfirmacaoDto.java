package br.com.itau.ada.aquariopix.bacen.dto;

import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacoes;
import br.com.itau.ada.aquariopix.bacen.model.ChavePix;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChavePixConfirmacaoDto {

    private String reqId;

    private String chave;

    private String tipo;

    private String banco;

    private String agencia;

    private String conta;

    private StatusSolicitacoes status;

    public ChavePix mapperToEntity() {
        return new ChavePix(this.chave, this.tipo, this.banco, this.agencia, this.conta);
    }

    public void setStatus(StatusSolicitacoes status) {
        this.status = status;
    }
}
