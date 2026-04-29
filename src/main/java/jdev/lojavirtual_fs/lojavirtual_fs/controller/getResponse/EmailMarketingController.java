package jdev.lojavirtual_fs.lojavirtual_fs.controller.getResponse;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse.CampaignResponse;
import jdev.lojavirtual_fs.lojavirtual_fs.service.getResponse.EmailMarketingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/email-marketing")
public class EmailMarketingController {

    private static final Logger logger = LoggerFactory.getLogger(EmailMarketingController.class);
    private final EmailMarketingService emailMarketingService;

    public EmailMarketingController(EmailMarketingService emailMarketingService) {
        this.emailMarketingService = emailMarketingService;
    }

    // Endpoint principal da aula 14.7 - Retorna lista de campanhas
    @GetMapping("/campaigns")
    public ResponseEntity<List<CampaignResponse>> getCampaigns() {
        logger.info("Requisição recebida: GET /api/email-marketing/campaigns");

        List<CampaignResponse> campaigns = emailMarketingService.getAllCampaigns();

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

    // Endpoint para buscar campanha específica
    @GetMapping("/campaigns/{campaignId}")
    public ResponseEntity<CampaignResponse> getCampaignById(@PathVariable String campaignId) {
        logger.info("Requisição recebida: GET /api/email-marketing/campaigns/{}", campaignId);

        CampaignResponse campaign = emailMarketingService.getCampaignById(campaignId);

        if (campaign == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(campaign);
    }

}
