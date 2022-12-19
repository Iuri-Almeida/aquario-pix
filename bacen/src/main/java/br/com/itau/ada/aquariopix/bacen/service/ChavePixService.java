package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.dto.MensagemKafkaDto;
import br.com.itau.ada.aquariopix.bacen.dto.chavePix.ChavePixDto;
import br.com.itau.ada.aquariopix.bacen.dto.chavePix.ChavePixConfirmacaoDto;
import br.com.itau.ada.aquariopix.bacen.dto.chavePix.ChavePixSolicitacaoDto;
import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import br.com.itau.ada.aquariopix.bacen.kafka.producer.BacenProducer;
import br.com.itau.ada.aquariopix.bacen.model.ContaBacen;
import br.com.itau.ada.aquariopix.bacen.repository.ChavePixRepository;
import com.google.gson.Gson;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChavePixService {

    private final ChavePixRepository chavePixRepository;

    private final BacenProducer producer;

    private final ContaBacenService contaBacenService;

    public ChavePixService(ChavePixRepository chavePixRepository, BacenProducer producer, ContaBacenService contaBacenService) {
        this.chavePixRepository = chavePixRepository;
        this.producer = producer;
        this.contaBacenService = contaBacenService;
    }

    public boolean chaveEmUso(String tipo, String chave) {
        return chavePixRepository.findById(tipo, chave).isPresent();
    }

    public ChavePixDto consultarChavePix (String chave){
        return chavePixRepository.findById(chave).orElseThrow(() -> new RuntimeException("Chave não encontrada")).mapperToChavePixDto();
    }

    public MensagemKafkaDto cadastrarChavePix(ChavePixSolicitacaoDto chavePixDto) {
        ChavePixConfirmacaoDto chavePixConfirmacaoDto = salvarChave(chavePixDto);

        return enviaMensagemConfirmacaoChave(chavePixConfirmacaoDto);
    }

    public ChavePixConfirmacaoDto salvarChave(ChavePixSolicitacaoDto chavePixDto) {
        ChavePixConfirmacaoDto chavePixConfirmacao = validarChave(chavePixDto);

        if (chavePixConfirmacao.getStatus() == StatusSolicitacao.Aceito){
            chavePixRepository.save(chavePixDto.mapperToEntity());
        }

        return chavePixConfirmacao;
    }

    private ChavePixConfirmacaoDto validarChave(ChavePixSolicitacaoDto chavePix) {
        ChavePixConfirmacaoDto chavePixConfirmacaoDto = chavePix.mapperToConfirmacaoDto(StatusSolicitacao.Pendente);

        if (chaveEmUso(chavePix.getTipo(), chavePix.getChave())) {
            chavePixConfirmacaoDto.setStatus(StatusSolicitacao.Recusado);
        }

        else if (donoDaChaveValido(chavePix)) {
            chavePixConfirmacaoDto.setStatus(StatusSolicitacao.Aceito);
        } else {
            chavePixConfirmacaoDto.setStatus(StatusSolicitacao.Recusado);
        }

        return chavePixConfirmacaoDto;
    }

    //Está com switch pensando na implementação futura de diferentes tipos de chave Pix
    private boolean donoDaChaveValido(@NotNull ChavePixSolicitacaoDto chavePix) {
        switch (chavePix.getTipo()) {
            case ("CPF"):
                return verificarCpfDaConta(chavePix.getChave(), chavePix.getConta(), chavePix.getAgencia());
        }

        throw new RuntimeException("Tipo de chave pix não encontrado");
    }

    public boolean verificarCpfDaConta(String cpf, String conta, String agencia){
        Optional<ContaBacen> contaBacen = contaBacenService.findByNumeroContaAndAgencia(conta, agencia);
        if (contaBacen.isPresent()) return contaBacen.get().getCpf().equals(cpf);
        throw new RuntimeException("Conta não existente no Bacen");
    }

    private MensagemKafkaDto enviaMensagemConfirmacaoChave(ChavePixConfirmacaoDto chavePixConfirmacaoDto) {
        String topic = definirTopico(chavePixConfirmacaoDto.getBanco());
        String key = chavePixConfirmacaoDto.getReqId();
        String message = new Gson().toJson(chavePixConfirmacaoDto);

        producer.publicar(topic, key, message);

        return new MensagemKafkaDto(topic, key, message);
    }

    @Contract(pure = true)
    private @NotNull String definirTopico(@NotNull String banco) {
        switch (banco) {
            case ("Itau"):
                return "confirmacao-cadastro-chavepix-itau";
            case ("Ada"):
                return "confirmacao-cadastro-chavepix-ada";
        }
        throw new RuntimeException("Banco não cadastrado");
    }

}
