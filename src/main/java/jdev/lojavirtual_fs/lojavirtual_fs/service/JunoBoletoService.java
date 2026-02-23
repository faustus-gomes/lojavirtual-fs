package jdev.lojavirtual_fs.lojavirtual_fs.service;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jdev.lojavirtual_fs.lojavirtual_fs.model.AccessTokenJunoApi;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.AccesTokenJunoRepository;
import org.glassfish.jersey.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Base64;

@Service
public class JunoBoletoService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Autowired
    private AccessTokenJunoService accessTokenJunoService;
    @Autowired
    private AccesTokenJunoRepository accesTokenJunoRepository;
    public AccessTokenJunoApi obterTokebApiJuno() throws Exception {

         AccessTokenJunoApi accessTokenJunoApi = accessTokenJunoService.buscaTokenAtivo();

         if (accessTokenJunoApi == null || (accessTokenJunoApi != null && accessTokenJunoApi.expirado())) {

             String clienteID = "vi7QZerW09C8JG1o";
             String secretID = "$A_+&ksH}&+2<3VM]1MZqc,F_xif_-Dc";

             // Cliente customozado que ignora o hostname
             HostIgnoringClient hostIgnoringClient = new HostIgnoringClient("https://api.juno.com.br/");
             Client client = hostIgnoringClient.hostIgnoringClient();

             try {
                 String basicChave = clienteID + ":" + secretID;
                 String tokenAutenticacao = Base64.getEncoder().encodeToString(basicChave.getBytes());

                 // Criando o formulário com o grant_type
                 Form form = new Form();
                 form.param("grant_type", "client_credentials");

                 // Fazendo a requisição POST
                 Response response = client.target("https://api.juno.com.br/authorization-server/oauth/token")
                         .request(MediaType.APPLICATION_FORM_URLENCODED)
                         .header(HttpHeaders.AUTHORIZATION, "Basic " + tokenAutenticacao)
                         .post(Entity.form(form));

                 if (response.getStatus() == 200) {
                     // Limpa tokens antigos
                     accesTokenJunoRepository.deleteAll();
                     accesTokenJunoRepository.flush();

                     // Converte a resposta para o objeto
                     AccessTokenJunoApi accessTokenJunoAPI2 = response.readEntity(AccessTokenJunoApi.class);
                     accessTokenJunoAPI2.setToken_acesso(tokenAutenticacao);

                     // Salva o novo token
                     accessTokenJunoAPI2 = accesTokenJunoRepository.saveAndFlush(accessTokenJunoAPI2);

                     return accessTokenJunoAPI2;
                 } else {
                     // Log do erro para debug
                     String errorResponse = response.readEntity(String.class);
                     System.err.println("Erro na autenticação: " + response.getStatus() + " - " + errorResponse);
                     return null;
                 }
             } finally {
                 // IMPORTANTE: Fecha o cliente para liberar recursos
                 client.close();
             }
         } else {
             return accessTokenJunoApi;
         }

    }

}
