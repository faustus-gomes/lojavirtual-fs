package jdev.lojavirtual_fs.lojavirtual_fs.service.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasCobrancaRequest;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasCobrancaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AsaasCobrancaService {
    @Autowired
    @Qualifier("asaasRestTemplate")
    private RestTemplate asaasRestTemplate;

    @Value("${asaas.api.access-token}")
    private String apiKey;

    @Value("${asaas.api.base-url}")
    private String asaasUrl;

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("access_token", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;

    }

    /**
     * Cria uma cobrança no Asaas (PIX ou BOLETO)
     */
    public AsaasCobrancaResponse criarCobranca(AsaasCobrancaRequest request) {
        String url = asaasUrl + "/payments";
        log.info("Criando cobrança no Asaas. URL: {}", url);
        log.info("Request: {}", request);

        HttpHeaders headers = getHeaders();
        HttpEntity<AsaasCobrancaRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<AsaasCobrancaResponse> response = asaasRestTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                AsaasCobrancaResponse.class
        );

        log.info("Cobrança criada. ID: {}, Status: {}", response.getBody().getId(), response.getBody().getStatus());
        return response.getBody();
    }

    /**
     * Busca cobrança por ID
     */
    public AsaasCobrancaResponse buscarCobranca(String paymentId) {
        String url = asaasUrl + "/payments/" + paymentId;
        log.info("Buscando cobrança. URL: {}", url);

        HttpHeaders headers = getHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<AsaasCobrancaResponse> response = asaasRestTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                AsaasCobrancaResponse.class
        );

        return response.getBody();
    }
}
