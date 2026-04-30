package jdev.lojavirtual_fs.lojavirtual_fs.service.getResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse.CampaignResponse;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse.LeadRequestDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse.LeadResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailMarketingService {
    private static final Logger logger = LoggerFactory.getLogger(EmailMarketingService.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;


    public EmailMarketingService(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    // ===========================================
    // AULA 14.7 - Carregando as campanhas por API
    // ===========================================

    /**
     * Método principal - Retorna lista de campanhas usando ParameterizedTypeReference
     */
    public List<CampaignResponse> getAllCampaigns() {
        logger.info("Buscando todas as campanhas...");

        try {
            List<CampaignResponse> campaigns = restClient.get()
                    .uri("/campaigns")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CampaignResponse>>() {});

            logger.info("✅ Sucesso! Encontradas {} campanhas.", campaigns != null ? campaigns.size() : 0);

            // Log de cada campanha encontrada
            if (campaigns != null) {
                for (CampaignResponse campaign : campaigns) {
                    logger.debug("Campanha: ID={}, Nome={}", campaign.getId(), campaign.getName());
                }
            }

            return campaigns;

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar campanhas: {}", e.getMessage());
            throw new RuntimeException("Falha ao carregar campanhas do GetResponse", e);
        }
    }

    /**
     * Método alternativo - Retorna como Array (útil se List estiver dando problema)
     */
    public CampaignResponse[] getCampaignsAsArray() {
        logger.info("Buscando campanhas como Array...");

        try {
            CampaignResponse[] campaigns = restClient.get()
                    .uri("/campaigns")
                    .retrieve()
                    .body(CampaignResponse[].class);

            logger.info("✅ Encontradas {} campanhas", campaigns != null ? campaigns.length : 0);
            return campaigns;

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar campanhas: {}", e.getMessage());
            return new CampaignResponse[0];
        }
    }

    /**
     * Método para debug - Retorna o JSON bruto da resposta
     */
    public String getCampaignsRawJson() {
        logger.info("Buscando JSON bruto das campanhas...");

        try {
            String jsonResponse = restClient.get()
                    .uri("/campaigns")
                    .retrieve()
                    .body(String.class);

            logger.info("✅ JSON recebido: {}", jsonResponse);
            return jsonResponse;

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar JSON: {}", e.getMessage());
            return "Erro: " + e.getMessage();
        }
    }

    /**
     * Método para buscar uma campanha específica por ID
     */
    public CampaignResponse getCampaignById(String campaignId) {
        logger.info("Buscando campanha por ID: {}", campaignId);

        try {
            CampaignResponse campaign = restClient.get()
                    .uri("/campaigns/{campaignId}", campaignId)
                    .retrieve()
                    .body(CampaignResponse.class);

            logger.info("✅ Campanha encontrada: {}", campaign != null ? campaign.getName() : "null");
            return campaign;

        } catch (Exception e) {
            logger.error("❌ Campanha não encontrada: {}", campaignId);
            return null;
        }
    }

    // ===========================================
    // AULA 14.8 - Post em API cadastro de Lead no e-mail
    // ===========================================

    public LeadResponseDTO createLead(LeadRequestDTO leadRequest) {
        logger.info("Aula 14.8 - Criando lead: {}", leadRequest.getEmail());
        logger.info("Dados do lead: {}", leadRequest);

        try {
            // Monta o corpo da requisição no formato que o GetResponse espera
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("email", leadRequest.getEmail());
            requestBody.put("campaign", Map.of("campaignId", leadRequest.getCampaignId()));

            // Adiciona nome se foi fornecido
            if (leadRequest.getName() != null && !leadRequest.getName().isEmpty()) {
                requestBody.put("name", leadRequest.getName());
            }

            // Log para debug
            logger.debug("Corpo da requisição: {}", requestBody);

            // Faz a requisição POST para /contacts
            LeadResponseDTO response = restClient.post()
                    .uri("/contacts")
                    .body(requestBody)
                    .retrieve()
                    .body(LeadResponseDTO.class);

            logger.info("✅ Lead criado com sucesso!");
            logger.info("ID do contato: {}", response != null ? response.getContactId() : "null");
            logger.info("Status: {}", response != null ? response.getStatus() : "null");

            return response;

        } catch (Exception e) {
            logger.error("❌ Erro ao criar lead: {}", e.getMessage());
            throw new RuntimeException("Falha ao criar lead: " + e.getMessage(), e);
        }
    }

}
