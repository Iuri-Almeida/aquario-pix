package com.letscode.itau.bancoitau.controller;

import com.letscode.itau.bancoitau.model.PixTransferencia;
import com.letscode.itau.bancoitau.repository.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/itau/pix")
public class PixController {
    private final TransferenciaRepository transferenciaRepository;
    @GetMapping("transferencias")
    public ResponseEntity<Flux<PixTransferencia>> verTransferencias() {
        return ResponseEntity.ok().body(transferenciaRepository.findAll());
    }
}
