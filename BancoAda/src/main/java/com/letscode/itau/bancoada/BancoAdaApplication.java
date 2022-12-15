package com.letscode.itau.bancoada;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
@EnableDiscoveryClient
public class BancoAdaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BancoAdaApplication.class, args);
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("ada-cadastro-chavepix-solicitacao")
                .partitions(10)
                .replicas(1)
                .build();
    }

}
