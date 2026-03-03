package jdev.lojavirtual_fs.lojavirtual_fs.service.asaas;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class AsaasHeaderBuilderService {
    /**
     13.10 - Definindo Header do Post para Nota Fiscal  * */
    private final String accessToken;


    public AsaasHeaderBuilderService(@Value("${asaas.api.access-token}") String accessToken) {
        this.accessToken = accessToken;
    }

    public HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("access_token", accessToken);// Header específico do Asaas
        return headers;
    }

    public HttpHeaders buildHeadersWithIdempotency(String idempotencyKey) {
        HttpHeaders headers = buildHeaders();
        headers.set("Idempotency-Key", idempotencyKey); // Para evitar duplicidade
        return headers;
    }
}
