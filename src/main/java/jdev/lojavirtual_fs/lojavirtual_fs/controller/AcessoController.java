package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Acesso;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.AcessoRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.AcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
public class AcessoController {
    @Autowired
    private AcessoService acessoService;
    @Autowired
    private AcessoRepository acessoRepository;
    @ResponseBody
    @PostMapping(value = "/salvarAcesso")
    public ResponseEntity<Acesso> salvarAcesso(@RequestBody Acesso acesso) throws ExceptionLoja {
        if (acesso.getId() == null) {
            List<Acesso> acessos =  acessoRepository.buscarAcessoDesc(acesso.getDescricao().toUpperCase());
            if (!acessos.isEmpty()) {
                throw new ExceptionLoja("Já existe esta descrição cadastrada " + acesso.getDescricao());
            }
        }


        Acesso acessoSalvo = acessoService.save(acesso);

        return new ResponseEntity<Acesso>(acessoSalvo, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/deleteAcesso")
    public ResponseEntity<?> deleteAcesso(@RequestBody Acesso acesso) {

        acessoRepository.deleteById(acesso.getId());
        return new ResponseEntity("Acesso Removido",HttpStatus.OK);
    }

    //@Secured({"ROLE_GERENTE","ROLE_ADMIN"})
    @ResponseBody
    @DeleteMapping(value = "/deleteAcessoPorId/{id}")
    public ResponseEntity<?> deleteAcessoPorId(@PathVariable("id")Long id) {

        acessoRepository.deleteById(id);
        return new ResponseEntity("Acesso Removido",HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/obterAcesso/{id}")
    public ResponseEntity<Acesso> obterAcesso(@PathVariable("id")Long id) throws ExceptionLoja {

        Acesso acesso=  acessoRepository.findById(id).orElse(null);

        if (acesso == null) {
            throw new ExceptionLoja("Náo econtrado o acesso com o código "+ id);
        }
        return new ResponseEntity<Acesso>(acesso,HttpStatus.OK);
    }
    @ResponseBody
    @GetMapping(value = "/buscarPorDesc/{desc}")
    public ResponseEntity<List<Acesso>> buscarPorDesc(@PathVariable("desc")String desc) {

        List<Acesso> acesso =  acessoRepository.buscarAcessoDesc(desc.toUpperCase());
        return new ResponseEntity<List<Acesso>>(acesso,HttpStatus.OK);
    }
}
