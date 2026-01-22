package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Acesso;
import jdev.lojavirtual_fs.lojavirtual_fs.model.FormaPagamento;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.FormaPagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
public class FormaPagamentoController {

    @Autowired
    FormaPagamentoRepository formaPagamentoRepository;

    @ResponseBody
    @PostMapping(value = "/salvarFormaPagamento")
    public ResponseEntity<FormaPagamento> salvarFormaPagamento(@RequestBody @Valid FormaPagamento formaPagamento) {
        /*if (formaPagamento.getId() == null) {
            List<FormaPagamento> formaPagamentos =  formaPagamentoRepository.buscarAcessoDesc(acesso.getDescricao().toUpperCase());
            if (!formaPagamentos.isEmpty()) {
                throw new ExceptionLoja("Já existe esta descrição cadastrada " + formaPagamento.getDescricao());
            }
        }*/

        formaPagamento = formaPagamentoRepository.saveAndFlush(formaPagamento);

        return  new ResponseEntity<FormaPagamento>(formaPagamento, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/listaFormaPagamentoEmpresa/{idEmpresa}")
    public ResponseEntity<List<FormaPagamento>> listaFormaPagamentoEmpresa(@PathVariable(value = "idEmpresa") long idEmpresa) {

        return new ResponseEntity<List<FormaPagamento>>(formaPagamentoRepository.findAll(idEmpresa), HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/listaFormaPagamento")
    public ResponseEntity<List<FormaPagamento>> listaFormaPagamento() {

        return new ResponseEntity<List<FormaPagamento>>(formaPagamentoRepository.findAll(), HttpStatus.OK);
    }
}
