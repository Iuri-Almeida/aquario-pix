package br.com.itau.ada.aquariopix.bacen.model;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ContaBacen {
    @Id
    @NotNull
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
}
