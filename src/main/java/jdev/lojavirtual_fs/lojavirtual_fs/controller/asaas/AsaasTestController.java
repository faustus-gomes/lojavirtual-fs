package jdev.lojavirtual_fs.lojavirtual_fs.controller.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasInvoiceResponseDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.service.asaas.AsaasInvoiceTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/asaas/test")
public class AsaasTestController {

    private static final Logger log = LoggerFactory.getLogger(AsaasTestController.class);
    @Autowired
    private AsaasInvoiceTestService asaasInvoiceTestService;

    /**
     * Endpoint: GET /ecommercefs/asaas/test/conexao
     * Testa se a conexão com a API Asaas está funcionando
     */

    @GetMapping("/conexao")
    public ResponseEntity<Map<String, Object>> testarConexao() {

            Map<String, Object> response = new HashMap<>();

            try {
                log.info("📡 Testando conexão com API Asaas...");
                asaasInvoiceTestService.testarConexao();

                response.put("sucesso", true);
                response.put("mensagem", "✅ Teste de conexão executado com sucesso!");
                response.put("timestamp", System.currentTimeMillis());

                return ResponseEntity.ok(response);

            }catch (Exception e) {
                log.error("❌ Erro no teste de conexão: {}", e.getMessage());

                response.put("sucesso", false);
                response.put("mensagem", "❌ Erro: " + e.getMessage());
                response.put("timestamp", System.currentTimeMillis());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
    }

    /**
     * Endpoint: POST /ecommercefs/asaas/test/emissao
     * Testa a emissão de uma nota fiscal de R$ 1,00
     */
    @PostMapping("/emissao")
    public ResponseEntity<Map<String, Object>> testarEmissao() {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("📤 Testando emissão de nota fiscal...");

            AsaasInvoiceResponseDTO invoice = asaasInvoiceTestService.testarEmissaoSimples();

            response.put("sucesso", true);
            response.put("mensagem", "✅ Nota fiscal emitida com sucesso!");
            response.put("dados", invoice);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        }catch (Exception e) {
            log.error("❌ Erro na emissão: {}", e.getMessage());

            response.put("sucesso", false);
            response.put("mensagem", "❌ Erro na emissão: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint: GET /ecommercefs/asaas/test/consulta/{invoiceId}
     * Consulta uma nota fiscal pelo ID
     */
    @GetMapping("/consulta/{invoiceId}")
    public ResponseEntity<Map<String, Object>> testarConsulta(
            @PathVariable("invoiceId") String invoiceId) {

        Map<String, Object> response = new HashMap<>();

        try {
            log.info("📋 Consultando nota fiscal: {}", invoiceId);

            AsaasInvoiceResponseDTO invoice = asaasInvoiceTestService.testarConsulta(invoiceId);

            response.put("sucesso", true);
            response.put("mensagem", "✅ Consulta realizada com sucesso!");
            response.put("dados", invoice);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);
        }catch (Exception e) {
            log.error("❌ Erro na consulta: {}", e.getMessage());

            response.put("sucesso", false);
            response.put("mensagem", "❌ Erro na consulta: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Endpoint: GET /ecommercefs/asaas/test/status
     * Verifica o status da API (health check)
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();

        response.put("servico", "Asaas Integration");
        response.put("status", "online");
        response.put("timestamp", System.currentTimeMillis());
        response.put("versao", "1.0.0");

        return ResponseEntity.ok(response);
    }
}
