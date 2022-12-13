package br.com.itau.ada.aquariopix.bacen.dto;

import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
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

    private StatusSolicitacao status;

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }
}
