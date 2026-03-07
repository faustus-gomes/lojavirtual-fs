package jdev.lojavirtual_fs.lojavirtual_fs.controller.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.service.asaas.AsaasFiscalInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/asaas/fiscal")
public class AsaasFiscalController {
    @Autowired
    private AsaasFiscalInfoService fiscalInfoService;

    /**
     * POST /ecommercefs/asaas/fiscal/configurar
     * Configura as informações fiscais da empresa no Asaas
     */
    @PostMapping("/configurar")
    public ResponseEntity<Map<String, Object>> configurarFiscal() {
        Map<String, Object> response = new HashMap<>();

        boolean sucesso = fiscalInfoService.configurarInformacoesFiscais();

        response.put("sucesso", sucesso);
        response.put("mensagem", sucesso ?
                "✅ Informações fiscais configuradas com sucesso!" :
                "❌ Erro ao configurar. Verifique os logs.");

        return ResponseEntity.ok(response);
    }

    /**
     * GET /ecommercefs/asaas/fiscal/status
     * Verifica se as informações fiscais já estão configuradas
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> verificarStatusFiscal() {
        Map<String, Object> response = new HashMap<>();

        boolean configurado = fiscalInfoService.verificarConfiguracaoFiscal();

        response.put("configurado", configurado);
        response.put("mensagem", configurado ?
                "✅ Informações fiscais OK" :
                "❌ Informações fiscais NÃO configuradas");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/diagnostico")
    public ResponseEntity<Map<String, Object>> diagnosticar() {
        Map<String, Object> response = new HashMap<>();
        fiscalInfoService.diagnosticarConfiguracao();
        response.put("mensagem", "Diagnóstico executado. Verifique os logs.");
        return ResponseEntity.ok(response);
    }

}
