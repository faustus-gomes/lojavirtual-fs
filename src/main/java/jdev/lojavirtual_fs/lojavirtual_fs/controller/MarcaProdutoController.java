package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.model.MarcaProduto;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.MarcaProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
public class MarcaProdutoController {
    @Autowired
    private MarcaProdutoRepository marcaRepository;
    @ResponseBody
    @PostMapping(value = "/salvarMarcaProduto")
    public ResponseEntity<MarcaProduto> salvarMarcaProduto(@RequestBody @Valid MarcaProduto marcaProduto) throws ExceptionLoja {
        if (marcaProduto.getEmpresa() == null || marcaProduto.getEmpresa().getId() == null || marcaProduto.getEmpresa().getId() <= 0) {
            throw new ExceptionLoja("Empresa Responsável deve ser informada.");
        }

        if (marcaProduto.getId() == null) {
            List<MarcaProduto> marcaProdutos =  marcaRepository.buscarMarcaDesc(marcaProduto.getNomeDesc().toUpperCase());
            if (!marcaProdutos.isEmpty()) {
                throw new ExceptionLoja("Já existe Marca com a descrição cadastrada " + marcaProduto.getNomeDesc());
            }
        }


        MarcaProduto marcaProdutoSalvo= marcaRepository.save(marcaProduto);

        return new ResponseEntity<MarcaProduto>(marcaProdutoSalvo, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/deleteMarca")
    public ResponseEntity<?> deleteMarca(@RequestBody MarcaProduto marcaProduto) {

        marcaRepository.deleteById(marcaProduto.getId());
        return new ResponseEntity("Marca Produto Removido",HttpStatus.OK);
    }

    @ResponseBody
    @DeleteMapping(value = "/deleteMarcaPorId/{id}")
    public ResponseEntity<?> deleteMarcaPorId(@PathVariable("id")Long id) {

        marcaRepository.deleteById(id);
        return new ResponseEntity("Marca Produto Removido",HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/obterMarcaProduto/{id}")
    public ResponseEntity<MarcaProduto> obterMarcaProduto(@PathVariable("id")Long id) throws ExceptionLoja {

        MarcaProduto marcaProduto=  marcaRepository.findById(id).orElse(null);

        if (marcaProduto == null) {
            throw new ExceptionLoja("Não econtrado a marca com o código "+ id);
        }
        return new ResponseEntity<MarcaProduto>(marcaProduto,HttpStatus.OK);
    }
    @ResponseBody
    @GetMapping(value = "/buscarMarcaProdutoPorDesc/{desc}")
    public ResponseEntity<List<MarcaProduto>> buscarMarcaProdutoPorDesc(@PathVariable("desc")String desc) {

        List<MarcaProduto> marcaProdutos =  marcaRepository.buscarMarcaDesc(desc.toUpperCase());
        return new ResponseEntity<List<MarcaProduto>>(marcaProdutos,HttpStatus.OK);
    }
}
