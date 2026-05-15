package jdev.lojavirtual_fs.lojavirtual_fs.controller.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.PagamentoCartaoRequest;
import jdev.lojavirtual_fs.lojavirtual_fs.service.asaas.CartaoPagamentoService;
import jdev.lojavirtual_fs.lojavirtual_fs.service.asaas.CheckoutPagamentoService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.ILoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class CheckoutPagamentoController {

    @Autowired
    private CheckoutPagamentoService checkoutPagamentoService;
    @Autowired
    private CartaoPagamentoService cartaoPagamentoService;

    /**
     * Endpoint que recebe os dados do frontend (AJAX)
     * URL: /finalizarCompraCartao
     */
    @PostMapping("/finalizarCompraCartao")
    public ResponseEntity<String> finalizarCompraCartao(PagamentoCartaoRequest request) {
        String resultado = checkoutPagamentoService.processarPagamento(request);

        if ("sucesso".equals(resultado)) {
            return ResponseEntity.ok("sucesso");
        } else if ("pagamento_pendente".equals(resultado)) {
            return ResponseEntity.ok("pagamento_pendente");
        } else {
            return ResponseEntity.badRequest().body(resultado);
        }
    }

    /**
     * AULA 12.25 - Comprando com cartão de crédito - Parte 9
     * Endpoint final para compra com cartão
     */
    @PostMapping("/comprar/cartao")
    public ResponseEntity<?> comprarComCartao(@RequestBody PagamentoCartaoRequest request) {
        log.info("=== AULA 12.25 - Comprando com cartão de crédito ===");

        try {
            String resultado = cartaoPagamentoService.processarPagamentoCartao(request);

            if ("sucesso".equals(resultado)) {
                return ResponseEntity.ok(Map.of(
                        "sucesso", true,
                        "mensagem", "Pagamento realizado com sucesso!",
                        "status", "CONFIRMED"
                ));
            } else if ("pagamento_pendente".equals(resultado)) {
                return ResponseEntity.ok(Map.of(
                        "sucesso", true,
                        "mensagem", "Pagamento em processamento",
                        "status", "PENDING"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "sucesso", false,
                        "erro", resultado
                ));
            }

        } catch (Exception e) {
            log.error("Erro na compra: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                    "sucesso", false,
                    "erro", e.getMessage()
            ));
        }
    }
}
