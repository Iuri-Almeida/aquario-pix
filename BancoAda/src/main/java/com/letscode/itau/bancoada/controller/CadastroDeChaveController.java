package com.letscode.itau.bancoada.controller;

import com.letscode.itau.bancoada.dto.ChavePixDTO;
import com.letscode.itau.bancoada.model.ChavePix;
import com.letscode.itau.bancoada.model.Conta;
import com.letscode.itau.bancoada.dto.PixDTORequest;
import com.letscode.itau.bancoada.service.CadastroDeChaveService;
import com.letscode.itau.bancoada.service.PixService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ada/pix")
public class CadastroDeChaveController {
    private final CadastroDeChaveService service;
    private final PixService pixService;

    @PostMapping("chaves")
    public Mono<ResponseEntity<ChavePix>> cadastrarChavePix(@RequestBody ChavePixDTO chavePixDTO) {

        Long idRequisicao = service.withIdChavePix(chavePixDTO);

        String conta = chavePixDTO.getRequerente().getConta();
        String agencia = chavePixDTO.getRequerente().getAgencia();
        String cpf = chavePixDTO.getRequerente().getCpf();

        service.conferirRequerente(conta, agencia);

        service.solicitarCadastroBacen(idRequisicao, conta, agencia, cpf);

        return service.salvarChavePix(chavePixDTO);

    }

    @GetMapping("/{chave}")
    public Mono<ResponseEntity<ChavePix>> findByChave(@PathVariable String chave) {
        return service.findByChave(chave);
    }

    @GetMapping()
    public Flux<ChavePix> findAll() {
        return service.findAll();
    }

    @PostMapping()
    public Mono<ResponseEntity<Conta>> enviaPix(@RequestBody PixDTORequest pixDTORequest) {
        return pixService.enviaPix(pixDTORequest);
    }
}
