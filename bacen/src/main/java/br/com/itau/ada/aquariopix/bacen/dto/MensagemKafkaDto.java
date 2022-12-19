package br.com.itau.ada.aquariopix.bacen.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MensagemKafkaDto {

    private String topic;
    private String key;
    private String message;
}
