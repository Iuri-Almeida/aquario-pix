package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.dto.ChavePixDto;
import br.com.itau.ada.aquariopix.bacen.dto.transferenciaPix.PixSolicitacaoDto;
import br.com.itau.ada.aquariopix.bacen.kafka.producer.BacenProducer;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PixSolicitacaoService {

    private final ChavePixService chavePixService;

    private final BacenProducer producer;

    public PixSolicitacaoService(ChavePixService chavePixService, BacenProducer producer) {
        this.chavePixService = chavePixService;
        this.producer = producer;
    }

    public void enviarPix(PixSolicitacaoDto pixSolicitacaoDto) {
        Optional<ChavePixDto> chavePix = chavePixService.consultarChavePix(pixSolicitacaoDto.getChave());
        if (chavePix.isPresent()) {
            enviarSolicitacao(chavePix.get().getBanco(), pixSolicitacaoDto);
        }
    }

    private void enviarSolicitacao(String banco, PixSolicitacaoDto pixSolicitacaoDto) {

        switch (banco) {
            case ("Itau"):
                producer.publish("ada-pix-solicitacao", pixSolicitacaoDto.getReqId()+banco, pixSolicitacaoDto.toString());
        }

    }
}
