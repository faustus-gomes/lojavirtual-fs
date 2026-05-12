package jdev.lojavirtual_fs.lojavirtual_fs.controller.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasCobrancaResponse;
import jdev.lojavirtual_fs.lojavirtual_fs.service.asaas.CheckoutPixBoletoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/asaas/pagamento")
public class PagamentoPixBoletoController {
    @Autowired
    private CheckoutPixBoletoService pixBoletoService;

    /**
     * Gerar QR Code PIX para a venda
     * GET /api/pagamento/pix/{idVenda}
     */
    @GetMapping("/pix/{idVenda}")
    public ResponseEntity<?> gerarPix(@PathVariable Long idVenda) {
        try {
            AsaasCobrancaResponse resposta = pixBoletoService.gerarPix(idVenda);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("id", resposta.getId());
            response.put("status", resposta.getStatus());
            response.put("qrCode", resposta.getPixQrCode());
            response.put("copyPaste", resposta.getPixCopyPaste());
            response.put("valor", resposta.getValue());
            response.put("vencimento", resposta.getDueDate());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("sucesso", "false");
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Gerar Boleto para a venda
     * GET /api/pagamento/boleto/{idVenda}
     */
    @GetMapping("/boleto/{idVenda}")
    public ResponseEntity<?> gerarBoleto(@PathVariable Long idVenda) {
        try {
            AsaasCobrancaResponse resposta = pixBoletoService.gerarBoleto(idVenda);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("id", resposta.getId());
            response.put("status", resposta.getStatus());
            response.put("boletoUrl", resposta.getBankSlipUrl());
            response.put("invoiceUrl", resposta.getInvoiceUrl());
            response.put("valor", resposta.getValue());
            response.put("vencimento", resposta.getDueDate());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("sucesso", "false");
            response.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
