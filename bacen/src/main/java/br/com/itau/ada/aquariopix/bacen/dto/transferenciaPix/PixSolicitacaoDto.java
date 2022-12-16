package br.com.itau.ada.aquariopix.bacen.dto.transferenciaPix;

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
    private String contaRemetente;
    private String agenciaRemetente;

}
