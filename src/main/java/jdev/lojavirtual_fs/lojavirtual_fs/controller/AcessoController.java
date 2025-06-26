package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jdev.lojavirtual_fs.lojavirtual_fs.model.Acesso;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.AcessoRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.AcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class AcessoController {
    @Autowired
    private AcessoService acessoService;
    @Autowired
    private AcessoRepository acessoRepository;
    @ResponseBody
    @PostMapping(value = "/salvarAcesso")
    public ResponseEntity<Acesso> salvarAcesso(@RequestBody Acesso acesso) {

        Acesso acessoSalvo = acessoService.save(acesso);

        return new ResponseEntity<Acesso>(acessoSalvo, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/deleteAcesso")
    public ResponseEntity<?> deleteAcesso(@RequestBody Acesso acesso) {

        acessoRepository.deleteById(acesso.getId());
        return new ResponseEntity("Acesso Removido",HttpStatus.OK);
    }
}
