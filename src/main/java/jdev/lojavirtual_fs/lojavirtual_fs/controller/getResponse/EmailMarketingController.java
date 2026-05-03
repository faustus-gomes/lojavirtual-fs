package jdev.lojavirtual_fs.lojavirtual_fs.controller.getResponse;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse.*;
import jdev.lojavirtual_fs.lojavirtual_fs.service.getResponse.EmailMarketingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Map;

@RestController
    @RequestMapping("/email-marketing")
public class EmailMarketingController {

    private static final Logger logger = LoggerFactory.getLogger(EmailMarketingController.class);
    private final EmailMarketingService emailMarketingService;

    public EmailMarketingController(EmailMarketingService emailMarketingService) {
        this.emailMarketingService = emailMarketingService;
    }

    // Endpoint principal da aula 14.7 - Retorna lista de campanhas
    @GetMapping("/campaigns/geral")
    public ResponseEntity<List<CampaignResponse>> getCampaigns() {
        logger.info("Requisição recebida: GET /email-marketing/campaigns/geral");

        List<CampaignResponse> campaigns = emailMarketingService.getAllCampaigns(false);

        if (campaigns == null || campaigns.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(campaigns);
    }

    // Endpoint alternativo - Retorna como ARRAY
    @GetMapping("/campaigns/array")
    public ResponseEntity<CampaignResponse[]> getCampaignsAsArray() {
        logger.info("Requisição recebida: GET /api/email-marketing/campaigns/array");

        CampaignResponse[] campaigns = emailMarketingService.getCampaignsAsArray();

        if (campaigns.length == 0) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(campaigns);
    }

    // Endpoint para debug - Retorna JSON bruto
    @GetMapping("/campaigns/raw")
    public ResponseEntity<String> getCampaignsRaw() {
        logger.info("Requisição recebida: GET /api/email-marketing/campaigns/raw");

        String jsonResponse = emailMarketingService.getCampaignsRawJson();
        return ResponseEntity.ok(jsonResponse);
    }


    // ===========================================
    // AULA 14.8 - Cadastro de Lead
    // ===========================================
    @PostMapping("/leads")
    public ResponseEntity<LeadResponseDTO> createLead(@Valid @RequestBody LeadRequestDTO leadRequest) {
        logger.info("Requisição recebida: POST /email-marketing/leads");
        logger.info("Email: {}, Campanha: {}", leadRequest.getEmail(), leadRequest.getCampaignId());

        LeadResponseDTO lead = emailMarketingService.createLead(leadRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(lead);
    }

    // ===========================================
    // AULA 14.10 - Enviar e-mail/newsletter
    // ===========================================

    /**
     * Endpoint para enviar newsletter (versão completa) 14.10
     */
    @PostMapping("/send-newsletter")
    public ResponseEntity<NewsletterResponseDTO> sendNewsletter(
            @Valid @RequestBody NewsletterRequestDTO newsletterRequest) {

        logger.info("Requisição recebida: POST /api/email-marketing/send-newsletter");
        logger.info("Nome: {}, Assunto: {}", newsletterRequest.getName(), newsletterRequest.getSubject());

        NewsletterResponseDTO response = emailMarketingService.sendNewsletter(newsletterRequest);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    /**
     * Endpoint simplificado para enviar e-mail para um único destinatário
     */
    // Endpoint 1: Envio simples (requer campaignId)
    @PostMapping("/send-email")
    public ResponseEntity<NewsletterResponseDTO> sendEmail(
            @RequestParam String campaignId,
            @Valid @RequestBody SendEmailDTO sendEmailDTO) {

        logger.info("POST /send-email - Campanha: {}, Para: {}", campaignId, sendEmailDTO.getTo());

        NewsletterResponseDTO response = emailMarketingService.sendSimpleEmail(
                sendEmailDTO.getTo(),
                sendEmailDTO.getSubject(),
                sendEmailDTO.getHtmlContent(),
                campaignId
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    // Endpoint 2: Envio sem campaignId (usa campanha padrão)
    @PostMapping("/send-email/default")
    public ResponseEntity<NewsletterResponseDTO> sendEmailToDefaultCampaign(
            @Valid @RequestBody SendEmailDTO sendEmailDTO) {

        logger.info("POST /send-email/default - Para: {}", sendEmailDTO.getTo());

        // Busca a campanha padrão
        List<CampaignResponse> campaigns = emailMarketingService.getAllCampaigns(false);
        String defaultCampaignId = campaigns.stream()
                .filter(CampaignResponse::isDefaultCampaign)
                .findFirst()
                .map(CampaignResponse::getId)
                .orElseThrow(() -> new RuntimeException("Nenhuma campanha padrão encontrada"));

        NewsletterResponseDTO response = emailMarketingService.sendSimpleEmail(
                sendEmailDTO.getTo(),
                sendEmailDTO.getSubject(),
                sendEmailDTO.getHtmlContent(),
                defaultCampaignId
        );

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }


    @PostMapping("/send-email-to-lead")
    public ResponseEntity<?> sendEmailToLead(
            @RequestParam String campaignId,
            @Valid @RequestBody SendEmailDTO sendEmailDTO) {

        try {
            NewsletterResponseDTO response = emailMarketingService.sendEmailToExistingLead(
                    sendEmailDTO.getTo(),
                    sendEmailDTO.getSubject(),
                    sendEmailDTO.getHtmlContent(),
                    campaignId
            );
            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/from-fields")
    public ResponseEntity<List<Map<String, Object>>> getFromFields() {
        List<Map<String, Object>> fromFields = emailMarketingService.getFromFields();
        return ResponseEntity.ok(fromFields);
    }

    // ===========================================
    // AULA 14.11 - Endpoints para listagem de campanhas
    // ===========================================

    /**
    * Lista todas as campanhas (com cache)
    */
    @GetMapping("/campaigns")
    public ResponseEntity<List<CampaignResponse>> getAllCampaigns() {
        logger.info("GET /campaigns - Listando todas as campanhas");

        List<CampaignResponse> campaigns = emailMarketingService.getAllCampaigns(false);

        if (campaigns == null || campaigns.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(campaigns);
    }

    /**
     * Lista campanhas forçando refresh do cache
     */
    @GetMapping("/campaigns/refresh")
    public ResponseEntity<List<CampaignResponse>> refreshCampaigns() {
        logger.info("GET /campaigns/refresh - Forçando refresh do cache");

        List<CampaignResponse> campaigns = emailMarketingService.getAllCampaigns(true);

        if (campaigns == null || campaigns.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(campaigns);
    }

    /**
     * Busca campanha por ID
     */
    @GetMapping("/campaigns/{campaignId}")
    public ResponseEntity<CampaignResponse> getCampaignById(@PathVariable String campaignId) {
        logger.info("GET /campaigns/{} - Buscando campanha", campaignId);

        CampaignResponse campaign = emailMarketingService.getCampaignById(campaignId);

        if (campaign == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(campaign);
    }

    /**
     * Busca campanha padrão
     */
    @GetMapping("/campaigns/default")
    public ResponseEntity<CampaignResponse> getDefaultCampaign() {
        logger.info("GET /campaigns/default - Buscando campanha padrão");

        CampaignResponse campaign = emailMarketingService.getDefaultCampaign();

        if (campaign == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(campaign);
    }

    /**
     * Busca campanhas por nome
     */
    @GetMapping("/campaigns/search")
    public ResponseEntity<List<CampaignResponse>> searchCampaigns(@RequestParam String name) {
        logger.info("GET /campaigns/search?name={} - Buscando campanhas", name);

        List<CampaignResponse> campaigns = emailMarketingService.getCampaignsByName(name);

        if (campaigns == null || campaigns.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(campaigns);
    }

    /**
     * Limpa o cache de campanhas
     */
    @DeleteMapping("/campaigns/cache")
    public ResponseEntity<String> clearCampaignCache() {
        logger.info("DELETE /campaigns/cache - Limpando cache");

        emailMarketingService.clearCampaignCache();

        return ResponseEntity.ok("Cache de campanhas limpo com sucesso");
    }

    // ===========================================
    // AULA 14.12 - Endpoints para gerenciar leads
    // ===========================================

    /**
     * Cria um novo lead
     */
    @PostMapping("/leadsnew")
    public ResponseEntity<LeadResponseDTO> createLeadNew(@Valid @RequestBody LeadRequestDTO leadRequest) {
        logger.info("POST /leads - Email: {}, Campanha: {}",
                leadRequest.getEmail(), leadRequest.getCampaignId());

        LeadResponseDTO lead = emailMarketingService.createLead(leadRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(lead);
    }

    /**
     * Cria múltiplos leads em lote
     */
    @PostMapping("/leads/batch")
    public ResponseEntity<List<LeadResponseDTO>> createLeadsBatch(
            @Valid @RequestBody List<LeadRequestDTO> leadRequests) {

        logger.info("POST /leads/batch - Criando {} leads", leadRequests.size());

        List<LeadResponseDTO> results = emailMarketingService.createLeadsBatch(leadRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(results);
    }

    /**
     * Remove um lead por ID
     */
    @DeleteMapping("/leads/{contactId}")
    public ResponseEntity<Void> deleteLead(@PathVariable String contactId) {
        logger.info("DELETE /leads/{} - Removendo lead", contactId);

        boolean deleted = emailMarketingService.deleteLead(contactId);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
