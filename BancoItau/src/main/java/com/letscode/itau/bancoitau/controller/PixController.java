package com.letscode.itau.bancoitau.controller;

import com.letscode.itau.bancoitau.dto.PixDTORequest;
import com.letscode.itau.bancoitau.model.Conta;
import com.letscode.itau.bancoitau.model.PixTransferencia;
import com.letscode.itau.bancoitau.repository.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.letscode.itau.bancoitau.service.PixService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/itau/pix")
public class PixController {
    private final TransferenciaRepository transferenciaRepository;
    private final PixService pixService;
    @GetMapping("transferencias")
    public ResponseEntity<Flux<PixTransferencia>> verTransferencias() {
        return ResponseEntity.ok().body(transferenciaRepository.findAll());
    }

    @PostMapping()
    public Mono<ResponseEntity<Conta>> enviaPix(@RequestBody PixDTORequest pixDTO) {
        System.out.println(pixDTO);
        return pixService.enviaPix(pixDTO);
    }
}
