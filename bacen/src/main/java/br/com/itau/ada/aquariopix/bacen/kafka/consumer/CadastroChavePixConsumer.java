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
            id = "${spring.kafka.consumer.cadastro-chavePix-itau.group-id}",
            topics = "${topic.cadastro-chavePix-itau.consumer.name}")
    public void cadastroChavePixItau(String mensagem, Acknowledgment ack){
        cadastrarChavePix(mensagem);

        ack.acknowledge();
    }

    @KafkaListener(
            id = "ada-cadastro-chavepix",
            topics = "ada-cadastro-chavepix-solicitacao")
    public void cadastroChavePixAda(String mensagem, Acknowledgment ack){
        cadastrarChavePix(mensagem);

        ack.acknowledge();
    }

    private void cadastrarChavePix(String mensagem) {
        ChavePixSolicitacaoDto chavePixDto = parseMensagem(mensagem);
        chavePixService.cadastrarChavePix(chavePixDto);
    }

    private ChavePixSolicitacaoDto parseMensagem(String mensagem) {
        return new Gson().fromJson(mensagem, ChavePixSolicitacaoDto.class);
    }

}
