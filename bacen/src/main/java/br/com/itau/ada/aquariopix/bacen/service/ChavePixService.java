package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.model.ChavePix;
import br.com.itau.ada.aquariopix.bacen.repository.ChavePixRepository;
import dto.ChavePixDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChavePixService {

    private final ChavePixRepository chavePixRepository;

    public ChavePixService(ChavePixRepository chavePixRepository) {
        this.chavePixRepository = chavePixRepository;
    }

    public void cadastrarChavePix (ChavePixDto chavePixDto) {
        verificarChaveExistente(chavePixDto.getTipo(), chavePixDto.getChave());
        chavePixRepository.save(chavePixDto.mapperToEntity());
    }

    public ChavePixDto buscarChave(String tipo, String chave){
       return chavePixRepository.findById(tipo ,chave).get().mapperToDto();
    }

    private void verificarChaveExistente(String tipo, String chave) {
        if (buscarChave(tipo, chave).getChave().length() > 1){
            throw new RuntimeException("A chave já está em uso");
        }
    }

}
