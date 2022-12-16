package br.com.itau.ada.aquariopix.bacen.kafka.consumer;

import br.com.itau.ada.aquariopix.bacen.dto.chavePix.ChavePixSolicitacaoDto;
import br.com.itau.ada.aquariopix.bacen.service.ChavePixService;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class CadastroChavePixConsumer {

    private final ChavePixService chavePixService;

    public CadastroChavePixConsumer(ChavePixService chavePixService) {
        this.chavePixService = chavePixService;
    }

    @KafkaListener(
            id = "${spring.kafka.consumer.cadastro-chavePix-Itau.group-id}",
            topics = "${topic.cadastro-chavePix-Itau.consumer.name}")
    public void listenCadastroChavePix(String message, Acknowledgment ack){
        ChavePixSolicitacaoDto chavePixDto = new Gson().fromJson(message, ChavePixSolicitacaoDto.class);
        chavePixService.cadastrarChavePixEnviaMensagem(chavePixDto);

        ack.acknowledge();
    }

}
