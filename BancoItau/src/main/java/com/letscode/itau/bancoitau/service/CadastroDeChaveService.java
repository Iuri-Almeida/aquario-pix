package com.letscode.itau.bancoitau.service;

import com.letscode.itau.bancoitau.dto.ChavePixDTO;
import com.letscode.itau.bancoitau.enumeration.Status;
import com.letscode.itau.bancoitau.model.ChavePix;
import com.letscode.itau.bancoitau.repository.ChavePixRepository;
import com.letscode.itau.bancoitau.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class CadastroDeChaveService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private ChavePixRepository pixRepository;
    private ContaRepository contaRepository;

    @Autowired
    public void setPixRepository(ChavePixRepository repository) {
        this.pixRepository = repository;

    }

    @Autowired
    public void setContaRepository(ContaRepository repository) {
        this.contaRepository = repository;

    }


    public void conferirRequerente(String numeroConta, String agencia) {
        contaRepository.findByNumeroContaAndAgencia(numeroConta, agencia).subscribe(System.out::println);
    }


    public void solicitarCadastroBacen(Long idRequisicao, String numeroConta, String agencia, String chave) {
        String mensagem = "{reqId:"
                + idRequisicao +
                ", tipo: CPF, " +
                "chave: " + chave +
                ", banco: Itau" +
                ", conta: " + numeroConta +
                ", agencia: " + agencia +
                "} ";

        enviaMensagemKafka(mensagem);

    }


    public Mono<ResponseEntity<ChavePix>> salvarChavePix(ChavePixDTO chavePixDTO) {
        ChavePix chavePix = chavePixDTOparaChavePix(chavePixDTO);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        return pixRepository.save(chavePix).map(
                chave -> ResponseEntity.created(uriComponentsBuilder.path("/api/itau/pix/{chave}")
                                .buildAndExpand(chave.getChave())
                                .toUri()
                        )
                        .body(chave)
        );
    }

    public Long geraIdRequisicao() {
        return (long) new Random().nextInt(Integer.MAX_VALUE);
    }

    private void enviaMensagemKafka(String mensagem) {
        kafkaTemplate.send("itau-cadastro-chavepix-solicitacao", mensagem);
    }


    private ChavePix chavePixDTOparaChavePix(ChavePixDTO chavePixDTO) {
        return new ChavePix(chavePixDTO.getReqId(), chavePixDTO.getTipoDeChave(), chavePixDTO.getRequerente().getAgencia(), chavePixDTO.getRequerente().getConta(), chavePixDTO.getRequerente().getCpf(), Status.PENDENTE);
    }

    public Long withIdChavePix(ChavePixDTO chavePixDTO) {
        Long idRequisicao = geraIdRequisicao();
        chavePixDTO.setReqId(idRequisicao);
        return idRequisicao;
    }

    public Mono<ResponseEntity<ChavePix>> findByChave(String chave) {
        //TODO atualizar status da chave de acordo com o Bacen
        return pixRepository.findByChave(chave).map(
                ResponseEntity::ok
        ).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public Flux<ChavePix> findAll() {
        return pixRepository.findAll();
    }
}
