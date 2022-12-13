package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.enums.StatusSolicitacao;
import br.com.itau.ada.aquariopix.bacen.kafka.producer.CadastroChavePixProducer;
import br.com.itau.ada.aquariopix.bacen.repository.ChavePixRepository;
import com.google.gson.Gson;
import br.com.itau.ada.aquariopix.bacen.dto.ChavePixConfirmacaoDto;
import br.com.itau.ada.aquariopix.bacen.dto.ChavePixSolicitacaoDto;
import org.springframework.stereotype.Service;

@Service
public class ChavePixService {

    private final ChavePixRepository chavePixRepository;

    private final CadastroChavePixProducer cadastroChaveProducer;

    public ChavePixService(ChavePixRepository chavePixRepository, CadastroChavePixProducer cadastroChaveProducer) {
        this.chavePixRepository = chavePixRepository;
        this.cadastroChaveProducer = cadastroChaveProducer;
    }

    public boolean verificarChaveExistente(String tipo, String chave) {
        return chavePixRepository.findById(tipo, chave).isPresent();
    }

    public ChavePixConfirmacaoDto cadastrarChavePix(ChavePixSolicitacaoDto chavePixDto) {
        ChavePixConfirmacaoDto chavePixConfirmacaoDto = chavePixDto.mapperToConfirmacaoDto(StatusSolicitacao.Pendente);

        if (verificarChaveExistente(chavePixDto.getTipo(), chavePixDto.getChave())) {
            chavePixConfirmacaoDto.setStatus(StatusSolicitacao.Recusado);
        }
        else {
            chavePixRepository.save(chavePixDto.mapperToEntity());
            chavePixConfirmacaoDto.setStatus(StatusSolicitacao.Aceito);
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
