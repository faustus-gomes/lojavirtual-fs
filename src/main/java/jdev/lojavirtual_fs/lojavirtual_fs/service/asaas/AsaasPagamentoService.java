package jdev.lojavirtual_fs.lojavirtual_fs.service.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AsaasPagamentoService {

    @Value("${asaas.api.base-url:https://api.asaas.com/v3}")
    private String asaasUrl;

    @Value("${asaas.api.access-token}")
    private String asaasApiKey;

    @Autowired
    @Qualifier("asaasRestTemplate")  // ← Adicione esta anotação
    private RestTemplate restTemplate;

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("access_token", asaasApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public AsaasPagamentoResponse criarPagamentoCartao(AsaasPagamentoRequest request) {
        String url = asaasUrl + "/payments";
        HttpEntity<AsaasPagamentoRequest> entity = new HttpEntity<>(request, getHeaders());
        ResponseEntity<AsaasPagamentoResponse> response = restTemplate.postForEntity(url, entity, AsaasPagamentoResponse.class);
        return response.getBody();
    }
}
