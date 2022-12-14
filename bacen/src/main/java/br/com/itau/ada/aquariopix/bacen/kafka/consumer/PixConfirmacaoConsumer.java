package br.com.itau.ada.aquariopix.bacen.kafka.consumer;

import br.com.itau.ada.aquariopix.bacen.dto.transferenciaPix.PixConfirmacaoDto;
import br.com.itau.ada.aquariopix.bacen.service.PixConfirmacaoService;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class PixConfirmacaoConsumer {

    private PixConfirmacaoService pixConfirmacaoService;

    public PixConfirmacaoConsumer(PixConfirmacaoService pixConfirmacaoService) { this.pixConfirmacaoService = pixConfirmacaoService; }

    @KafkaListener(
            id = "${spring.kafka.consumer.pixConfirmacao-itau.group-id}",
            topics = "${topic.consumer.itauPixConfirmacao.name}")
    public void itauConfirmacaoPix(String message, Acknowledgment ack){
        confirmacaoPix(message);

        ack.acknowledge();
    }

    @KafkaListener(
            id = "${spring.kafka.consumer.pixConfirmacao-ada.group-id}",
            topics = "${topic.consumer.adaPixConfirmacao.name}")
    public void adaConfirmacaoPix(String message, Acknowledgment ack){
        confirmacaoPix(message);

        ack.acknowledge();
    }

    private void confirmacaoPix(String messagem) {
        PixConfirmacaoDto pixDto = parseMensagem(messagem);
        pixConfirmacaoService.confirmarPixParaRemetente(pixDto);
    }

    private PixConfirmacaoDto parseMensagem(String messagem) {
        return new Gson().fromJson(messagem, PixConfirmacaoDto.class);
    }

}
