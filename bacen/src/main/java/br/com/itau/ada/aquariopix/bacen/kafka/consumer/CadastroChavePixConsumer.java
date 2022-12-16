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
    public void listenCadastroChavePixItau(String mensagem, Acknowledgment ack){
        cadastrarChavePix(mensagem);

        ack.acknowledge();
    }

    @KafkaListener(
            id = "${spring.kafka.consumer.cadastro-chavePix-ada.group-id}",
            topics = "${topic.cadastro-chavePix-ada.consumer.name}")
    public void listenCadastroChavePixAda(String mensagem, Acknowledgment ack){
        cadastrarChavePix(mensagem);

        ack.acknowledge();
    }

    private void cadastrarChavePix(String mensagem) {
        ChavePixSolicitacaoDto chavePixDto = parseMensagem(mensagem);
        chavePixService.cadastrarChavePixEnviaMensagem(chavePixDto);
    }

    private ChavePixSolicitacaoDto parseMensagem(String mensagem) {
        return new Gson().fromJson(mensagem, ChavePixSolicitacaoDto.class);
    }

}
