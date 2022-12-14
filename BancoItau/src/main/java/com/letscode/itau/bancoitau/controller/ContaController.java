package com.letscode.itau.bancoitau.controller;

import com.letscode.itau.bancoitau.model.Conta;
import com.letscode.itau.bancoitau.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping(value = "/api/itau/conta")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @GetMapping
    public ResponseEntity<Flux<Conta>> findAll() {
        return ResponseEntity.ok().body(contaService.findAll());
    }

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<Conta>> findById(@PathVariable Long id) {
        return contaService.findById(id);
    }

    @GetMapping(value = "/numeroContaAndAgencia")
    public Mono<ResponseEntity<Conta>> findByNumeroContaAndAgencia(
            @RequestParam(value = "numeroConta", defaultValue = "") String numeroConta,
            @RequestParam(value = "agencia", defaultValue = "") String agencia
    ) {
        return contaService.findByNumeroContaAndAgencia(numeroConta, agencia);
    }

    @PostMapping
    public Mono<ResponseEntity<Conta>> insert(@RequestBody Conta conta) {
        return contaService.insert(conta);
    }

    @PatchMapping(value = "/{id}")
    public Mono<ResponseEntity<Conta>> update(@PathVariable Long id, @RequestBody Conta conta) {
        return contaService.update(id, conta);
    }

    @DeleteMapping(value = "/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        return contaService.delete(id);
    }

}
