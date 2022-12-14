package br.com.itau.ada.aquariopix.bacen.dto;

import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import br.com.itau.ada.aquariopix.bacen.model.ChavePix;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChavePixSolicitacaoDto {

    private String reqId;
    private String chave;

    private String tipo;

    private String banco;

    private String agencia;

    private String conta;


    public ChavePix mapperToEntity() {
        return new ChavePix(this.chave, this.tipo, this.banco, this.agencia, this.conta);
    }

    public ChavePixDto mapperToChavePixDto() {
        return new ChavePixDto(this.chave, this.tipo, this.banco, this.agencia, this.conta);
    }

    public ChavePixConfirmacaoDto mapperToConfirmacaoDto(StatusSolicitacao status) {
        return new ChavePixConfirmacaoDto(this.reqId, this.chave, this.tipo, this.banco, this.agencia, this.conta, status);
    }
}
