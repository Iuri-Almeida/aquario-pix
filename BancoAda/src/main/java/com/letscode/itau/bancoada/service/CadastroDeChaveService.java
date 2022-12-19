package com.letscode.itau.bancoada.service;

import com.google.gson.Gson;
import com.letscode.itau.bancoada.dto.CadastroBacenDTORequest;
import com.letscode.itau.bancoada.dto.CadastroBacenDTOResponse;
import com.letscode.itau.bancoada.dto.ChavePixDTO;
import com.letscode.itau.bancoada.enumeration.Status;
import com.letscode.itau.bancoada.enumeration.TipoChavePix;
import com.letscode.itau.bancoada.kafka.producer.AdaKafkaProducer;
import com.letscode.itau.bancoada.model.ChavePix;
import com.letscode.itau.bancoada.repository.ChavePixRepository;
import com.letscode.itau.bancoada.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class CadastroDeChaveService {
    private final AdaKafkaProducer adaKafkaProducer;
    private final ChavePixRepository pixRepository;
    private final ContaRepository contaRepository;

    public void conferirRequerente(String numeroConta, String agencia) {
        contaRepository.findByNumeroContaAndAgencia(numeroConta, agencia).subscribe();
    }

    public void solicitarCadastroBacen(Long idRequisicao, String numeroConta, String agencia, String chave) {
        CadastroBacenDTORequest requestBacen = new CadastroBacenDTORequest(idRequisicao, chave, TipoChavePix.CPF, agencia, numeroConta);

        String mensagem = this.object2Json(requestBacen);

        this.enviaMensagemKafka(mensagem);
    }

    public Mono<ResponseEntity<ChavePix>> salvarChavePix(ChavePixDTO chavePixDTO) {
        ChavePix chavePix = this.chavePixDTOparaChavePix(chavePixDTO);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();

        return pixRepository.save(chavePix).map(chave -> ResponseEntity.created(uriComponentsBuilder.path("/api/ada/pix/{chave}").buildAndExpand(chave.getChave()).toUri()).body(chave));
    }

    public Long withIdChavePix(ChavePixDTO chavePixDTO) {
        Long idRequisicao = this.geraIdRequisicao();

        chavePixDTO.setReqId(idRequisicao);

        return idRequisicao;
    }

    public Flux<ChavePix> findAll() {
        return pixRepository.findAll();
    }

    public Mono<ResponseEntity<ChavePix>> findByChave(String chave) {
        return pixRepository.findByChave(chave).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public void getStatusBacen(CadastroBacenDTOResponse cadastroBacenDTOResponse) {
        this.atualizaStatus(cadastroBacenDTOResponse);
    }

    private void atualizaStatus(CadastroBacenDTOResponse cadastroBacenDTOResponse) {
        if (this.isStatusAceito(cadastroBacenDTOResponse.getStatus())) {
            pixRepository.findByChave(cadastroBacenDTOResponse.getChave())
                    .subscribe(entidade -> {
                        entidade.setStatus(Status.Aceito);
                        pixRepository.save(entidade).subscribe(e -> System.out.println("Status atualizado com sucesso " + Status.Aceito));
                    });
        } else if (Status.Recusado.equals(cadastroBacenDTOResponse.getStatus())) {
            pixRepository.findByChave(cadastroBacenDTOResponse.getChave())
                    .subscribe(entidade -> {
                        entidade.setStatus(Status.Recusado);
                        pixRepository.save(entidade).subscribe(e -> System.out.println("Status atualizado com sucesso " + Status.Recusado));
                    });
        }
    }

    private void enviaMensagemKafka(String mensagem) {
        adaKafkaProducer.publicar("ada-cadastro-chavepix-solicitacao", mensagem);
    }

    private ChavePix chavePixDTOparaChavePix(ChavePixDTO chavePixDTO) {
        return new ChavePix(chavePixDTO.getReqId(), chavePixDTO.getTipoDeChave(), chavePixDTO.getRequerente().getAgencia(), chavePixDTO.getRequerente().getConta(), chavePixDTO.getRequerente().getCpf(), Status.Pendente);
    }

    private String object2Json(Object obj) {
        return new Gson().toJson(obj);
    }

    private Long geraIdRequisicao() {
        return (long) new Random().nextInt(Integer.MAX_VALUE);
    }

    private boolean isStatusAceito(Status status) {
        return Status.Aceito.equals(status);
    }

}
