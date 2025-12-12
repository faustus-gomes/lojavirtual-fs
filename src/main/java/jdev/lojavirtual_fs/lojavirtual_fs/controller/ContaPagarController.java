package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.model.ContaPagar;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.ContaPagarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
public class ContaPagarController {
    @Autowired
    private ContaPagarRepository contaPagarRepository;

    @ResponseBody
    @PostMapping(value = "/salvarContaPagar")
    public ResponseEntity<ContaPagar> salvarContaPagar(@RequestBody @Valid ContaPagar contaPagar) throws ExceptionLoja {
        if (contaPagar.getEmpresa() == null || contaPagar.getEmpresa().getId() == null || contaPagar.getEmpresa().getId() <= 0) {
            throw new ExceptionLoja("Empresa Responsável deve ser informada.");
        }

        if (contaPagar.getId() == null) {
            List<ContaPagar> contaPagars;
            contaPagars= contaPagarRepository.buscaContaDesc(contaPagar.getDescricao().toUpperCase());
            if (!contaPagars.isEmpty()) {
                throw new ExceptionLoja("Já existe esta descrição cadastrada " + contaPagar.getDescricao());
            }
        }

        if (contaPagar.getValorTotal()== null) {
            throw new ExceptionLoja("Valor Total deve ser informada.");
        }

        if ( (contaPagar.getPessoa()) == null || contaPagar.getPessoa().getId() == null || contaPagar.getPessoa().getId() <= 0) {
            throw new ExceptionLoja("Pessoa do Responsável deve ser informada.");
        }

        if ( (contaPagar.getPessoa_fornecedor()) == null || contaPagar.getPessoa_fornecedor().getId() == null || contaPagar.getPessoa_fornecedor().getId() <= 0) {
            throw new ExceptionLoja("Fornecedor Responsável deve ser informado.");
        }


        ContaPagar contaPagarSalvo = contaPagarRepository.save(contaPagar);

        return new ResponseEntity<ContaPagar>(contaPagarSalvo, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/deleteContaPagar")
    public ResponseEntity<?> deleteContaPagar(@RequestBody ContaPagar contaPagar) {

        contaPagarRepository.deleteById(contaPagar.getId());
        return new ResponseEntity("Conta Pagar Removido",HttpStatus.OK);
    }

    //@Secured({"ROLE_GERENTE","ROLE_ADMIN"})
    @ResponseBody
    @DeleteMapping(value = "/deleteContaPagarPorId/{id}")
    public ResponseEntity<?> deleteContaPagarPorId(@PathVariable("id")Long id) {

        contaPagarRepository.deleteById(id);
        return new ResponseEntity("Conta Pagar Removido",HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/obterContaPagar/{id}")
    public ResponseEntity<ContaPagar> obterContaPagar(@PathVariable("id")Long id) throws ExceptionLoja {

        ContaPagar contaPagar=  contaPagarRepository.findById(id).orElse(null);

        if (contaPagar == null) {
            throw new ExceptionLoja("Náo econtrado a contapagar com o código "+ id);
        }
        return new ResponseEntity<ContaPagar>(contaPagar,HttpStatus.OK);
    }
    @ResponseBody
    @GetMapping(value = "/buscarContaPagarDesc/{desc}")
    public ResponseEntity<List<ContaPagar>> buscarPrdNome(@PathVariable("desc")String desc) {

        List<ContaPagar> contaPagar =  contaPagarRepository.buscaContaDesc(desc.toUpperCase());
        return new ResponseEntity<List<ContaPagar>>(contaPagar,HttpStatus.OK);
    }
}
