package jdev.lojavirtual_fs.lojavirtual_fs.config;

import org.glassfish.jersey.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class GetResponseConfig {

    @Value("${getresponse.token}")
    private String token;

    @Value("${getresponse.api.url}")
    private String apiUrl;

    @Bean
    public RestClient restClient() {
        // 🔑 AQUI ESTÁ A CORREÇÃO: adicionar o prefixo "api-key "
        String authHeader = "api-key " + token;
        System.out.println("🔑 Header de autenticação: " + authHeader);

        return RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Auth-Token", authHeader)  // ← FORMATO CORRETO
                .build();
    }
}
