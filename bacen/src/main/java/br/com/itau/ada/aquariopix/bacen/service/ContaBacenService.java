package br.com.itau.ada.aquariopix.bacen.service;

import br.com.itau.ada.aquariopix.bacen.model.ContaBacen;
import br.com.itau.ada.aquariopix.bacen.repository.ContaBacenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class ContaBacenService {
    private final ContaBacenRepository contaBacenRepository;

    public Flux<ContaBacen> findAll() {
        return contaBacenRepository.findAll();
    }

    public ContaBacen findById(Long id) {
        return contaBacenRepository.findById(id).block();
    }

    public ContaBacen findByNumeroContaAndAgencia(String numeroConta, String agencia) {
        return contaBacenRepository.findByNumeroContaAndAgencia(numeroConta, agencia).block();
    }

    public void insert(ContaBacen contaBacen) {
        contaBacenRepository.save(contaBacen);
    }

    public void update(Long id, ContaBacen contaBacen) {
        ContaBacen contaDb = this.findById(id);

        this.updateData(contaDb, contaBacen);

        contaBacenRepository.save(contaDb);
    }

    public void delete(Long id) {
        contaBacenRepository.deleteById(id);
    }

    private void updateData(ContaBacen contaDb, ContaBacen contaBacen) {

        if (Optional.ofNullable(contaBacen.getNome()).isPresent()) {
            contaDb.setNome(contaBacen.getNome());
        }

        if (Optional.ofNullable(contaBacen.getCpf()).isPresent()) {
            contaDb.setCpf(contaBacen.getCpf());
        }

        if (Optional.ofNullable(contaBacen.getEmail()).isPresent()) {
            contaDb.setEmail(contaBacen.getEmail());
        }

        if (Optional.ofNullable(contaBacen.getNumeroConta()).isPresent()) {
            contaDb.setNumeroConta(contaBacen.getNumeroConta());
        }

        if (Optional.ofNullable(contaBacen.getAgencia()).isPresent()) {
            contaDb.setAgencia(contaBacen.getAgencia());
        }
        if (Optional.ofNullable(contaBacen.getBanco()).isPresent()) {
            contaDb.setBanco(contaBacen.getBanco());
        }
    }

}
