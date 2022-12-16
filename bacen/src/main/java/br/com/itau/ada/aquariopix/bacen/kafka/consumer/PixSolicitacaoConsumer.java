package br.com.itau.ada.aquariopix.bacen.kafka.consumer;

import br.com.itau.ada.aquariopix.bacen.dto.transferenciaPix.PixSolicitacaoDto;
import br.com.itau.ada.aquariopix.bacen.service.PixSolicitacaoService;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class PixSolicitacaoConsumer {

    private PixSolicitacaoService pixSolicitacaoService;

    public PixSolicitacaoConsumer (PixSolicitacaoService pixSolicitacaoService) { this.pixSolicitacaoService = pixSolicitacaoService; }

    @KafkaListener(
            id = "${spring.kafka.consumer.pixSolicitacao-itau.group-id}",
            topics = "${topic.consumer.itauPixSolicitacao.name}")
    public void listenItauEnvioPix(String message, Acknowledgment ack){
        parseMensagemEnvioPix(message);

        ack.acknowledge();
    }

    @KafkaListener(
            id = "${spring.kafka.consumer.pixSolicitacao-ada.group-id}",
            topics = "${topic.consumer.adaPixSolicitacao.name}")
    public void listenAdaEnvioPix(String message, Acknowledgment ack){
        parseMensagemEnvioPix(message);

        ack.acknowledge();
    }

    private void parseMensagemEnvioPix(String messagem) {
        PixSolicitacaoDto pixDto = parseMensagem(messagem);
        pixSolicitacaoService.enviarPix(pixDto);
    }

    private PixSolicitacaoDto parseMensagem(String messagem) {
        return new Gson().fromJson(messagem, PixSolicitacaoDto.class);
    }
}
