package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Acesso;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Produto;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.AcessoRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.MarcaProdutoRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.ProdutoRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.AcessoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
public class ProdutoController {
    @Autowired
    private ProdutoRepository produtoRepository;

    @ResponseBody
    @PostMapping(value = "/salvarProduto")
    public ResponseEntity<Produto> salvarAcesso(@RequestBody @Valid Produto produto) throws ExceptionLoja {
        if (produto.getEmpresa() == null || produto.getEmpresa().getId() <= 0) {
            throw new ExceptionLoja("Empresa Responsável deve ser informada.");
        }

        if (produto.getId() == null) {
            List<Produto> produtos;
            produtos= produtoRepository.buscarProdutoNome(produto.getNome().toUpperCase(), produto.getEmpresa().getId());
            if (!produtos.isEmpty()) {
                throw new ExceptionLoja("Já existe esta descrição cadastrada " + produto.getNome());
            }
        }

        if ( (produto.getMarcaProduto()) == null || produto.getMarcaProduto().getId() <= 0) {
            throw new ExceptionLoja("Marca do produto do Responsável deve ser informada.");
        }

        if ( (produto.getCategoriaProduto()) == null || produto.getCategoriaProduto().getId() <= 0) {
            throw new ExceptionLoja("Categoria do produto do Responsável deve ser informada.");
        }


        Produto produtoSalvo = produtoRepository.save(produto);

        return new ResponseEntity<Produto>(produtoSalvo, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/deleteProduto")
    public ResponseEntity<?> deleteProduto(@RequestBody Produto produto) {

        produtoRepository.deleteById(produto.getId());
        return new ResponseEntity("Produto Removido",HttpStatus.OK);
    }

    //@Secured({"ROLE_GERENTE","ROLE_ADMIN"})
    @ResponseBody
    @DeleteMapping(value = "/deleteProdutoPorId/{id}")
    public ResponseEntity<?> deleteProdutoPorId(@PathVariable("id")Long id) {

        produtoRepository.deleteById(id);
        return new ResponseEntity("Produto Removido",HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/obterProduto/{id}")
    public ResponseEntity<Produto> obterProduto(@PathVariable("id")Long id) throws ExceptionLoja {

        Produto produto=  produtoRepository.findById(id).orElse(null);

        if (produto == null) {
            throw new ExceptionLoja("Náo econtrado o acesso com o código "+ id);
        }
        return new ResponseEntity<Produto>(produto,HttpStatus.OK);
    }
    @ResponseBody
    @GetMapping(value = "/buscarPrdNome/{desc}")
    public ResponseEntity<List<Produto>> buscarPrdNome(@PathVariable("desc")String desc) {

        List<Produto> produto =  produtoRepository.buscarProdutoNome(desc.toUpperCase());
        return new ResponseEntity<List<Produto>>(produto,HttpStatus.OK);
    }
}
