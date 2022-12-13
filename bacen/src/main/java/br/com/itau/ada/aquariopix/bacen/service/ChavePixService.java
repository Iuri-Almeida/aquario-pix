package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacoes;
import br.com.itau.ada.aquariopix.bacen.kafka.producer.CadastroChaveProducer;
import br.com.itau.ada.aquariopix.bacen.model.ChavePix;
import br.com.itau.ada.aquariopix.bacen.repository.ChavePixRepository;
import com.google.gson.Gson;
import br.com.itau.ada.aquariopix.bacen.dto.ChavePixConfirmacaoDto;
import br.com.itau.ada.aquariopix.bacen.dto.ChavePixSolicitacaoDto;
import org.springframework.stereotype.Service;

@Service
public class ChavePixService {

    private final ChavePixRepository chavePixRepository;

    private final CadastroChaveProducer cadastroChaveProducer;

    public ChavePixService(ChavePixRepository chavePixRepository, CadastroChaveProducer cadastroChaveProducer) {
        this.chavePixRepository = chavePixRepository;
        this.cadastroChaveProducer = cadastroChaveProducer;
    }

    public boolean verificarChaveExistente(String tipo, String chave) {
        return chavePixRepository.findById(tipo ,chave).isPresent();
    }

    public ChavePixConfirmacaoDto cadastrarChavePix(ChavePixSolicitacaoDto chavePixDto) {
        ChavePixConfirmacaoDto chavePixConfirmacaoDto = chavePixDto.mapperToConfirmacaoDto(chavePixDto.getReqId(), StatusSolicitacoes.Pendente);

        if (verificarChaveExistente(chavePixDto.getTipo(), chavePixDto.getChave())) {
            chavePixConfirmacaoDto.setStatus(StatusSolicitacoes.Recusado);
        }
        else {
            chavePixRepository.save(chavePixDto.mapperToEntity());
            chavePixConfirmacaoDto.setStatus(StatusSolicitacoes.Aceito);
        }

        publicarConfirmacaoChave(chavePixConfirmacaoDto);

        return chavePixConfirmacaoDto;
    }

    private void publicarConfirmacaoChave(ChavePixConfirmacaoDto chavePixConfirmacaoDto) {
        String message = new Gson().toJson(chavePixConfirmacaoDto);
        String key = chavePixConfirmacaoDto.getReqId() + chavePixConfirmacaoDto.getBanco();

        cadastroChaveProducer.publish(key, message);
    }

}
