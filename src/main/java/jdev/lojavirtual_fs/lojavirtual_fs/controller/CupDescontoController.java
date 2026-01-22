package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jdev.lojavirtual_fs.lojavirtual_fs.model.CupDesc;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.CupDescontoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RestController
public class CupDescontoController {

    @Autowired
    private CupDescontoRepository cupDescontoRepository;

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
}
