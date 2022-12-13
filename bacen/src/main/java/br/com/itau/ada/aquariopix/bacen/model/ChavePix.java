package br.com.itau.ada.aquariopix.bacen.model;

import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import com.sun.istack.NotNull;
import br.com.itau.ada.aquariopix.bacen.dto.ChavePixConfirmacaoDto;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChavePix {
    @Id
    @Column(name = "chave", nullable = false)
    private String chave;

    @NotNull
    private String tipo;

    @NotNull
    private String banco;

    @NotNull
    private String agencia;

    @NotNull
    private String conta;

}
