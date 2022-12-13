package com.letscode.itau.bancoitau;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
public class BancoItauApplication {

    public static void main(String[] args) {
        SpringApplication.run(BancoItauApplication.class, args);
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("itau-cadastro-chavepix-solicitacao")
                .partitions(10)
                .replicas(1)
                .build();
    }

    @KafkaListener(id = "myId", topics = "cadastro-confirmacao-itau")
    public void listen(String in) {
        System.out.println(in);
    }

}
