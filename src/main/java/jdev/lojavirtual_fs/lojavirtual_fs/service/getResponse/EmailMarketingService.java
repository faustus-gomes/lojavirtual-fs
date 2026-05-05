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
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.time.Duration;

@Service
public class EmailMarketingService {
    private static final Logger logger = LoggerFactory.getLogger(EmailMarketingService.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    // ===========================================
    // AULA 14.11 - Service e método de lista de campanha
    // ===========================================

    //Cache para armazenar campanhas (evita chamadas  repetidas à API)
    private List<CampaignResponse> cachedCampaigns;
    private LocalDateTime cacheTimestamp;
    private static final long CACHE_DURATION_MINUTES = 10; //Cache por 10 minutos


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
    public List<CampaignResponse> getAllCampaigns(boolean forceRefresh) {
        logger.info("Aula 14.11 - Buscando campanhas...");

        // Verifica se o cache é válido e não precisa atualizar
        if (!forceRefresh && isCacheValid()) {
            logger.info("Buscando campanhas (forceRefresh={})", forceRefresh);
            return cachedCampaigns;
        }

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
     * Busca campanhas diretamente da API com paginação
     */
    private List<CampaignResponse> fetchCampaignsFromApi() {
        logger.info("Buscando campanhas da API GetResponse...");

        List<CampaignResponse> allCampaigns = new ArrayList<>();
        int page = 1;
        int perPage = 100;
        boolean hasMore = true;

        while (hasMore) {
            try {
                // Constrói a URL com parâmetros
                String url = UriComponentsBuilder.fromPath("/campaigns")
                        .queryParam("page", page)
                        .queryParam("perPage", perPage)
                        .build()
                        .toString();

                logger.debug("URL da requisição: {}", url);

                // Faz a requisição
                String response = restClient.get()
                        .uri(url)
                        .retrieve()
                        .body(String.class);

                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonArray = mapper.readTree(response);

                if (jsonArray.isArray() && jsonArray.size() > 0) {
                    for (JsonNode node : jsonArray) {
                        CampaignResponse campaign = new CampaignResponse();
                        campaign.setId(node.get("campaignId").asText());
                        campaign.setName(node.get("name").asText());
                        campaign.setDescription(node.has("description") ? node.get("description").asText() : "");
                        campaign.setDefaultCampaign(node.has("isDefault") && node.get("isDefault").asBoolean());
                        allCampaigns.add(campaign);
                    }

                    // Se veio menos que perPage, é a última página
                    if (jsonArray.size() < perPage) {
                        hasMore = false;
                    } else {
                        page++;
                    }
                } else {
                    hasMore = false;
                }

            } catch (Exception e) {
                logger.error("Erro na página {}: {}", page, e.getMessage());
                hasMore = false;
            }
        }

        logger.info("Total de campanhas encontradas: {}", allCampaigns.size());
        return allCampaigns;
    }

    /**
     * Verifica se o cache ainda é válido
     */
    private boolean isCacheValid() {
        if (cachedCampaigns == null || cacheTimestamp == null) {
            return false;
        }

        long minutesSinceLastUpdate = Duration.between(cacheTimestamp, LocalDateTime.now()).toMinutes();
        boolean isValid = minutesSinceLastUpdate < CACHE_DURATION_MINUTES;

        if (isValid) {
            logger.debug("Cache válido ({} minutos desde último update)", minutesSinceLastUpdate);
        } else {
            logger.debug("Cache expirado ({} minutos desde último update)", minutesSinceLastUpdate);
        }

        return isValid;
    }

    /**
     * Busca uma campanha específica por ID
     */
    public CampaignResponse getCampaignById(String campaignId) {
        logger.info("Buscando campanha por ID: {}", campaignId);

        // Primeiro tenta do cache
        if (isCacheValid() && cachedCampaigns != null) {
            CampaignResponse cached = cachedCampaigns.stream()
                    .filter(c -> c.getId().equals(campaignId))
                    .findFirst()
                    .orElse(null);

            if (cached != null) {
                logger.info("Campanha encontrada no cache: {}", cached.getName());
                return cached;
            }
        }

        // Se não encontrou no cache, busca diretamente da API
        try {
            CampaignResponse campaign = restClient.get()
                    .uri("/campaigns/{campaignId}", campaignId)
                    .retrieve()
                    .body(CampaignResponse.class);

            logger.info("Campanha encontrada na API: {}", campaign != null ? campaign.getName() : "null");
            return campaign;

        } catch (Exception e) {
            logger.error("Campanha não encontrada: {}", campaignId);
            return null;
        }
    }

    /**
     * Busca campanha padrão (isDefault = true)
     */
    public CampaignResponse getDefaultCampaign() {
        logger.info("Buscando campanha padrão...");

        List<CampaignResponse> campaigns = getAllCampaigns(false);

        CampaignResponse defaultCampaign = campaigns.stream()
                .filter(CampaignResponse::isDefaultCampaign)
                .findFirst()
                .orElse(null);

        if (defaultCampaign == null && !campaigns.isEmpty()) {
            logger.warn("Nenhuma campanha padrão encontrada, usando a primeira da lista");
            defaultCampaign = campaigns.get(0);
        }

        logger.info("Campanha padrão: {}", defaultCampaign != null ? defaultCampaign.getName() : "nenhuma");
        return defaultCampaign;
    }

    /**
     * Busca campanhas por nome (busca parcial)
     */
    public List<CampaignResponse> getCampaignsByName(String nameContains) {
        logger.info("Buscando campanhas que contenham: {}", nameContains);

        List<CampaignResponse> allCampaigns = getAllCampaigns(false);

        List<CampaignResponse> filtered = allCampaigns.stream()
                .filter(c -> c.getName() != null && c.getName().toLowerCase().contains(nameContains.toLowerCase()))
                .collect(Collectors.toList());

        logger.info("Encontradas {} campanhas com o termo '{}'", filtered.size(), nameContains);
        return filtered;
    }

    /**
     * Limpa o cache manualmente (útil após criar/alterar campanhas)
     */
    public void clearCampaignCache() {
        logger.info("Limpando cache de campanhas");
        cachedCampaigns = null;
        cacheTimestamp = null;
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

    // ===========================================
    // AULA 14.8 - Post em API cadastro de Lead no e-mail
    // ===========================================
    // ===========================================
    // AULA 14.12 - Service e método de criar lead na API
    // Ajuste Mais Robustas
    // ===========================================

    /**
     * Cria um novo lead (contato) na campanha especificada
     * @param leadRequest DTO com dados do lead
     * @return LeadResponseDTO com os dados do lead criado/encontrado
     */
    public LeadResponseDTO createLead(LeadRequestDTO leadRequest) {
        logger.info("Aula 14.12 - Criando lead: {}", leadRequest.getEmail());

        // Validações básicas
        if (leadRequest.getEmail() == null || leadRequest.getEmail().isEmpty()) {
            throw new IllegalArgumentException("E-mail é obrigatório");
        }

        if (leadRequest.getCampaignId() == null || leadRequest.getCampaignId().isEmpty()) {
            throw new IllegalArgumentException("CampaignId é obrigatório");
        }

        try {
            // Primeiro, verifica se o lead já existe
            LeadResponseDTO existingLead = findLeadByEmailInCampaign(
                    leadRequest.getEmail(),
                    leadRequest.getCampaignId()
            );

            if (existingLead != null) {
                logger.info("Lead já existe na campanha: {}", existingLead.getContactId());

                // Se tem nome novo, atualiza
                if (leadRequest.getName() != null && !leadRequest.getName().isEmpty()) {
                    logger.info("Atualizando nome do lead...");
                    return updateLeadName(existingLead.getContactId(), leadRequest.getName());
                }

                return existingLead;
            }

            // Lead não existe, vamos criar
            logger.info("Lead não encontrado, criando novo...");

            // Monta o corpo da requisição (APENAS CAMPOS BÁSICOS)
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("email", leadRequest.getEmail());
            requestBody.put("campaign", Map.of("campaignId", leadRequest.getCampaignId()));

            // Adiciona nome se fornecido
            if (leadRequest.getName() != null && !leadRequest.getName().isEmpty()) {
                requestBody.put("name", leadRequest.getName());
            }

            // ⚠️ REMOVER CUSTOM FIELDS E TAGS - eles estão causando erro
            // requestBody.put("customFieldValues", customFields);  // ← REMOVER
            // requestBody.put("tags", tags);  // ← REMOVER

            logger.debug("Corpo da requisição: {}", requestBody);

            // Faz a requisição POST para /contacts
            LeadResponseDTO response = restClient.post()
                    .uri("/contacts")
                    .body(requestBody)
                    .retrieve()
                    .body(LeadResponseDTO.class);

            logger.info("✅ Lead criado com sucesso!");
            logger.info("ID: {}, Status: {}",
                    response != null ? response.getContactId() : "null",
                    response != null ? response.getStatus() : "null");

            return response;

        } catch (Exception e) {
            logger.error("❌ Erro ao criar lead: {}", e.getMessage());

            // Tratamento específico para erro 409 (conflito)
            if (e.getMessage().contains("409") || e.getMessage().contains("Contact already added")) {
                logger.warn("Lead já existe (conflito detectado), tentando recuperar...");
                return findLeadByEmailInCampaign(leadRequest.getEmail(), leadRequest.getCampaignId());
            }

            throw new RuntimeException("Falha ao criar lead: " + e.getMessage(), e);
        }
    }

    /**
     * Busca lead por e-mail em uma campanha específica
     */
    private LeadResponseDTO findLeadByEmailInCampaign(String email, String campaignId) {
        logger.info("Buscando lead: {} na campanha: {}", email, campaignId);

        try {
            // Busca contatos com o e-mail específico
            String response = restClient.get()
                    .uri("/contacts?query[email]=" + email + "&query[campaignId]=" + campaignId)
                    .retrieve()
                    .body(String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonArray = mapper.readTree(response);

            if (jsonArray.isArray() && !jsonArray.isEmpty()) {
                JsonNode lead = jsonArray.get(0);
                LeadResponseDTO leadResponse = new LeadResponseDTO();
                leadResponse.setContactId(lead.has("contactId") ? lead.get("contactId").asText() : null);
                leadResponse.setEmail(lead.has("email") ? lead.get("email").asText() : null);
                leadResponse.setName(lead.has("name") ? lead.get("name").asText() : null);
                leadResponse.setStatus(lead.has("status") ? lead.get("status").asText() : null);
                leadResponse.setCampaignId(campaignId);

                logger.info("Lead encontrado: {}", leadResponse.getContactId());
                return leadResponse;
            }

            logger.info("Lead não encontrado na campanha");
            return null;

        } catch (Exception e) {
            logger.error("Erro ao buscar lead: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Atualiza o nome de um lead existente
     */
    private LeadResponseDTO updateLeadName(String contactId, String newName) {
        logger.info("Atualizando nome do lead: {} -> {}", contactId, newName);

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", newName);

            LeadResponseDTO response = restClient.post()
                    .uri("/contacts/{contactId}", contactId)
                    .body(requestBody)
                    .retrieve()
                    .body(LeadResponseDTO.class);

            logger.info("✅ Nome atualizado com sucesso");
            return response;

        } catch (Exception e) {
            logger.error("Erro ao atualizar nome: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Cria múltiplos leads em lote (batch)
     */
    public List<LeadResponseDTO> createLeadsBatch(List<LeadRequestDTO> leadRequests) {
        logger.info("Criando {} leads em lote", leadRequests.size());

        List<LeadResponseDTO> results = new ArrayList<>();
        List<LeadResponseDTO> errors = new ArrayList<>();

        for (LeadRequestDTO leadRequest : leadRequests) {
            try {
                LeadResponseDTO lead = createLead(leadRequest);
                results.add(lead);
                logger.info("Lead criado: {}", leadRequest.getEmail());
            } catch (Exception e) {
                logger.error("Erro ao criar lead {}: {}", leadRequest.getEmail(), e.getMessage());
                errors.add(new LeadResponseDTO(null, leadRequest.getEmail(), null, "ERROR", null));
            }
        }

        logger.info("Batch concluído: {} sucessos, {} erros", results.size(), errors.size());
        return results;
    }

    /**
     * Remove um lead por ID
     */
    public boolean deleteLead(String contactId) {
        logger.info("Removendo lead: {}", contactId);

        try {
            restClient.delete()
                    .uri("/contacts/{contactId}", contactId)
                    .retrieve()
                    .toBodilessEntity();

            logger.info("✅ Lead removido com sucesso");
            return true;

        } catch (Exception e) {
            logger.error("Erro ao remover lead: {}", e.getMessage());
            return false;
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

    // ===========================================
    // AULA 14.13 - Service e método de enviar e-mail API
    // ===========================================
    /**
     * Envia e-mail para um lead existente (usando contactId)
     */
    public NewsletterResponseDTO sendEmailToLead(String contactId, String subject, String htmlContent) {
        logger.info("Aula 14.13 - Enviando e-mail para lead ID: {}", contactId);

        try {
            // Busca o lead para confirmar que existe
            LeadResponseDTO lead = getLeadById(contactId);
            if (lead == null) {
                throw new RuntimeException("Lead não encontrado com ID: " + contactId);
            }

            // 🔧 CORREÇÃO: Montar requisição completa com campaign
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("name", "E-mail para: " + lead.getEmail());
            requestBody.put("subject", subject);

            // Conteúdo
            Map<String, String> content = new HashMap<>();
            content.put("html", htmlContent);
            requestBody.put("content", content);

            // 🔧 CAMPO OBRIGATÓRIO: fromField
            Map<String, String> fromField = new HashMap<>();
            fromField.put("fromFieldId", getDefaultFromFieldId());
            requestBody.put("fromField", fromField);

            // 🔧 CAMPO OBRIGATÓRIO: campaign
            Map<String, String> campaign = new HashMap<>();
            campaign.put("campaignId", lead.getCampaignId() != null ? lead.getCampaignId() : "C1ZEr");
            requestBody.put("campaign", campaign);

            // Configuração de envio
            Map<String, Object> sendSettings = new HashMap<>();
            sendSettings.put("selectedContacts", List.of(contactId));
            requestBody.put("sendSettings", sendSettings);

            // Tracking
            requestBody.put("trackOpens", true);
            requestBody.put("trackClicks", true);

            logger.debug("Corpo da requisição: {}", requestBody);

            // Envia a requisição
            NewsletterResponseDTO response = restClient.post()
                    .uri("/newsletters")
                    .body(requestBody)
                    .retrieve()
                    .body(NewsletterResponseDTO.class);

            logger.info("✅ E-mail enviado para lead: {}", lead.getEmail());
            return response;

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar e-mail para lead: {}", e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage(), e);
        }
    }

    /**
     * Envia e-mail para um endereço de e-mail específico
     * (cria o lead automaticamente se não existir)
     */
    public NewsletterResponseDTO sendEmailToEmailAddress(String email, String subject, String htmlContent, String campaignId) {
        logger.info("Aula 14.13 - Enviando e-mail para: {} na campanha: {}", email, campaignId);

        // 🔧 VALIDAÇÃO: Verifica se campaignId foi fornecido
        if (campaignId == null || campaignId.isEmpty()) {
            throw new IllegalArgumentException("campaignId é obrigatório para enviar e-mail");
        }

        try {
            // Primeiro, garante que o lead existe
            LeadRequestDTO leadRequest = new LeadRequestDTO();
            leadRequest.setEmail(email);
            leadRequest.setCampaignId(campaignId);

            LeadResponseDTO lead = createLead(leadRequest);

            if (lead == null || lead.getContactId() == null) {
                throw new RuntimeException("Não foi possível criar/obter o lead");
            }

            // Envia o e-mail usando o contactId
            return sendEmailToLead(lead.getContactId(), subject, htmlContent);

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar e-mail: {}", e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage(), e);
        }
    }

    /**
     * Envia e-mail para uma campanha inteira (todos os contatos)
     */
    public NewsletterResponseDTO sendEmailToCampaign(String campaignId, String subject, String htmlContent) {
        logger.info("Aula 14.13 - Enviando e-mail para campanha: {}", campaignId);

        try {
            // Verifica se a campanha existe
            CampaignResponse campaign = getCampaignById(campaignId);
            if (campaign == null) {
                throw new RuntimeException("Campanha não encontrada: " + campaignId);
            }

            // Monta a requisição para enviar para a campanha
            Map<String, Object> requestBody = buildNewsletterRequest(
                    "Newsletter: " + subject,
                    subject,
                    htmlContent,
                    campaignId, // campaignId
                    null, // selectedContacts
                    List.of(campaignId) // selectedCampaigns
            );

            NewsletterResponseDTO response = restClient.post()
                    .uri("/newsletters")
                    .body(requestBody)
                    .retrieve()
                    .body(NewsletterResponseDTO.class);

            logger.info("✅ E-mail enviado para campanha: {}", campaign.getName());
            return response;

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar e-mail para campanha: {}", e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage(), e);
        }
    }

    /**
     * Envia e-mail com template (HTML pré-formatado)
     */
    public NewsletterResponseDTO sendEmailWithTemplate(String toEmail, String templateName,
                                                       Map<String, String> templateVariables,
                                                       String campaignId) {
        logger.info("Aula 14.13 - Enviando e-mail com template: {}", templateName);
        logger.info("Para: {}, Campanha: {}", toEmail, campaignId);

        try {
            // Carrega o template
            String htmlContent = loadTemplate(templateName, templateVariables);
            String subject = getTemplateSubject(templateName);

            // 🔧 CORREÇÃO: Passar o campaignId explicitamente
            return sendEmailToEmailAddress(toEmail, subject, htmlContent, campaignId);

        } catch (Exception e) {
            logger.error("❌ Erro ao enviar e-mail com template: {}", e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail com template: " + e.getMessage(), e);
        }
    }

    /**
     * Envia e-mail agendado para data futura
     */
    public NewsletterResponseDTO sendScheduledEmail(String toEmail, String subject, String htmlContent,
                                                    String campaignId, LocalDateTime scheduleDate) {
        logger.info("Aula 14.13 - Agendando e-mail para: {} em {}", toEmail, scheduleDate);

        try {
            // Primeiro, garante que o lead existe
            LeadRequestDTO leadRequest = new LeadRequestDTO();
            leadRequest.setEmail(toEmail);
            leadRequest.setCampaignId(campaignId);

            LeadResponseDTO lead = createLead(leadRequest);

            if (lead == null || lead.getContactId() == null) {
                throw new RuntimeException("Não foi possível criar/obter o lead");
            }

            // Monta requisição com agendamento
            Map<String, Object> requestBody = buildNewsletterRequest(
                    "E-mail agendado: " + subject,
                    subject,
                    htmlContent,
                    campaignId,
                    List.of(lead.getContactId()),
                    null
            );

            // Adiciona agendamento
            Map<String, Object> schedule = new HashMap<>();
            schedule.put("scheduledFor", scheduleDate.toString());
            requestBody.put("schedule", schedule);

            NewsletterResponseDTO response = restClient.post()
                    .uri("/newsletters")
                    .body(requestBody)
                    .retrieve()
                    .body(NewsletterResponseDTO.class);

            logger.info("✅ E-mail agendado com sucesso para: {}", scheduleDate);
            return response;

        } catch (Exception e) {
            logger.error("❌ Erro ao agendar e-mail: {}", e.getMessage());
            throw new RuntimeException("Falha ao agendar e-mail: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém estatísticas de um e-mail enviado
     */
    public Map<String, Object> getEmailStats(String newsletterId) {
        logger.info("Buscando estatísticas do e-mail: {}", newsletterId);

        try {
            Map<String, Object> stats = restClient.get()
                    .uri("/newsletters/{newsletterId}/stats", newsletterId)
                    .retrieve()
                    .body(Map.class);

            logger.info("✅ Estatísticas obtidas: {}", stats);
            return stats;

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar estatísticas: {}", e.getMessage());
            return Map.of("error", "Não foi possível obter estatísticas");
        }
    }

    // ===========================================
// AULA 14.14 - Service e método de dados de e-mail da conta
// ===========================================
    /**
     * Obtém informações básicas da conta
     */
    public AccountInfoDTO getAccountInfo() {
        logger.info("Aula 14.14 - Buscando informações da conta...");

        try {
            AccountInfoDTO accountInfo = restClient.get()
                    .uri("/accounts")
                    .retrieve()
                    .body(AccountInfoDTO.class);

            logger.info("✅ Informações obtidas: {} - {}",
                    accountInfo != null ? accountInfo.getEmail() : "null",
                    accountInfo != null ? accountInfo.isActive() : false);

            return accountInfo;

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar informações da conta: {}", e.getMessage());
            throw new RuntimeException("Falha ao obter dados da conta", e);
        }
    }

    /**
     * Obtém limites da conta
     */
    public AccountLimitsDTO getAccountLimits() {
        logger.info("Buscando limites da conta...");

        try {
            // Busca informações da conta que contém os limites
            AccountInfoDTO accountInfo = getAccountInfo();

            // Como o DTO ainda não tem limits, buscamos de um endpoint específico
            AccountLimitsDTO limits = restClient.get()
                    .uri("/accounts/limits")
                    .retrieve()
                    .body(AccountLimitsDTO.class);

            if (limits == null) {
                // Fallback: cria limites padrão
                limits = new AccountLimitsDTO();
                limits.setContacts(1000);
                limits.setMonthlyEmails(5000);
                limits.setUsers(1);
            }

            logger.info("✅ Limites: {} contatos, {} e-mails/mês",
                    limits.getContacts(), limits.getMonthlyEmails());

            return limits;

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar limites: {}", e.getMessage());

            // Retorna limites padrão em caso de erro
            AccountLimitsDTO defaultLimits = new AccountLimitsDTO();
            defaultLimits.setContacts(1000);
            defaultLimits.setMonthlyEmails(5000);
            defaultLimits.setUsers(1);
            return defaultLimits;
        }
    }

    /**
     * Obtém estatísticas da conta
     */
    public AccountStatsDTO getAccountStats() {
        logger.info("Buscando estatísticas da conta...");

        try {
            AccountStatsDTO stats = new AccountStatsDTO();

            // Busca total de contatos
            String contactsResponse = restClient.get()
                    .uri("/contacts?perPage=1")
                    .retrieve()
                    .body(String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(contactsResponse);

            // Tenta obter o total do header ou da resposta
            if (jsonNode.isArray()) {
                stats.setTotalContacts(jsonNode.size());
            } else if (jsonNode.has("total")) {
                stats.setTotalContacts(jsonNode.get("total").asInt());
            } else {
                stats.setTotalContacts(0);
            }

            // Busca campanhas enviadas
            List<CampaignResponse> campaigns = getAllCampaigns(false);
            stats.setCampaignsActive(campaigns != null ? campaigns.size() : 0);

            // Busca e-mails enviados no mês (estimativa)
            stats.setEmailsSentThisMonth(estimateEmailsSentThisMonth());

            // Calcula e-mails restantes
            AccountLimitsDTO limits = getAccountLimits();
            int remaining = limits.getMonthlyEmails() - stats.getEmailsSentThisMonth();
            stats.setEmailsRemaining(Math.max(0, remaining));

            // Estatísticas de engajamento (exemplo)
            stats.setOpenRate(25.5);
            stats.setClickRate(5.2);
            stats.setBounceRate(1.8);
            stats.setActiveContacts(stats.getTotalContacts());

            logger.info("✅ Estatísticas: {} contatos, {} e-mails restantes",
                    stats.getTotalContacts(), stats.getEmailsRemaining());

            return stats;

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar estatísticas: {}", e.getMessage());

            // Retorna estatísticas vazias
            AccountStatsDTO emptyStats = new AccountStatsDTO();
            emptyStats.setTotalContacts(0);
            emptyStats.setEmailsRemaining(0);
            return emptyStats;
        }
    }

    /**
     * Obtém dashboard completo da conta
     */
    public AccountDashboardDTO getAccountDashboard() {
        logger.info("Buscando dashboard da conta...");

        try {
            AccountDashboardDTO dashboard = new AccountDashboardDTO();
            dashboard.setAccountInfo(getAccountInfo());
            dashboard.setLimits(getAccountLimits());
            dashboard.setStats(getAccountStats());
            dashboard.setStatus("active");
            dashboard.setPlanName("Professional");

            logger.info("✅ Dashboard montado com sucesso");
            return dashboard;

        } catch (Exception e) {
            logger.error("❌ Erro ao montar dashboard: {}", e.getMessage());
            throw new RuntimeException("Falha ao obter dashboard da conta", e);
        }
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Estima e-mails enviados no mês (pode ser melhorado com API real)
     */
    private int estimateEmailsSentThisMonth() {
        try {
            // Tenta buscar de relatórios
            String response = restClient.get()
                    .uri("/reports/stats/email")
                    .retrieve()
                    .body(String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response);

            if (jsonNode.has("sent")) {
                return jsonNode.get("sent").asInt();
            }

            return 0;

        } catch (Exception e) {
            logger.warn("Não foi possível obter estatísticas de envio: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Obtém relatório de campanhas enviadas
     */
    public List<Map<String, Object>> getCampaignsReport() {
        logger.info("Buscando relatório de campanhas...");

        try {
            List<Map<String, Object>> report = restClient.get()
                    .uri("/reports/campaigns")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

            logger.info("✅ Relatório obtido: {} campanhas", report != null ? report.size() : 0);
            return report;

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar relatório: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Verifica status da API (health check)
     */
    public Map<String, Object> healthCheck() {
        logger.info("Realizando health check da API...");

        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().toString());

        try {
            // Testa conexão com a API
            AccountInfoDTO accountInfo = getAccountInfo();
            health.put("api_status", "CONNECTED");
            health.put("account_email", accountInfo != null ? accountInfo.getEmail() : "unknown");
            health.put("account_active", accountInfo != null ? accountInfo.isActive() : false);

        } catch (Exception e) {
            health.put("api_status", "ERROR");
            health.put("api_error", e.getMessage());
        }

        logger.info("Health check: {}", health.get("api_status"));
        return health;
    }

    /**
     * Busca lead por ID
     */
    public LeadResponseDTO getLeadById(String contactId) {
        logger.info("Buscando lead por ID: {}", contactId);

        try {
            LeadResponseDTO lead = restClient.get()
                    .uri("/contacts/{contactId}", contactId)
                    .retrieve()
                    .body(LeadResponseDTO.class);

            return lead;

        } catch (Exception e) {
            logger.error("Lead não encontrado: {}", contactId);
            return null;
        }
    }

    /**
     * Constrói o corpo da requisição para newsletter
     */
    private Map<String, Object> buildNewsletterRequest(String name, String subject, String htmlContent,
                                                       String campaignId, List<String> selectedContacts,
                                                       List<String> selectedCampaigns) {
        Map<String, Object> requestBody = new LinkedHashMap<>();

        // Informações básicas
        requestBody.put("name", name);
        requestBody.put("subject", subject);

        // Conteúdo
        Map<String, String> content = new HashMap<>();
        content.put("html", htmlContent);
        requestBody.put("content", content);

        // FromField (remetente padrão)
        Map<String, String> fromField = new HashMap<>();
        fromField.put("fromFieldId", getDefaultFromFieldId());
        requestBody.put("fromField", fromField);

        // Configuração de envio
        Map<String, Object> sendSettings = new HashMap<>();

        if (selectedContacts != null && !selectedContacts.isEmpty()) {
            sendSettings.put("selectedContacts", selectedContacts);
        }

        if (selectedCampaigns != null && !selectedCampaigns.isEmpty()) {
            sendSettings.put("selectedCampaigns", selectedCampaigns);
        }

        if (campaignId != null && (selectedContacts == null || selectedContacts.isEmpty())) {
            // Fallback: usa o campaign no nível raiz
            Map<String, String> campaign = new HashMap<>();
            campaign.put("campaignId", campaignId);
            requestBody.put("campaign", campaign);
        }

        if (!sendSettings.isEmpty()) {
            requestBody.put("sendSettings", sendSettings);
        }

        // Tracking
        requestBody.put("trackOpens", true);
        requestBody.put("trackClicks", true);

        return requestBody;
    }

    /**
     * Carrega template de e-mail (simplificado - você pode expandir)
     */
    private String loadTemplate(String templateName, Map<String, String> variables) {
        // Exemplo simples - você pode carregar de arquivos ou banco de dados
        String template = "";

        switch (templateName) {
            case "welcome":
                template = "<h1>Bem-vindo {{name}}!</h1><p>Obrigado por se cadastrar.</p>";
                break;
            case "newsletter":
                template = "<h2>Newsletter {{date}}</h2><p>{{content}}</p>";
                break;
            case "promotion":
                template = "<h1>🎉 Oferta Especial!</h1><p>{{promotionText}}</p><p>Código: {{promoCode}}</p>";
                break;
            default:
                template = "<p>{{content}}</p>";
        }

        // Substitui as variáveis
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            template = template.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return template;
    }

    /**
     * Obtém assunto do template
     */
    private String getTemplateSubject(String templateName) {
        switch (templateName) {
            case "welcome": return "Bem-vindo!";
            case "newsletter": return "Nossa Newsletter";
            case "promotion": return "Oferta Especial!";
            default: return "Mensagem importante";
        }
    }

    /**
     * Busca o fromFieldId padrão da conta
     */
    private String getDefaultFromFieldId() {
        try {
            List<Map<String, Object>> fromFields = restClient.get()
                    .uri("/from-fields")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

            if (fromFields != null) {
                for (Map<String, Object> field : fromFields) {
                    if (Boolean.TRUE.equals(field.get("isDefault"))) {
                        return (String) field.get("fromFieldId");
                    }
                }
                if (!fromFields.isEmpty()) {
                    return (String) fromFields.get(0).get("fromFieldId");
                }
            }

            return "qSi3R"; // fallback

        } catch (Exception e) {
            logger.error("Erro ao buscar fromFieldId: {}", e.getMessage());
            return "qSi3R";
        }
    }

    // Versão com custom fields (se já existirem no GetResponse)
    private Map<String, Object> buildCustomFields() {
        Map<String, Object> customFields = new LinkedHashMap<>();

        // Custom fields devem ser objetos com id e valor
        List<Map<String, Object>> fields = new ArrayList<>();

        Map<String, Object> field = new HashMap<>();
        field.put("customFieldId", "created_from"); // ID que existe no GetResponse
        field.put("value", List.of("SpringBoot_API")); // Valor como array
        fields.add(field);

        customFields.put("customFieldValues", fields);

        return customFields;
    }

    private Map<String, Object> buildTags() {
        // Tags devem ser IDs válidos (criados previamente no GetResponse)
        List<String> tagIds = List.of("TAG_ID_1", "TAG_ID_2"); // IDs reais!
        return Map.of("tags", tagIds);
    }

}
