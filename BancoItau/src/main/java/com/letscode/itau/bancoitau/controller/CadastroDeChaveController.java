package com.letscode.itau.bancoitau.controller;

import com.letscode.itau.bancoitau.model.ChavePix;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/itau/pix")
public class CadastroDeChaveController {
    @PostMapping("chaves")
    public void cadastrarChavePix(@RequestBody ChavePix chavePix) {
        //TODO Salvar chave no banco
        //TODO ENVIAR SOLICITACAO BACEN

    }
}
