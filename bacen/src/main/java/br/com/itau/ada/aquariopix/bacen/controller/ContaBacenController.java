package br.com.itau.ada.aquariopix.bacen.controller;

import br.com.itau.ada.aquariopix.bacen.model.ContaBacen;
import br.com.itau.ada.aquariopix.bacen.service.ContaBacenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/bacen/contas")
@RequiredArgsConstructor
public class ContaBacenController {
    private final ContaBacenService contaBacenService;

    @GetMapping
    public ResponseEntity<List<ContaBacen>> findAll() {
        return ResponseEntity.ok().body(contaBacenService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ContaBacen> findById(@PathVariable Long id) {
        return ResponseEntity.ok().body(contaBacenService.findById(id));
    }

    @GetMapping(value = "/numeroContaAndAgencia")
    public ResponseEntity<ContaBacen> findByNumeroContaAndAgencia(
            @RequestParam(value = "banco", defaultValue = "") String banco,
            @RequestParam(value = "numeroConta", defaultValue = "") String numeroConta,
            @RequestParam(value = "agencia", defaultValue = "") String agencia
    ) {
        return ResponseEntity.ok().body(contaBacenService.findByBancoContaAndAgencia(banco, numeroConta, agencia).get());
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody ContaBacen contaBacen) {
        contaBacenService.insert(contaBacen);
        return ResponseEntity.created(URI.create("")).build();
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody ContaBacen contaBacen) {
        contaBacenService.update(id, contaBacen);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contaBacenService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
