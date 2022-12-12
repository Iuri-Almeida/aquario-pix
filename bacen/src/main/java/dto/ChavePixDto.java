package dto;

import br.com.itau.ada.aquariopix.bacen.model.ChavePix;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChavePixDto {

    private String chave;

    private String tipo;

    private String banco;

    private String agencia;

    private String conta;


    public ChavePix mapperToEntity() {
        return new ChavePix(this.chave, this.tipo, this.banco, this.agencia, this.conta);
    }
}
