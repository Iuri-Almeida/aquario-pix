package br.com.itau.ada.aquariopix.bacen.model;

import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Entity
@NoArgsConstructor
public class PixTransferencia {

    @Id
    @NotNull
    private String reqId;

    @NotNull
    private String chave;

    @NotNull
    private BigDecimal valor;

    @NotNull
    private LocalDateTime dataHora;

    @NotNull
    private String bancoRemetente;

    @NotNull
    private String contaRemetente;

    @NotNull
    private String agenciaRemetente;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status;

    public void setStatus(StatusSolicitacao status) {
        this.status = status;
    }

}
