package br.com.itau.ada.aquariopix.bacen.kafka.consumer;

import br.com.itau.ada.aquariopix.bacen.model.ContaBacen;
import br.com.itau.ada.aquariopix.bacen.service.ContaBacenService;
import com.google.gson.Gson;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class CadastroContaConsumer {

    private final ContaBacenService contaBacenService;

    public CadastroContaConsumer(ContaBacenService contaBacenService) {
        this.contaBacenService = contaBacenService;
    }

    @KafkaListener(
            id = "${spring.kafka.consumer.cadastro-conta-itau.group-id}",
            topics = "${topic.consumer.itauCadastroConta.name}")
    public void listenCadastroContaItau(String mensagem, Acknowledgment ack){
        cadastrarConta(mensagem);

        ack.acknowledge();
    }

    @KafkaListener(
            id = "${spring.kafka.consumer.cadastro-conta-ada.group-id}",
            topics = "${topic.consumer.adaCadastroConta.name}")
    public void listenCadastroContaAda(String mensagem, Acknowledgment ack){
        cadastrarConta(mensagem);

        ack.acknowledge();
    }

    private void cadastrarConta(String mensagem) {
        ContaBacen contaBacen = parseMensagem(mensagem);
        contaBacenService.insert(contaBacen);
    }

    private ContaBacen parseMensagem(String mensagem) {
        return new Gson().fromJson(mensagem, ContaBacen.class);
    }

}
