package br.com.itau.ada.aquariopix.bacen.controller;

import br.com.itau.ada.aquariopix.bacen.dto.ChavePixDto;
import br.com.itau.ada.aquariopix.bacen.service.ChavePixService;
import br.com.itau.ada.aquariopix.bacen.dto.ChavePixJaExistenteDto;
import br.com.itau.ada.aquariopix.bacen.dto.ChavePixConfirmacaoDto;
import br.com.itau.ada.aquariopix.bacen.dto.ChavePixSolicitacaoDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/bacen/pix/chaves")
public class ChavePixController {

    private final ChavePixService chavePixService;

    public ChavePixController(ChavePixService chavePixService) {
        this.chavePixService = chavePixService;
    }

    @GetMapping
    public ResponseEntity<ChavePixJaExistenteDto> chavePixEmUso(@RequestParam(value = "tipo", required = true) String tipo, @RequestParam("chave") String  chave) {
        boolean chaveExistente = chavePixService.chaveEmUso(tipo, chave);
        if (chaveExistente) return ResponseEntity.ok(new ChavePixJaExistenteDto(chaveExistente));
        else return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

    @PostMapping
    public ResponseEntity<ChavePixConfirmacaoDto> cadastrarChavePix(@RequestBody ChavePixSolicitacaoDto chavePixSolicitacaoDto) {
        ChavePixConfirmacaoDto chavePixConfirmacaoDto = chavePixService.cadastrarChavePix(chavePixSolicitacaoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(chavePixConfirmacaoDto);
    }

    @GetMapping
    public ResponseEntity<ChavePixDto> consultarChavePix(@RequestBody ChavePixSolicitacaoDto chavePixSolicitacaoDto) {
        Optional<ChavePixDto> chavePix = chavePixService.consultarChavePix(chavePixSolicitacaoDto);
        if (chavePix.isPresent()) return ResponseEntity.ok(chavePix.get());
        else return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
