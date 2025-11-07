package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @GetMapping("/teste-excecao")
    public String testeExcecao() throws ExceptionLoja {
        System.out.println("=== DEBUG: Lançando ExceptionLoja ===");
        throw new ExceptionLoja("🚨 TESTE - Esta é uma exceção de debug");
    }

    @PostMapping("/teste-post")
    public String testePost(@RequestBody(required = false) String corpo) throws ExceptionLoja {
        System.out.println("=== DEBUG: Teste POST - Corpo: " + corpo);
        if (corpo == null) {
            throw new ExceptionLoja("🚨 TESTE POST - Corpo nulo");
        }
        return "Sucesso: " + corpo;
    }
}
