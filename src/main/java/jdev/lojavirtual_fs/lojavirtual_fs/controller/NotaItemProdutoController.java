package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.model.NotaItemProduto;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Produto;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.NotaItemProdutoRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class NotaItemProdutoController {

    @Autowired
    private NotaItemProdutoRepository notaItemProdutoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @ResponseBody
    @PostMapping(value = "/salvarNotaItemProduto")
    public ResponseEntity<NotaItemProduto> salvarNotaItemProduto(@RequestBody @Valid NotaItemProduto notaItemProduto) throws ExceptionLoja {
        if (notaItemProduto.getProduto() == null || notaItemProduto.getProduto().getId() <= 0) {
            throw new ExceptionLoja("O produto deve ser informado");
        }

        if (notaItemProduto.getNotaFiscalCompra() == null || notaItemProduto.getNotaFiscalCompra().getId() <= 0) {
            throw new ExceptionLoja("A nota deve ser informada");
        }

        if (notaItemProduto.getEmpresa() == null || notaItemProduto.getEmpresa().getId() <= 0) {
            throw new ExceptionLoja("A empresa deve ser informada");
        }

        if (notaItemProduto.getId() == null) {
             List<NotaItemProduto> notaExistente = notaItemProdutoRepository.buscaNotaItemProdutoNota(
                     notaItemProduto.getProduto().getId(),
                     notaItemProduto.getNotaFiscalCompra().getId());

             if (!notaExistente.isEmpty()) {
                throw new ExceptionLoja("Já existe este produto cadastrado para esta nota.");
             }
         }

        if (notaItemProduto.getProduto().getId() != null) {
            Optional<Produto> produtoExistente = produtoRepository.findById(notaItemProduto.getProduto().getId());
            if (!produtoExistente.isPresent()) {
                throw new ExceptionLoja("Produto com ID " + notaItemProduto.getProduto().getId() + " não encontrado");
            }
            // Atualiza o objeto produto com os dados do banco
            notaItemProduto.setProduto(produtoExistente.get());
        }

        NotaItemProduto notaItemSalva = notaItemProdutoRepository.save(notaItemProduto);
        // Para carregar o Json com todos os dados
        notaItemSalva = notaItemProdutoRepository.findById(notaItemProduto.getId()).get();

        return new ResponseEntity<NotaItemProduto>(notaItemSalva, HttpStatus.OK);
    }

    @ResponseBody
    @DeleteMapping(value = "/deleteNotaItemProdutoPorId/{id}")
    public ResponseEntity<?> deleteNotaItemProdutoPorId(@PathVariable("id")Long id) {

        //notaItemProdutoRepository.deleteItemNotaFiscalCompra(id);
        notaItemProdutoRepository.deleteById(id);

        return new ResponseEntity("ìtem Nota Fiscal de Produto Removido",HttpStatus.OK);
    }
}
