package com.letscode.itau.bancoitau.service;

import com.google.gson.Gson;
import com.letscode.itau.bancoitau.dto.CadastroBacenDTORequest;
import com.letscode.itau.bancoitau.dto.CadastroBacenDTOResponse;
import com.letscode.itau.bancoitau.dto.ChavePixDTO;
import com.letscode.itau.bancoitau.enumeration.Status;
import com.letscode.itau.bancoitau.enumeration.TipoChavePix;
import com.letscode.itau.bancoitau.model.ChavePix;
import com.letscode.itau.bancoitau.repository.ChavePixRepository;
import com.letscode.itau.bancoitau.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
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





    private void solicitarCadastroBacen(Long idRequisicao, String numeroConta, String agencia, String chave) {
        CadastroBacenDTORequest requestBacen = new CadastroBacenDTORequest(idRequisicao, chave, TipoChavePix.CPF, agencia, numeroConta);
        String mensagem = new Gson().toJson(requestBacen);
        enviaMensagemKafka(mensagem);
    }


    private Mono<ResponseEntity<ChavePix>> salvarChavePix(ChavePixDTO chavePixDTO) {
        ChavePix chavePix = chavePixDTOparaChavePix(chavePixDTO);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        return pixRepository.save(chavePix).map(chave -> ResponseEntity.created(uriComponentsBuilder.path("/api/itau/pix/{chave}").buildAndExpand(chave.getChave()).toUri()).body(chave));
    }

    private Long geraIdRequisicao() {
        return (long) new Random().nextInt(Integer.MAX_VALUE);
    }


    private Long withIdChavePix(ChavePixDTO chavePixDTO) {
        Long idRequisicao = geraIdRequisicao();
        chavePixDTO.setReqId(idRequisicao);
        return idRequisicao;
    }

    public Mono<ResponseEntity<ChavePix>> findByChave(String chave) {
        return pixRepository.findByChave(chave).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public Flux<ChavePix> findAll() {
        return pixRepository.findAll();
    }

    @KafkaListener(id = "myId", topics = "confirmacao-cadastro-chavepix-itau")
    public void getStatusBacen(String mensagem) {
        CadastroBacenDTOResponse cadastroBacenDTOResponse = new Gson().fromJson(mensagem, CadastroBacenDTOResponse.class);
        atualizaStatus(cadastroBacenDTOResponse);
    }

    public Mono<ResponseEntity<ChavePix>> cadastrarChavePix(ChavePixDTO chavePixDTO) {
        Long idRequisicao = withIdChavePix(chavePixDTO);
        String conta = chavePixDTO.getRequerente().getConta();
        String agencia = chavePixDTO.getRequerente().getAgencia();
        String cpf = chavePixDTO.getRequerente().getCpf();

        solicitarCadastroBacen(idRequisicao, conta, agencia, cpf);

        return salvarChavePix(chavePixDTO);
    }

    private void atualizaStatus(CadastroBacenDTOResponse cadastroBacenDTOResponse) {
        if (Status.Aceito.equals(cadastroBacenDTOResponse.getStatus())) {
            pixRepository.findByChave(cadastroBacenDTOResponse.getChave()).subscribe(entidade -> {
                entidade.setStatus(Status.Aceito);
                pixRepository.save(entidade).subscribe(e -> {
                    System.out.println("Status atualizado com sucesso " + Status.Aceito);
                });
            });
        } else if (Status.Recusado.equals(cadastroBacenDTOResponse.getStatus())) {
            pixRepository.findByChave(cadastroBacenDTOResponse.getChave()).subscribe(entidade -> {
                entidade.setStatus(Status.Recusado);
                pixRepository.save(entidade).subscribe(e -> {
                    System.out.println("Status atualizado com sucesso " + Status.Recusado);
                });
            });
        }
    }


    private void enviaMensagemKafka(String mensagem) {
        kafkaTemplate.send("itau-cadastro-chavepix-solicitacao", mensagem);
    }


    private ChavePix chavePixDTOparaChavePix(ChavePixDTO chavePixDTO) {
        return new ChavePix(chavePixDTO.getReqId(), chavePixDTO.getTipoDeChave(), chavePixDTO.getRequerente().getAgencia(), chavePixDTO.getRequerente().getConta(), chavePixDTO.getRequerente().getCpf(), Status.Pendente);
    }
}
