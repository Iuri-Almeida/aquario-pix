package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.dto.transferenciaPix.PixConfirmacaoDto;
import br.com.itau.ada.aquariopix.bacen.kafka.producer.BacenProducer;
import br.com.itau.ada.aquariopix.bacen.model.PixTransferencia;
import br.com.itau.ada.aquariopix.bacen.repository.PixTransferenciaRepository;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PixConfirmacaoService {

    private final PixTransferenciaRepository pixTransferenciaRepository;

    private final BacenProducer producer;

    private final ChavePixService chavePixService;

    public PixConfirmacaoService(PixTransferenciaRepository pixTransferenciaRepository, BacenProducer producer, ChavePixService chavePixService) {
        this.pixTransferenciaRepository = pixTransferenciaRepository;
        this.producer = producer;
        this.chavePixService = chavePixService;
    }

    private Optional<PixTransferencia> buscarPixTransferencia(PixConfirmacaoDto pixConfirmacaoDto) {
        return pixTransferenciaRepository.findById(pixConfirmacaoDto.getReqId());
    }
    private void atualizarStatusPix(PixConfirmacaoDto pixConfirmacaoDto, PixTransferencia entity) {
        entity.setStatus(pixConfirmacaoDto.getStatus());
        pixTransferenciaRepository.save(entity);
    }

    public void enviarConfirmacao(PixConfirmacaoDto pixConfirmacaoDto) {
        Optional<PixTransferencia> pixTransferencia = buscarPixTransferencia(pixConfirmacaoDto);
        atualizarStatusPix(pixConfirmacaoDto, pixTransferencia.get());

        String banco = pixTransferencia.get().getBancoRemetente();

        switch (banco) {
            case ("Itau"):
                producer.publish("pix-confirmacao-itau", pixConfirmacaoDto.getReqId(), new Gson().toJson(pixConfirmacaoDto));
                break;
            case ("Ada"):
                producer.publish("pix-confirmacao-ada", pixConfirmacaoDto.getReqId(), new Gson().toJson(pixConfirmacaoDto));
                break;
        }
    }



}
