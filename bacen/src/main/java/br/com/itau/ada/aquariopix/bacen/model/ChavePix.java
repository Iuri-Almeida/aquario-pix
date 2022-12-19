package br.com.itau.ada.aquariopix.bacen.model;

import br.com.itau.ada.aquariopix.bacen.dto.chavePix.ChavePixDto;
import com.sun.istack.NotNull;
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

    public ChavePixDto mapperToChavePixDto() {
        return new ChavePixDto(this.chave, this.tipo, this.banco, this.agencia, this.conta);
    }
}
