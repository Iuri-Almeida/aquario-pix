package com.letscode.itau.bancoada.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic topicoAdaCadastroChavePixSolicitacao() {
        return TopicBuilder.name("ada-cadastro-chavepix-solicitacao")
                .partitions(10)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topicoAdaPixSolicitacao() {
        return TopicBuilder.name("ada-pix-solicitacao")
                .partitions(10)
                .replicas(1)
                .build();
    }

}