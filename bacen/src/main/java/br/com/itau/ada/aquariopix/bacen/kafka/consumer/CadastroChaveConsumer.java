package br.com.itau.ada.aquariopix.bacen.kafka.consumer;

import br.com.itau.ada.aquariopix.bacen.service.ChavePixService;
import com.google.gson.Gson;
import br.com.itau.ada.aquariopix.bacen.dto.ChavePixSolicitacaoDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class CadastroChaveConsumer {

    private final ChavePixService chavePixService;

    public CadastroChaveConsumer(ChavePixService chavePixService) {
        this.chavePixService = chavePixService;
    }

    @KafkaListener(
            id = "${spring.kafka.consumer.group-id}",
            topics = "${topic.consumer.name}")
    public void listenCadastroChavePix(String message, Acknowledgment ack){
        ChavePixSolicitacaoDto chavePixDto = new Gson().fromJson(message, ChavePixSolicitacaoDto.class);
        chavePixService.cadastrarChavePix(chavePixDto);

        ack.acknowledge();
    }

}
