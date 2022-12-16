package br.com.itau.ada.aquariopix.bacen.model;

import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Entity
@NoArgsConstructor
public class PixTransferencia {

    @Id
    private String reqId;
    private String chave;
    private BigDecimal valor;
    private LocalDateTime dataHora;
    private String bancoRemetente;
    private String contaRemetente;
    private String agenciaRemetente;
    private StatusSolicitacao status;

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

}
