package com.letscode.itau.bancoitau.controller;

import com.letscode.itau.bancoitau.model.Conta;
import com.letscode.itau.bancoitau.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

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
    public ResponseEntity<Conta> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(contaService.findById(id));
    }

    @GetMapping(value = "/numeroContaAndAgencia")
    public ResponseEntity<Conta> findByNumeroContaAndAgencia(
            @RequestParam(value = "numeroConta", defaultValue = "") String numeroConta,
            @RequestParam(value = "agencia", defaultValue = "") String agencia
    ) {
        return ResponseEntity.ok().body(contaService.findByNumeroContaAndAgencia(numeroConta, agencia));
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody Conta conta) {
        contaService.insert(conta);
        return ResponseEntity.created(URI.create("")).build();
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody Conta conta) {
        contaService.update(id, conta);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
