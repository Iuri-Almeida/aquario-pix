package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.dto.ChavePixDto;
import br.com.itau.ada.aquariopix.bacen.dto.transferenciaPix.PixSolicitacaoDto;
import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import br.com.itau.ada.aquariopix.bacen.kafka.producer.BacenProducer;
import br.com.itau.ada.aquariopix.bacen.repository.PixTransferenciaRepository;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PixSolicitacaoService {

    private final ChavePixService chavePixService;

    private final BacenProducer producer;

    private final PixTransferenciaRepository pixTransferenciaRepository;

    public PixSolicitacaoService(ChavePixService chavePixService, BacenProducer producer, PixTransferenciaRepository pixTransferenciaRepository) {
        this.chavePixService = chavePixService;
        this.producer = producer;
        this.pixTransferenciaRepository = pixTransferenciaRepository;
    }

    public void enviarPix(PixSolicitacaoDto pixSolicitacaoDto) {
        Optional<ChavePixDto> chavePix = chavePixService.consultarChavePix(pixSolicitacaoDto.getChave());
        if (chavePix.isPresent()) {
            pixTransferenciaRepository.save(pixSolicitacaoDto.mapperToEntity(StatusSolicitacao.Pendente));
            enviarSolicitacao(chavePix.get().getBanco(), pixSolicitacaoDto);
        }
    }

    private void enviarSolicitacao(String banco, PixSolicitacaoDto pixSolicitacaoDto) {
        String topic;
        switch (banco) {
            case ("Itau"):
                producer.publish("pix-solicitacao-itau", pixSolicitacaoDto.getReqId(), new Gson().toJson(pixSolicitacaoDto));
            case ("Ada"):
                producer.publish("pix-solicitacao-ada", pixSolicitacaoDto.getReqId(), new Gson().toJson(pixSolicitacaoDto));
        }
    }
}
