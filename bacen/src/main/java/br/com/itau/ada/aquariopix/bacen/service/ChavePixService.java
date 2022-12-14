package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.dto.ChavePixDto;
import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import br.com.itau.ada.aquariopix.bacen.kafka.producer.CadastroChavePixProducer;
import br.com.itau.ada.aquariopix.bacen.model.ChavePix;
import br.com.itau.ada.aquariopix.bacen.model.ContaBacen;
import br.com.itau.ada.aquariopix.bacen.repository.ChavePixRepository;
import com.google.gson.Gson;
import br.com.itau.ada.aquariopix.bacen.dto.ChavePixConfirmacaoDto;
import br.com.itau.ada.aquariopix.bacen.dto.ChavePixSolicitacaoDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChavePixService {

    private final ChavePixRepository chavePixRepository;

    private final CadastroChavePixProducer cadastroChaveProducer;

    private final ContaBacenService contaBacenService;

    public ChavePixService(ChavePixRepository chavePixRepository, CadastroChavePixProducer cadastroChaveProducer, ContaBacenService contaBacenService) {
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

        cadastroChaveProducer.publish(key, message);
    }

    public Optional<ChavePixDto> consultarChavePix (ChavePixSolicitacaoDto chavePix){
        Optional<ChavePix> resultado = chavePixRepository.findById(chavePix.getChave());

        if (resultado.isPresent()) {
            if (resultado.get().getBanco() != chavePix.getBanco())
            {
                throw new RuntimeException("Acesso negado. Essa chave não pertence ao cliente informado");
            }
        }

        return Optional.ofNullable(chavePix.mapperToChavePixDto());
    }

}
