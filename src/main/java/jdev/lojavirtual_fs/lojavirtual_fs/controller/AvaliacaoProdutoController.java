package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Acesso;
import jdev.lojavirtual_fs.lojavirtual_fs.model.AvaliacaoProduto;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.AvaliacaoProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
public class AvaliacaoProdutoController {

    @Autowired
    private AvaliacaoProdutoRepository avaliacaoProdutoRepository;

    @ResponseBody
    @PostMapping(value = "/salvarAvaliacaoProduto")
    public ResponseEntity<AvaliacaoProduto> salvarAvaliacaoProduto(@RequestBody @Valid AvaliacaoProduto avaliacaoProduto) throws ExceptionLoja {

        if (avaliacaoProduto.getEmpresa() == null || (avaliacaoProduto.getEmpresa().getId() != null && avaliacaoProduto.getEmpresa().getId() <= 0)) {

            throw new ExceptionLoja("Avaliação deve conter a empresa associada.");
        }

        if (avaliacaoProduto.getProduto() == null || (avaliacaoProduto.getProduto().getId() != null && avaliacaoProduto.getProduto().getId() <= 0)) {

            throw new ExceptionLoja("Avaliação deve conter o produto associado.");
        }

        if (avaliacaoProduto.getPessoa() == null || (avaliacaoProduto.getPessoa().getId() != null && avaliacaoProduto.getPessoa().getId() <= 0)) {

            throw new ExceptionLoja("Avaliação deve conter a pessoa associado.");
        }

        avaliacaoProduto = avaliacaoProdutoRepository.saveAndFlush(avaliacaoProduto);
        return  new ResponseEntity<AvaliacaoProduto>(avaliacaoProduto, HttpStatus.OK);
    }

    @ResponseBody
    @DeleteMapping(value = "/deleteAvaliacaoPessoa/{idAvaliacao}")
    public ResponseEntity<?> deleteAvaliacaoPessoa(@PathVariable("idAvaliacao")Long idAvaliacao) {

        avaliacaoProdutoRepository.deleteById(idAvaliacao);

        return new ResponseEntity("Avaliação Removida",HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/avaliacaoProduto/{idProduto}")
    public ResponseEntity<List<AvaliacaoProduto>> avaliacaoProduto(@PathVariable("idProduto")Long idProduto) {

        List<AvaliacaoProduto> avaliacaoProdutos =  avaliacaoProdutoRepository.avaliacaoProduto(idProduto);
        return new ResponseEntity<List<AvaliacaoProduto>>(avaliacaoProdutos,HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/avaliacaoProdutoPessoa/{idProduto}/{idPessoa}")
    public ResponseEntity<List<AvaliacaoProduto>>
        avaliacaoProdutoPessoa(@PathVariable("idProduto")Long idProduto, @PathVariable("idPessoa")Long idPessoa ) {

        List<AvaliacaoProduto> avaliacaoProdutos =  avaliacaoProdutoRepository.avaliacaoProdutoPessoa(idProduto, idPessoa);
        return new ResponseEntity<List<AvaliacaoProduto>>(avaliacaoProdutos,HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/avaliacaoPessoa/{idPessoa}")
    public ResponseEntity<List<AvaliacaoProduto>> avaliacaoPessoa(@PathVariable("idPessoa")Long idPessoa) {

        List<AvaliacaoProduto> avaliacaoProdutos =  avaliacaoProdutoRepository.avaliacaoPessoa(idPessoa);
        return new ResponseEntity<List<AvaliacaoProduto>>(avaliacaoProdutos,HttpStatus.OK);
    }

}
