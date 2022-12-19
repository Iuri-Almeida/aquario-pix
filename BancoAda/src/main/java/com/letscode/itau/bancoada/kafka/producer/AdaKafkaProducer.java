package com.letscode.itau.bancoada.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@RequiredArgsConstructor
public class AdaKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publicar(String topic, String msg) {
        kafkaTemplate.send(topic, msg).addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable e) {
                System.out.println("Falha ao enviar mensagem: " + msg + "\nErro: " + e);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                System.out.println("Mensagem enviada com sucesso: " + msg);
            }
        });

        kafkaTemplate.flush();
    }

}
