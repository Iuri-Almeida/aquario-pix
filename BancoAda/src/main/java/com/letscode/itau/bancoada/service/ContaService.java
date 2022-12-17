package com.letscode.itau.bancoada.service;

import com.letscode.itau.bancoada.model.Conta;
import com.letscode.itau.bancoada.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;

    public Flux<Conta> findAll() {
        return contaRepository.findAll();
    }

    public Mono<ResponseEntity<Conta>> findById(Long id) {
        return contaRepository.findById(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public Mono<ResponseEntity<Conta>> findByNumeroContaAndAgencia(String numeroConta, String agencia) {
        return contaRepository.findByNumeroContaAndAgencia(numeroConta, agencia)
                .map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public Mono<ResponseEntity<Conta>> insert(Conta conta) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        return contaRepository.save(conta)
                .map(conta1 -> ResponseEntity.created(uriComponentsBuilder.path("/api/ada/conta/{id}")
                        .buildAndExpand(conta1.getId()).toUri()).body(conta1));
    }

    public Mono<ResponseEntity<Conta>> update(Long id, Conta conta) {
        return contaRepository.findById(id).flatMap(contaDb -> {
            this.updateData(contaDb, conta);
            return contaRepository.save(contaDb);
        }).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public Mono<ResponseEntity<Void>> delete(Long id) {
        return contaRepository.findById(id)
                .flatMap(contaDb -> contaRepository.delete(contaDb)
                        .then(Mono.just(ResponseEntity.noContent().<Void>build())))
                .defaultIfEmpty(ResponseEntity.notFound().build());
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
