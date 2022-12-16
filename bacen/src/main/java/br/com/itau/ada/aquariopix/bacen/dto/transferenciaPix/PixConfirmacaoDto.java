package br.com.itau.ada.aquariopix.bacen.dto.transferenciaPix;

import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PixConfirmacaoDto {

    private String reqId;
    private StatusSolicitacao status;
}
