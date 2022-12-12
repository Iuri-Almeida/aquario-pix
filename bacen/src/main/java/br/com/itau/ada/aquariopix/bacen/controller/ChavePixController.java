package br.com.itau.ada.aquariopix.bacen.controller;

import br.com.itau.ada.aquariopix.bacen.service.ChavePixService;
import dto.ChavePixDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/bacen/pix/chaves")
public class ChavePixController {

    private final ChavePixService chavePixService;

    public ChavePixController(ChavePixService chavePixService) {
        this.chavePixService = chavePixService;
    }

    //TODO: Inserir tratamento de erro
    @GetMapping
    public ResponseEntity<ChavePixDto> getChavePix(@RequestParam(value = "tipo", required = true) String tipo, @RequestParam("chave") String  chave) {
        ChavePixDto chavePixDto = chavePixService.buscarChave(tipo, chave);
        return ResponseEntity.ok(chavePixDto);
    }

}
