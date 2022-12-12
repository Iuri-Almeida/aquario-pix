package br.com.itau.ada.aquariopix.bacen.model;

import com.sun.istack.NotNull;
import dto.ChavePixDto;
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
    private int agencia;

    @NotNull
    private int conta;

    public ChavePixDto mapperToDto() {
        return new ChavePixDto(this.chave, this.tipo, this.banco, this.agencia, this.conta);
    }

}
