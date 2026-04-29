package jdev.lojavirtual_fs.lojavirtual_fs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class GetResponseConfig {

    @Value("${getresponse.token}")
    private String token;

    @Value("${getresponse.api.url}")
    private String apiUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("X-Auth-Token", token)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
