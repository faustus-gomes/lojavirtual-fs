package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.model.CupDesc;
import jdev.lojavirtual_fs.lojavirtual_fs.model.MarcaProduto;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.CupDescontoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
public class CupDescontoController {

    @Autowired
    private CupDescontoRepository cupDescontoRepository;

    @ResponseBody
    @PostMapping(value = "/salvarCupDesc")
    public ResponseEntity<CupDesc> salvarCupDesc(@RequestBody @Valid CupDesc cupDesc) throws ExceptionLoja {

        if (cupDesc.getEmpresa() == null || cupDesc.getEmpresa().getId() == null || cupDesc.getEmpresa().getId() <= 0) {
            throw new ExceptionLoja("Empresa Responsável deve ser informada.");
        }

        CupDesc cupDesc2= cupDescontoRepository.save(cupDesc);

        return new ResponseEntity<CupDesc>(cupDesc2, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/deleteCupDesc")
    public ResponseEntity<?> deleteCupDesc(@RequestBody CupDesc cupDesc) {

        cupDescontoRepository.deleteById(cupDesc.getId());
        return new ResponseEntity("Cupom de Desconto Removido",HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/listaCupomDescEmpresa/{idEmpresa}")
    public ResponseEntity<List<CupDesc>> listaCupomDescEmpresa(@PathVariable("idEmpresa") Long idEmpresa) {

        return new ResponseEntity<List<CupDesc>>(cupDescontoRepository.cupDescontoPorEmpresa(idEmpresa), HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/listaCupomDesc")
    public ResponseEntity<List<CupDesc>> listaCupomDesc() {

        return new ResponseEntity<List<CupDesc>>(cupDescontoRepository.findAll(), HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/obterCupomPorId/{id}")
    public ResponseEntity<CupDesc> obterCupomPorId(@PathVariable("id")Long id) throws ExceptionLoja {

        CupDesc cupDesc =  cupDescontoRepository.findById(id).orElse(null);

        if (cupDesc == null) {
            throw new ExceptionLoja("Não econtrado o cupom com o código "+ id);
        }
        return new ResponseEntity<CupDesc>(cupDesc,HttpStatus.OK);
    }
}
