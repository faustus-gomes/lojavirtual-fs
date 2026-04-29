package jdev.lojavirtual_fs.lojavirtual_fs.service.getResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse.CampaignDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetResponseService {

    private static final Logger logger = LoggerFactory.getLogger(GetResponseService.class);

    private final RestClient restClient;
    public GetResponseService(RestClient restClient) {
        this.restClient = restClient;
    }
    // Carregandop as Campanhas por API - Aula 14.7
    public List<CampaignDTO> getCampaigns() {
        logger.info("Carregando campanhas da API...");

        try {
            String response = restClient.get()
                    .uri("/campaigns")
                    .retrieve()
                    .body(String.class);

            //Converte a resposta Json para lista de CampaignDTO
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonArray = mapper.readTree(response);
            List<CampaignDTO> campaigns = new ArrayList<>();

            for (JsonNode node : jsonArray) {
                CampaignDTO campaign = new CampaignDTO(
                    node.get("campaignId").asText(),
                    node.get("name").asText(),
                    node.has("description") ? node.get("description").asText() : "",
                    node.has("isDefault") ? node.get("isDefault").asBoolean() : false
                );
                campaigns.add(campaign);
            }

            logger.info("Encontradas {} campanhas", campaigns.size());
            return  campaigns;
        } catch (Exception e) {
            logger.error("Erro ao carregar campanhas: {}", e.getMessage());
            throw new RuntimeException("Falha ao carregar campanhas", e);
        }

    }
}
