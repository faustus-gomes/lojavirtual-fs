package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaJuridica;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.PessoaRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.PessoaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//@Controller
@RestController
public class PessoaController {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaUserService pessoaUserService;

    //@ResponseBody
    @PostMapping(value = "/salvarPj")
    public ResponseEntity<PessoaJuridica> salvarPj(@RequestBody PessoaJuridica pessoaJuridica) throws ExceptionLoja {
        System.out.println("=== SALVAR PJ CHAMADO ===");
        System.out.println("PessoaJuridica: " + pessoaJuridica);
        System.out.println("É null? " + (pessoaJuridica == null));

        if (pessoaJuridica == null) {
            System.out.println("=== LANÇANDO EXCEÇÃO ===");
            throw new ExceptionLoja("Pessoa Jurídica não pode ser Null. É necessário enviar um JSON no corpo da requisição.");
        }

        // Validações de campos obrigatórios
        if (pessoaJuridica.getCnpj() == null || pessoaJuridica.getCnpj().trim().isEmpty()) {
            throw new ExceptionLoja("CNPJ é obrigatório");
        }

        if (pessoaJuridica.getNome() == null || pessoaJuridica.getNome().trim().isEmpty()) {
            throw new ExceptionLoja("Nome é obrigatório");
        }

        if (pessoaJuridica.getEmail() == null || pessoaJuridica.getEmail().trim().isEmpty()) {
            throw new ExceptionLoja("Email é obrigatório");
        }

        if (pessoaJuridica.getId() == null && pessoaRepository.existeCnpjCadastrado(pessoaJuridica.getCnpj()) != null) {
            throw new ExceptionLoja("Já existe pessoa cadastrada com este CNPJ " + pessoaJuridica.getCnpj());
        }

        pessoaJuridica = pessoaUserService.salvarPessoaJuridica(pessoaJuridica);

        return new ResponseEntity<PessoaJuridica>(pessoaJuridica, HttpStatus.CREATED);
    }
}
