package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.dto.MensagemKafkaDto;
import br.com.itau.ada.aquariopix.bacen.dto.transferenciaPix.PixConfirmacaoDto;
import br.com.itau.ada.aquariopix.bacen.kafka.producer.BacenProducer;
import br.com.itau.ada.aquariopix.bacen.model.PixTransferencia;
import br.com.itau.ada.aquariopix.bacen.repository.PixTransferenciaRepository;
import com.google.gson.Gson;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PixConfirmacaoService {

    private final PixTransferenciaRepository pixTransferenciaRepository;

    private final BacenProducer producer;

    public PixConfirmacaoService(PixTransferenciaRepository pixTransferenciaRepository, BacenProducer producer, ChavePixService chavePixService) {
        this.pixTransferenciaRepository = pixTransferenciaRepository;
        this.producer = producer;
    }

    public MensagemKafkaDto confirmarPixParaRemetente(PixConfirmacaoDto pixConfirmacaoDto) {
        Optional<PixTransferencia> pixTransferencia = buscarPixTransferencia(pixConfirmacaoDto);
        atualizarStatusPix(pixConfirmacaoDto, pixTransferencia.get());

        String bancoRemetente = pixTransferencia.get().getBancoRemetente();
        return enviarConfirmacaoPix(pixConfirmacaoDto, bancoRemetente);
    }

    private @NotNull Optional<PixTransferencia> buscarPixTransferencia(@NotNull PixConfirmacaoDto pixConfirmacaoDto) {
        return pixTransferenciaRepository.findById(pixConfirmacaoDto.getReqId());
    }

    private void atualizarStatusPix(@NotNull PixConfirmacaoDto pixConfirmacaoDto, @NotNull PixTransferencia entity) {
        entity.setStatus(pixConfirmacaoDto.getStatus());
        pixTransferenciaRepository.save(entity);
    }

    @Contract("_, _ -> new")
    private @NotNull MensagemKafkaDto enviarConfirmacaoPix(@NotNull PixConfirmacaoDto pixConfirmacaoDto, String banco) {
        String topic = definirTopico(banco);
        String key = pixConfirmacaoDto.getReqId();
        String message = new Gson().toJson(pixConfirmacaoDto);

        producer.publicar(topic, key, message);

        return new MensagemKafkaDto(topic, key, message);
    }

    @Contract(pure = true)
    private @NotNull String definirTopico(@NotNull String banco) {
        switch (banco) {
            case ("Itau"):
                return "pix-confirmacao-itau";
            case ("Ada"):
                return "pix-confirmacao-ada";
        }
        throw new RuntimeException("Banco não cadastrado");
    }

}
