package jdev.lojavirtual_fs.lojavirtual_fs.service.getResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

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

    // ===========================================
// AULA 14.10 - POST API enviando e-mail
// ===========================================

    /**
     * Envia uma newsletter/campanha de e-mail
     * @param newsletterRequest DTO com os dados do e-mail
     * @return Resposta da API com o ID da newsletter
     */
    public NewsletterResponseDTO sendNewsletter(NewsletterRequestDTO newsletterRequest) {
        logger.info("Aula 14.10 - Enviando newsletter: {}", newsletterRequest.getName());
        logger.info("Assunto: {}", newsletterRequest.getSubject());

        try {
            // Monta o corpo da requisição no formato que o GetResponse espera
            Map<String, Object> requestBody = new LinkedHashMap<>();

            // Informações básicas
            requestBody.put("name", newsletterRequest.getName());
            requestBody.put("subject", newsletterRequest.getSubject());

            // Conteúdo do e-mail
            Map<String, String> content = new HashMap<>();
            content.put("html", newsletterRequest.getContent().getHtml());
            if (newsletterRequest.getContent().getText() != null) {
                content.put("text", newsletterRequest.getContent().getText());
            }
            if (newsletterRequest.getContent().getSubject() != null) {
                content.put("subject", newsletterRequest.getContent().getSubject());
            }
            requestBody.put("content", content);

            // Destinatários (campanhas/listas)
            if (newsletterRequest.getCampaignIds() != null && !newsletterRequest.getCampaignIds().isEmpty()) {
                Map<String, String> campaign = new HashMap<>();
                campaign.put("campaignId", newsletterRequest.getCampaignIds().get(0));
                requestBody.put("campaign", campaign);
            }

            // Destinatários específicos (e-mails individuais)
            if (newsletterRequest.getRecipients() != null && !newsletterRequest.getRecipients().isEmpty()) {
                List<Map<String, String>> recipients = new ArrayList<>();
                for (EmailRecipientDTO recipient : newsletterRequest.getRecipients()) {
                    Map<String, String> rec = new HashMap<>();
                    if (recipient.getContactId() != null) {
                        rec.put("contactId", recipient.getContactId());
                    }
                    if (recipient.getEmail() != null) {
                        rec.put("email", recipient.getEmail());
                    }
                    recipients.add(rec);
                }
                requestBody.put("recipients", recipients);
            }

            // Configurações de tracking (rastreamento)
            if (newsletterRequest.getTrackOpens() != null) {
                requestBody.put("trackOpens", newsletterRequest.getTrackOpens());
            }
            if (newsletterRequest.getTrackClicks() != null) {
                requestBody.put("trackClicks", newsletterRequest.getTrackClicks());
            }

            // Configurações do remetente
            if (newsletterRequest.getFrom() != null) {
                requestBody.put("from", newsletterRequest.getFrom());
            }

            if (newsletterRequest.getReplyTo() != null) {
                requestBody.put("replyTo", newsletterRequest.getReplyTo());
            }

            // Log para debug
            logger.debug("Corpo da requisição: {}", requestBody);

            // Faz a requisição POST para /newsletters
            NewsletterResponseDTO response = restClient.post()
                    .uri("/newsletters")
                    .body(requestBody)
                    .retrieve()
                    .body(NewsletterResponseDTO.class);

            logger.info("✅ Newsletter enviada com sucesso!");
            logger.info("ID da newsletter: {}", response != null ? response.getNewsletterId() : "null");
            logger.info("Status: {}", response != null ? response.getStatus() : "null");

            return response;

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar newsletter: {}", e.getMessage());
            throw new RuntimeException("Falha ao enviar newsletter: " + e.getMessage(), e);
        }
    }

    /**
     * Versão simplificada para enviar e-mail para um único destinatário
     * @param toEmail E-mail do destinatário
     * @param subject Assunto do e-mail
     * @param htmlContent Conteúdo HTML do e-mail
     * @return Resposta da API
     */
    /**
     * Versão simplificada para enviar e-mail para um único destinatário
     * @param toEmail E-mail do destinatário
     * @param subject Assunto do e-mail
     * @param htmlContent Conteúdo HTML do e-mail
     * @return Resposta da API
     */
    /**
     * Versão simplificada para enviar e-mail para um único destinatário
     * Usando o endpoint de e-mail transacional
     */
    public NewsletterResponseDTO sendSimpleEmail(String toEmail, String subject, String htmlContent, String campaignId) {
        logger.info("Enviando e-mail para: {} na campanha: {}", toEmail, campaignId);

        try {
            // Buscar ou criar o lead
            LeadResponseDTO lead = getOrCreateLead(toEmail, campaignId);

            if (lead == null || lead.getContactId() == null) {
                throw new RuntimeException("Não foi possível obter o lead");
            }

            logger.info("Lead obtido: {}", lead.getContactId());

            // ===== CORPO DA REQUISIÇÃO COMPLETO E CORRETO =====
            Map<String, Object> requestBody = new LinkedHashMap<>();

            // 1. Nome da newsletter
            requestBody.put("name", "E-mail para: " + toEmail);

            // 2. Assunto
            requestBody.put("subject", subject);

            // 3. Conteúdo
            Map<String, String> content = new HashMap<>();
            content.put("html", htmlContent);
            requestBody.put("content", content);

            // 4. fromFieldId - USANDO O VALOR CORRETO DA SUA CONTA
            Map<String, String> fromField = new HashMap<>();
            fromField.put("fromFieldId", "qSi3R");  // ← SEU fromFieldId padrão
            requestBody.put("fromField", fromField);

            // 5. Campaign (obrigatório)
            Map<String, String> campaign = new HashMap<>();
            campaign.put("campaignId", campaignId);
            requestBody.put("campaign", campaign);

            // 6. Configuração de envio
            Map<String, Object> sendSettings = new HashMap<>();
            sendSettings.put("selectedContacts", List.of(lead.getContactId()));
            requestBody.put("sendSettings", sendSettings);

            // 7. Tracking
            requestBody.put("trackOpens", true);
            requestBody.put("trackClicks", true);

            logger.debug("Corpo da requisição: {}", requestBody);

            // Envia a requisição
            NewsletterResponseDTO response = restClient.post()
                    .uri("/newsletters")
                    .body(requestBody)
                    .retrieve()
                    .body(NewsletterResponseDTO.class);

            logger.info("✅ E-mail enviado com sucesso para: {}", toEmail);
            return response;

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar e-mail: {}", e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage(), e);
        }
    }

    // 🔧 NOVO MÉTODO: Cria ou busca o lead (trata o erro 409)
    private LeadResponseDTO getOrCreateLead(String email, String campaignId) {
        logger.info("Buscando ou criando lead: {} na campanha: {}", email, campaignId);

        try {
            // Tenta criar o lead
            LeadRequestDTO leadRequest = new LeadRequestDTO();
            leadRequest.setEmail(email);
            leadRequest.setCampaignId(campaignId);

            return createLead(leadRequest);

        } catch (Exception e) {
            // Se for erro 409 (lead já existe), busca o lead existente
            if (e.getMessage().contains("409") || e.getMessage().contains("Contact already added")) {
                logger.info("Lead já existe, buscando informações...");
                return findLeadByEmailOnly(email);
            }
            throw e;
        }
    }

    // 🔧 NOVO MÉTODO: Buscar lead por e-mail na campanha
    // 🔧 MÉTODO CORRIGIDO: Buscar lead por e-mail
    private LeadResponseDTO findLeadByEmail(String email, String campaignId) {
        logger.info("Buscando lead por e-mail: {} na campanha: {}", email, campaignId);

        try {
            // Busca contatos com o e-mail específico
            String response = restClient.get()
                    .uri("/contacts?query[email]=" + email)
                    .retrieve()
                    .body(String.class);

            logger.debug("Resposta da busca: {}", response);

            // Parse da resposta
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);

            // A resposta pode ser um array ou um objeto com lista
            JsonNode contactsArray = null;

            if (rootNode.isArray()) {
                contactsArray = rootNode;
            } else if (rootNode.has("result")) {
                contactsArray = rootNode.get("result");
            } else if (rootNode.has("contacts")) {
                contactsArray = rootNode.get("contacts");
            } else {
                // Tenta ver se é um objeto único
                if (rootNode.has("contactId")) {
                    contactsArray = mapper.createArrayNode().add(rootNode);
                }
            }

            if (contactsArray != null && contactsArray.isArray() && !contactsArray.isEmpty()) {
                // Procura o contato que pertence à campanha desejada
                for (JsonNode lead : contactsArray) {
                    String leadCampaignId = null;

                    // Extrai o campaignId de diferentes estruturas possíveis
                    if (lead.has("campaign") && lead.get("campaign").has("campaignId")) {
                        leadCampaignId = lead.get("campaign").get("campaignId").asText();
                    } else if (lead.has("campaignId")) {
                        leadCampaignId = lead.get("campaignId").asText();
                    }

                    // Se encontrou na campanha correta, retorna
                    if (leadCampaignId != null && leadCampaignId.equals(campaignId)) {
                        LeadResponseDTO leadResponse = new LeadResponseDTO();

                        if (lead.has("contactId")) {
                            leadResponse.setContactId(lead.get("contactId").asText());
                        }
                        if (lead.has("email")) {
                            leadResponse.setEmail(lead.get("email").asText());
                        }
                        if (lead.has("name")) {
                            leadResponse.setName(lead.get("name").asText());
                        }
                        if (lead.has("status")) {
                            leadResponse.setStatus(lead.get("status").asText());
                        }

                        leadResponse.setCampaignId(campaignId);

                        logger.info("Lead encontrado: {}", leadResponse.getContactId());
                        return leadResponse;
                    }
                }
            }

            // Se não encontrou, tenta buscar apenas por email sem filtro de campanha
            logger.warn("Lead não encontrado na campanha específica, tentando busca geral...");
            return findLeadByEmailOnly(email);

        } catch (Exception e) {
            logger.error("Erro ao buscar lead: {}", e.getMessage());
            throw new RuntimeException("Não foi possível recuperar o lead existente: " + e.getMessage(), e);
        }
    }

    // Método auxiliar: busca lead apenas por email (sem filtrar campanha)
    private LeadResponseDTO findLeadByEmailOnly(String email) {
        try {
            String response = restClient.get()
                    .uri("/contacts?query[email]=" + email)
                    .retrieve()
                    .body(String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);

            // Pega o primeiro contato encontrado
            JsonNode contact = null;
            if (rootNode.isArray() && !rootNode.isEmpty()) {
                contact = rootNode.get(0);
            } else if (rootNode.has("result") && rootNode.get("result").isArray() && !rootNode.get("result").isEmpty()) {
                contact = rootNode.get("result").get(0);
            }

            if (contact != null) {
                LeadResponseDTO leadResponse = new LeadResponseDTO();
                leadResponse.setContactId(contact.has("contactId") ? contact.get("contactId").asText() : null);
                leadResponse.setEmail(contact.has("email") ? contact.get("email").asText() : null);
                leadResponse.setName(contact.has("name") ? contact.get("name").asText() : null);
                leadResponse.setStatus(contact.has("status") ? contact.get("status").asText() : null);

                logger.info("Lead encontrado na busca geral: {}", leadResponse.getContactId());
                return leadResponse;
            }

            throw new RuntimeException("Lead não encontrado");

        } catch (Exception e) {
            logger.error("Erro na busca geral: {}", e.getMessage());
            throw new RuntimeException("Não foi possível encontrar o lead", e);
        }
    }

    /**
     * Estratégia: 1. Criar lead, 2. Enviar e-mail para o lead
     */
    public NewsletterResponseDTO sendEmailToExistingLead(String email, String subject, String htmlContent, String campaignId) {
        logger.info("Enviando e-mail para lead: {}", email);

        try {
            // Passo 1: Criar ou buscar o lead
            LeadRequestDTO leadRequest = new LeadRequestDTO();
            leadRequest.setEmail(email);
            leadRequest.setCampaignId(campaignId);

            LeadResponseDTO lead = null;
            try {
                lead = createLead(leadRequest);
                logger.info("Lead obtido: {}", lead != null ? lead.getContactId() : "null");
            } catch (Exception e) {
                logger.error("Erro ao obter lead: {}", e.getMessage());
                throw new RuntimeException("Não foi possível encontrar/criar o lead");
            }

            if (lead == null || lead.getContactId() == null) {
                throw new RuntimeException("Lead não encontrado ou não pôde ser criado");
            }

            // Passo 2: Monta requisição para enviar e-mail para o contactId
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("name", "E-mail para: " + email);
            requestBody.put("subject", subject);

            // Conteúdo
            Map<String, String> content = new HashMap<>();
            content.put("html", htmlContent);
            requestBody.put("content", content);

            // Envia para o contato específico
            Map<String, Object> sendSettings = new HashMap<>();
            sendSettings.put("selectedContacts", List.of(lead.getContactId())); // Aqui vai o contactId, não o email!
            requestBody.put("sendSettings", sendSettings);

            requestBody.put("trackOpens", true);
            requestBody.put("trackClicks", true);

            logger.info("Enviando newsletter para contactId: {}", lead.getContactId());

            NewsletterResponseDTO response = restClient.post()
                    .uri("/newsletters")
                    .body(requestBody)
                    .retrieve()
                    .body(NewsletterResponseDTO.class);

            logger.info("✅ E-mail enviado com sucesso!");
            return response;

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar e-mail: {}", e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> getFromFields() {
        logger.info("Buscando From Fields disponíveis...");

        try {
            List<Map<String, Object>> response = restClient.get()
                    .uri("/from-fields")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

            logger.info("✅ From Fields encontrados: {}", response);
            return response;

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar From Fields: {}", e.getMessage());
            return List.of();
        }
    }

}
