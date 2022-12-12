package com.letscode.itau.bancoitau.service;

import com.letscode.itau.bancoitau.model.Conta;
import com.letscode.itau.bancoitau.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;

    public Flux<Conta> findAll() {
        return contaRepository.findAll();
    }

    public Conta findById(Long id) {
        return contaRepository.findById(id).block();
    }

    public Conta findByNumeroContaAndAgencia(String numeroConta, String agencia) {
        return contaRepository.findByNumeroContaAndAgencia(numeroConta, agencia).block();
    }

    public void insert(Conta conta) {
        contaRepository.save(conta);
    }

    public void update(Long id, Conta conta) {
        Conta contaDb = this.findById(id);

        this.updateData(contaDb, conta);

        contaRepository.save(contaDb);
    }

    public void delete(Long id) {
        contaRepository.deleteById(id);
    }

    private void updateData(Conta contaDb, Conta conta) {

        if (Optional.ofNullable(conta.getNome()).isPresent()) {
            contaDb.setNome(conta.getNome());
        }

        if (Optional.ofNullable(conta.getCpf()).isPresent()) {
            contaDb.setCpf(conta.getCpf());
        }

        if (Optional.ofNullable(conta.getEmail()).isPresent()) {
            contaDb.setEmail(conta.getEmail());
        }

        if (Optional.ofNullable(conta.getNumeroConta()).isPresent()) {
            contaDb.setNumeroConta(conta.getNumeroConta());
        }

        if (Optional.ofNullable(conta.getAgencia()).isPresent()) {
            contaDb.setAgencia(conta.getAgencia());
        }

    }

}
