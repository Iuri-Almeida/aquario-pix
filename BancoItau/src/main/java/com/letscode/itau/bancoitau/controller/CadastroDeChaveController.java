package com.letscode.itau.bancoitau.controller;

import com.letscode.itau.bancoitau.dto.ChavePixDTO;
import com.letscode.itau.bancoitau.model.ChavePix;
import com.letscode.itau.bancoitau.service.CadastroDeChaveService;
import com.letscode.itau.bancoitau.service.PixService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/itau/pix")
public class CadastroDeChaveController {
    private final CadastroDeChaveService service;
    private final PixService pixService;

    @PostMapping("chaves")
    public Mono<ResponseEntity<ChavePix>> cadastrarChavePix(@RequestBody ChavePixDTO chavePixDTO) {
        //TODO tratamento de erro chave repetida
        return service.cadastrarChavePix(chavePixDTO);
    }

    @GetMapping("/{chave}")
    public Mono<ResponseEntity<ChavePix>> findByChave(@PathVariable String chave) {
        return service.findByChave(chave);
    }

    @GetMapping()
    public Flux<ChavePix> findAll() {
        return service.findAll();
    }
}
