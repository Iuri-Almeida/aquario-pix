package com.letscode.itau.bancoada.controller;

import com.letscode.itau.bancoada.model.PixTransferencia;
import com.letscode.itau.bancoada.repository.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ada/pix")
public class PixController {

    private final TransferenciaRepository transferenciaRepository;

    @GetMapping("/transferencias")
    public ResponseEntity<Flux<PixTransferencia>> verTransferencias() {
        return ResponseEntity.ok().body(transferenciaRepository.findAll());
    }
}
