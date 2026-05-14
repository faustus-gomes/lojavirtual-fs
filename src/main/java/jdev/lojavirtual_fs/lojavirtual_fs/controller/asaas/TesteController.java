package jdev.lojavirtual_fs.lojavirtual_fs.controller.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.service.asaas.AsaasCobrancaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/teste")
public class TesteController {

    @Autowired
    private AsaasCobrancaService asaasCobrancaService;

    /*@GetMapping("/cobranca")
    public String testarCobranca() {
        return "Service carregado com sucesso!";
    }*/

    @GetMapping("/status")
    @ResponseBody
    public Map<String, Object> status() {
        Map<String, Object> response = new HashMap<>();
        response.put("sucesso", true);
        response.put("mensagem", "Service carregado com sucesso!");

        System.out.println("=== TesteController chamado ===");
        System.out.println("AsaasCobrancaService: " + (asaasCobrancaService != null ? "OK" : "NULO"));

        return response;
    }

    @GetMapping("/testar")
    public String testar() {
        return "Funcionou!";
    }
}
