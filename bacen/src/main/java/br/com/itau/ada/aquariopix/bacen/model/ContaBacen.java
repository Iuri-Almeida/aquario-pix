package br.com.itau.ada.aquariopix.bacen.model;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ContaBacen {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @NotNull
    private String nome;
    @NotNull
    private String cpf;
    @NotNull
    private String email;
    @NotNull
    private Long numeroConta;
    @NotNull
    private Long agencia;
    @NotNull
    private String banco;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
