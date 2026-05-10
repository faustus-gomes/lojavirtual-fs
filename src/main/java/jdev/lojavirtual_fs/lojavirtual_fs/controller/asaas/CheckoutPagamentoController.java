package jdev.lojavirtual_fs.lojavirtual_fs.controller.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.PagamentoCartaoRequest;
import jdev.lojavirtual_fs.lojavirtual_fs.service.asaas.CheckoutPagamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class CheckoutPagamentoController {

    @Autowired
    private CheckoutPagamentoService checkoutPagamentoService;

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
}
