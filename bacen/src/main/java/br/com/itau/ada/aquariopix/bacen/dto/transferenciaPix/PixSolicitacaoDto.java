package br.com.itau.ada.aquariopix.bacen.dto.transferenciaPix;

import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import br.com.itau.ada.aquariopix.bacen.model.PixTransferencia;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PixSolicitacaoDto {

    private String reqId;
    private String chave;
    private BigDecimal valor;
    private LocalDateTime dataHora;
    private String bancoRemetente;
    private String contaRemetente;
    private String agenciaRemetente;

    public PixTransferencia mapperToEntity(StatusSolicitacao status){
        return new PixTransferencia(this.reqId, this.chave, this.valor, this.dataHora, this.bancoRemetente, this.contaRemetente, this. agenciaRemetente, status);
    }

}
