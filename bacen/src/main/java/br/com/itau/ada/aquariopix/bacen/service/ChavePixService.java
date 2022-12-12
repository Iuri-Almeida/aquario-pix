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

    public ChavePixDto buscarChave(String tipo, String chave){
        Optional<ChavePix> chavePix = chavePixRepository.findById(tipo ,chave);
        return chavePix.get().mapperToDto();
    }
}
