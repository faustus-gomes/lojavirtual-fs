package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.ImagemProdutoDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.model.ImagemProduto;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.ImagemProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RestController
public class ImagemProdutoController {

    @Autowired
    private ImagemProdutoRepository imagemProdutoRepository;

    @ResponseBody
    @PostMapping(value = "/salvarImagemProduto")
    public ResponseEntity<ImagemProdutoDTO> salvarImagemProduto(@RequestBody @Valid ImagemProduto imagemProduto) throws ExceptionLoja {

        ImagemProduto imagemProdutoSalvo =  imagemProdutoRepository.saveAndFlush(imagemProduto);

        ImagemProdutoDTO imagemProdutoDTO = new ImagemProdutoDTO();
        imagemProdutoDTO.setId(imagemProduto.getId());
        imagemProdutoDTO.setEmpresa(imagemProduto.getEmpresa().getId());
        imagemProdutoDTO.setProduto(imagemProduto.getProduto().getId());
        imagemProdutoDTO.setImagemMiniatura(imagemProduto.getImagemMiniatura());
        imagemProdutoDTO.setImagemOriginal(imagemProduto.getImagemOriginal());

        return new ResponseEntity<ImagemProdutoDTO>(imagemProdutoDTO, HttpStatus.OK);
    }

    @ResponseBody
    @DeleteMapping("/deleteTodasImagensProduto/{idProduto}")
    public ResponseEntity<?> deleteTodasImagensProduto(@PathVariable("idProduto") Long idProduto) {

        imagemProdutoRepository.deleteImagem(idProduto);
        return new ResponseEntity<>("Imagens do produto removida", HttpStatus.OK);
    }

    @ResponseBody
    @DeleteMapping("/deleteImagemObjeto")
    public ResponseEntity<?> deleteImagemObjeto(@RequestBody ImagemProduto imagemProduto) {
        if(!imagemProdutoRepository.existsById(imagemProduto.getId())) {
            return new ResponseEntity<String>("Imagem já removida ou não existe com esse id: "+ imagemProduto.getId(), HttpStatus.OK);
        }


        imagemProdutoRepository.deleteById(imagemProduto.getId());

        return new ResponseEntity<>("Imagem removida", HttpStatus.OK);
    }
    @ResponseBody
    @DeleteMapping("/deleteImagemProdutoPorId/{id}")
    public ResponseEntity<?> deleteImagemProdutoPorId(@PathVariable("id") Long id) {
        if(!imagemProdutoRepository.existsById(id)) {
            return new ResponseEntity<String>("Imagem já removida", HttpStatus.OK);
        }

        imagemProdutoRepository.deleteById(id);
        return new ResponseEntity<>("Imagem removida", HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping("/obterImagemProduto/{idProduto}")
    public ResponseEntity<List<ImagemProdutoDTO>> obterImagemProduto(@PathVariable("idProduto") Long idProduto) {
        List<ImagemProdutoDTO> dtos = new ArrayList<ImagemProdutoDTO>();

        List<ImagemProduto> imagemProdutos = imagemProdutoRepository.buscaImagemProduto(idProduto);

        for (ImagemProduto imagemProduto : imagemProdutos) {
            ImagemProdutoDTO imagemProdutoDTO = new ImagemProdutoDTO();
            imagemProdutoDTO.setId(imagemProduto.getId());
            imagemProdutoDTO.setEmpresa(imagemProduto.getEmpresa().getId());
            imagemProdutoDTO.setProduto(imagemProduto.getProduto().getId());
            imagemProdutoDTO.setImagemMiniatura(imagemProduto.getImagemMiniatura());
            imagemProdutoDTO.setImagemOriginal(imagemProduto.getImagemOriginal());

            dtos.add(imagemProdutoDTO);
        }

        return  new ResponseEntity<List<ImagemProdutoDTO>>(dtos, HttpStatus.OK);
    }
}
