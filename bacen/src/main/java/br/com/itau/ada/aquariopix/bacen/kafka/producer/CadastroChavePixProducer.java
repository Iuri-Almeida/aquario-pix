package br.com.itau.ada.aquariopix.bacen.kafka.producer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class CadastroChavePixProducer {

    @Value("${topic.producer.name}")
    private String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;


    public CadastroChavePixProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String key, String message) {
        kafkaTemplate.send(this.topic, key, message).addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                System.out.println("Falha ao enviar mensagem: " + message + "\nErro: " + ex);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("Mensagem enviada com sucesso: " + message);
            }
        });
        kafkaTemplate.flush();
    }
}
