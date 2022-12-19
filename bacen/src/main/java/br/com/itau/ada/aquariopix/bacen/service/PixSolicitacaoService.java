package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.dto.MensagemKafkaDto;
import br.com.itau.ada.aquariopix.bacen.dto.chavePix.ChavePixDto;
import br.com.itau.ada.aquariopix.bacen.dto.transferenciaPix.PixSolicitacaoDto;
import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import br.com.itau.ada.aquariopix.bacen.kafka.producer.BacenProducer;
import br.com.itau.ada.aquariopix.bacen.model.PixTransferencia;
import br.com.itau.ada.aquariopix.bacen.repository.PixTransferenciaRepository;
import com.google.gson.Gson;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class PixSolicitacaoService {

    private final ChavePixService chavePixService;

    private final BacenProducer producer;

    private final PixTransferenciaRepository pixTransferenciaRepository;

    public PixSolicitacaoService(ChavePixService chavePixService, BacenProducer producer, PixTransferenciaRepository pixTransferenciaRepository) {
        this.chavePixService = chavePixService;
        this.producer = producer;
        this.pixTransferenciaRepository = pixTransferenciaRepository;
    }

    public MensagemKafkaDto enviarPix(@NotNull PixSolicitacaoDto pixSolicitacaoDto) {
        ChavePixDto chavePix = chavePixService.consultarChavePix(pixSolicitacaoDto.getChave());

        PixTransferencia pixTransferencia = pixSolicitacaoDto.mapperToEntity(StatusSolicitacao.Pendente);
        pixTransferenciaRepository.save(pixTransferencia);

        return enviarSolicitacao(chavePix.getBanco(), pixSolicitacaoDto);
    }

    @Contract("_, _ -> new")
    private @NotNull MensagemKafkaDto enviarSolicitacao(String bancoDestinatario, @NotNull PixSolicitacaoDto pixSolicitacaoDto) {
        String topic = definirTopico(bancoDestinatario);
        String key = pixSolicitacaoDto.getReqId();
        String message = new Gson().toJson(pixSolicitacaoDto);

        producer.publicar(topic, key, message);

        return new MensagemKafkaDto(topic, key, message);
    }

    @Contract(pure = true)
    private @NotNull String definirTopico(@NotNull String bancoDestinatario) {
        switch (bancoDestinatario) {
            case ("Itau"):
                return "pix-solicitacao-itau";
            case ("Ada"):
                return "pix-solicitacao-ada";
        }
        throw new RuntimeException("Banco não cadastrado");
    }
}
