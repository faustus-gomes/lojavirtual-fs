package jdev.lojavirtual_fs.lojavirtual_fs.controller.asaas;

import com.fasterxml.jackson.databind.ObjectMapper;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasWebhookPayload;
import jdev.lojavirtual_fs.lojavirtual_fs.enums.StatusVendaLojaVirtual;
import jdev.lojavirtual_fs.lojavirtual_fs.model.VendaCompraLojaVirtual;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.VdCpLojaVirtRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/webhook/asaas")
public class WebhookAsaasController {

    @Autowired
    private VdCpLojaVirtRepository vendaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * AULA 12.27 e 12.28 - Endpoint para receber notificações do Asaas
     * URL: https://seudominio.com/ecommercefs/webhook/asaas
     */
    @PostMapping
    public ResponseEntity<?> receberWebhook(@RequestBody String payload) {
        log.info("=== WEBHOOK RECEBIDO DO ASAAS ===");
        log.info("Payload bruto: {}", payload);

        try {
            // Converter payload para objeto
            AsaasWebhookPayload webhookData = objectMapper.readValue(payload, AsaasWebhookPayload.class);

            String event = webhookData.getEvent();
            log.info("Evento recebido: {}", event);

            if (webhookData.getPayment() != null) {
                String paymentId = webhookData.getPayment().getId();
                String status = webhookData.getPayment().getStatus();
                String externalReference = webhookData.getPayment().getExternalReference();

                log.info("Pagamento ID: {}", paymentId);
                log.info("Status: {}", status);
                log.info("External Reference (ID Venda): {}", externalReference);

                // Processar baseado no evento
                switch (event) {
                    case "PAYMENT_CONFIRMED":
                    case "PAYMENT_RECEIVED":
                        processarPagamentoConfirmado(paymentId, status, externalReference);
                        break;
                    case "PAYMENT_PENDING":
                        processarPagamentoPendente(paymentId, status, externalReference);
                        break;
                    case "PAYMENT_REFUSED":
                    case "PAYMENT_CANCELED":
                        processarPagamentoRecusado(paymentId, status, externalReference);
                        break;
                    default:
                        log.info("Evento não tratado: {}", event);
                }
            }

            return ResponseEntity.ok().body(Map.of("recebido", true));

        } catch (Exception e) {
            log.error("Erro ao processar webhook: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * AULA 12.29 - Endpoint para teste do WebHook (GET)
     * Útil para verificar se o endpoint está acessível
     */
    @GetMapping
    public ResponseEntity<?> testarWebhook() {
        log.info("=== TESTE WEBHOOK GET ===");
        return ResponseEntity.ok().body(Map.of(
                "status", "Webhook endpoint está ativo!",
                "timestamp", System.currentTimeMillis(),
                "url", "/ecommercefs/webhook/asaas"
        ));
    }

    /**
     * Processa pagamento confirmado
     */
    private void processarPagamentoConfirmado(String paymentId, String status, String externalReference) {
        log.info("=== Processando pagamento CONFIRMADO ===");
        atualizarVenda(paymentId, status, StatusVendaLojaVirtual.FINALIZADA);
    }

    /**
     * Processa pagamento pendente
     */
    private void processarPagamentoPendente(String paymentId, String status, String externalReference) {
        log.info("=== Processando pagamento PENDENTE ===");
        atualizarVenda(paymentId, status, StatusVendaLojaVirtual.PENDENTE);
    }

    /**
     * Processa pagamento recusado/cancelado
     */
    private void processarPagamentoRecusado(String paymentId, String status, String externalReference) {
        log.info("=== Processando pagamento RECUSADO/CANCELADO ===");
        atualizarVenda(paymentId, status, StatusVendaLojaVirtual.CANCELADA);
    }

    /**
     * Atualiza o status da venda no banco
     */
    private void atualizarVenda(String paymentId, String statusAsaas, StatusVendaLojaVirtual novoStatus) {
        try {
            // Buscar venda pelo pagamentoId
            Optional<VendaCompraLojaVirtual> vendaOpt = vendaRepository.findByPagamentoId(paymentId);

            if (vendaOpt.isPresent()) {
                VendaCompraLojaVirtual venda = vendaOpt.get();
                venda.setStatusPagamento(statusAsaas);
                venda.setStatusVendaLojaVirtual(novoStatus);
                vendaRepository.save(venda);
                log.info("Venda {} atualizada para status: {}", venda.getId(), novoStatus);
            } else {
                log.warn("Venda não encontrada para o pagamentoId: {}", paymentId);
            }
        } catch (Exception e) {
            log.error("Erro ao atualizar venda: {}", e.getMessage(), e);
        }
    }
}
