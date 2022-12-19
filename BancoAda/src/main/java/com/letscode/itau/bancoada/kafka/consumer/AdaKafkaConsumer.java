package com.letscode.itau.bancoada.kafka.consumer;

import com.google.gson.Gson;
import com.letscode.itau.bancoada.dto.CadastroBacenDTOResponse;
import com.letscode.itau.bancoada.dto.PixDTOResponse;
import com.letscode.itau.bancoada.dto.PixSolicitacaoDTORequest;
import com.letscode.itau.bancoada.service.CadastroDeChaveService;
import com.letscode.itau.bancoada.service.PixService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdaKafkaConsumer {

    private final PixService pixService;
    private final CadastroDeChaveService cadastroDeChaveService;

    @KafkaListener(id = "myId", topics = "confirmacao-cadastro-chavepix-ada")
    public void getStatusBacen(String mensagem) {
        CadastroBacenDTOResponse cadastroBacenDTOResponse = this.json2CadastroBacenDTOResponse(mensagem);
        this.cadastroDeChaveService.getStatusBacen(cadastroBacenDTOResponse);
    }

    @KafkaListener(groupId = "myId3", topics = "pix-confirmacao-ada")
    public void listenPixConfirmacaoAda(String msg) {
        PixDTOResponse pixDTOResponse = this.json2PixDTOResponse(msg);
        this.pixService.getStatusBacenPix(pixDTOResponse);
    }

    @KafkaListener(groupId = "myId4", topics = "pix-solicitacao-ada")
    public void listenPixSolicitacaoAda(String msg) {
        PixSolicitacaoDTORequest pixSolicitacaoDTORequest = this.json2PixSolicitacaoDTORequest(msg);
        this.pixService.getSolicitacaoPix(pixSolicitacaoDTORequest);
    }

    private CadastroBacenDTOResponse json2CadastroBacenDTOResponse(String json) {
        return new Gson().fromJson(json, CadastroBacenDTOResponse.class);
    }

    private PixDTOResponse json2PixDTOResponse(String json) {
        return new Gson().fromJson(json, PixDTOResponse.class);
    }

    private PixSolicitacaoDTORequest json2PixSolicitacaoDTORequest(String json) {
        return new Gson().fromJson(json, PixSolicitacaoDTORequest.class);
    }

}
