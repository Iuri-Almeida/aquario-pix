package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.dto.ChavePixDto;
import br.com.itau.ada.aquariopix.bacen.dto.chavePix.ChavePixConfirmacaoDto;
import br.com.itau.ada.aquariopix.bacen.dto.chavePix.ChavePixSolicitacaoDto;
import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import br.com.itau.ada.aquariopix.bacen.kafka.producer.BacenProducer;
import br.com.itau.ada.aquariopix.bacen.model.ChavePix;
import br.com.itau.ada.aquariopix.bacen.model.ContaBacen;
import br.com.itau.ada.aquariopix.bacen.repository.ChavePixRepository;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChavePixService {

    private final ChavePixRepository chavePixRepository;

    private final BacenProducer cadastroChaveProducer;

    private final ContaBacenService contaBacenService;

    public ChavePixService(ChavePixRepository chavePixRepository, BacenProducer cadastroChaveProducer, ContaBacenService contaBacenService) {
        this.chavePixRepository = chavePixRepository;
        this.cadastroChaveProducer = cadastroChaveProducer;
        this.contaBacenService = contaBacenService;
    }

    public boolean chaveEmUso(String tipo, String chave) {
        return chavePixRepository.findById(tipo, chave).isPresent();
    }

    public ChavePixConfirmacaoDto cadastrarChavePix(ChavePixSolicitacaoDto chavePixDto) {
        ChavePixConfirmacaoDto chavePixConfirmacao = validarChave(chavePixDto);

        if (chavePixConfirmacao.getStatus() == StatusSolicitacao.Aceito){
            chavePixRepository.save(chavePixDto.mapperToEntity());
        }

        return chavePixConfirmacao;
    }

    public void cadastrarChavePixEnviaMensagem(ChavePixSolicitacaoDto chavePixDto) {
        ChavePixConfirmacaoDto chavePixConfirmacaoDto = cadastrarChavePix(chavePixDto);

        enviaMensagemConfirmacaoChave(chavePixConfirmacaoDto);
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

    private boolean donoDaChaveValido(ChavePixSolicitacaoDto chavePix) {
        boolean tipoValido = false;

        switch (chavePix.getTipo()) {
            case ("CPF"):
                tipoValido = verificarCpfDaConta(chavePix.getChave(), chavePix.getConta(), chavePix.getAgencia());
        }

        return tipoValido;
    }

    private boolean verificarCpfDaConta(String cpf, String conta, String agencia){
        ContaBacen contaBacen = contaBacenService.findByNumeroContaAndAgencia(conta, agencia);
        return contaBacen.getCpf().equals(cpf);
    }

    private void enviaMensagemConfirmacaoChave(ChavePixConfirmacaoDto chavePixConfirmacaoDto) {
        String message = new Gson().toJson(chavePixConfirmacaoDto);
        String key = chavePixConfirmacaoDto.getReqId() + chavePixConfirmacaoDto.getBanco();

        String topic = definirTopico(chavePixConfirmacaoDto.getBanco());
        cadastroChaveProducer.publish(topic, key, message);
    }

    public Optional<ChavePixDto> consultarChavePix (String chave){
        Optional<ChavePix> resultado = chavePixRepository.findById(chave);
        return Optional.ofNullable(resultado.get().mapperToChavePixDto());
    }

    private String definirTopico(String banco) {
        switch (banco) {
            case ("Itau"):
                return "confirmacao-cadastro-chavepix-itau";
            case ("Ada"):
                return "confirmacao-cadastro-chavepix-ada";
        }
        throw new RuntimeException("Banco não cadastrado");
    }


}
