package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Acesso;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Produto;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.AcessoRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.MarcaProdutoRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.ProdutoRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.AcessoService;
import jdev.lojavirtual_fs.lojavirtual_fs.service.ServiceSendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RestController
public class ProdutoController {
    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ServiceSendEmail serviceSendEmail;

    @ResponseBody
    @PostMapping(value = "/salvarProduto")
    public ResponseEntity<Produto> salvarAcesso(@RequestBody @Valid Produto produto) throws ExceptionLoja, IOException {
        if (produto.getTipoUnidade() == null || produto.getTipoUnidade().trim().isEmpty()) {
            throw new ExceptionLoja("Tipo unidade deve ser informada.");
        }

        if (produto.getNome().length() < 9) {
            throw new ExceptionLoja("Nome do produto deve ter no mínimo 9 caracteres.");
        }

        if (produto.getQtdeEstoque() < 1) {
            throw new ExceptionLoja("Quantidade de estoque deve ser no mínimo 1.");
        }

        if (produto.getImagens() == null || produto.getImagens().isEmpty() || produto.getImagens().size() == 0) {
            throw new ExceptionLoja("Deve ser informado imagem para o produto");
        }

        if (produto.getImagens().size() < 3) {
            throw new ExceptionLoja("Deve ser informado pelo menos 3 imagens para o produto");
        }

        if (produto.getImagens().size() > 6) {
            throw new ExceptionLoja("Deve ser informado no máximo, 6 imagens para o produto");
        }
        if (produto.getId() == null ) {
            for (int x = 0; x < produto.getImagens().size(); x++) {
                produto.getImagens().get(x).setProduto(produto);

                String base64Image = "";


                if (produto.getImagens().get(x).getImagemOriginal().contains("data:image")) {
                    base64Image = produto.getImagens().get(x).getImagemOriginal().split(",")[1];
                }else {
                    base64Image = produto.getImagens().get(x).getImagemOriginal();
                }

                // byte[] imageBytes DatatypeConverter.parseBase64Binary(base64Image) -> sibstituindo
                // Decodificando Base64
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
                if (bufferedImage != null) {
                     int type = bufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : bufferedImage.getType();
                     int largura = Integer.parseInt("800");
                     int altura = Integer.parseInt("600");

                    BufferedImage resizedImage = new BufferedImage(largura, altura, type);
                    Graphics2D g = resizedImage.createGraphics();
                    g.drawImage(bufferedImage, 0, 0, largura, altura, null);
                    g.dispose();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(resizedImage, "png", baos);

                    String miniImgBase64= "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());

                    produto.getImagens().get(x).setImagemMiniatura(miniImgBase64);

                    bufferedImage.flush();
                    resizedImage.flush();
                    //baos.flush();
                    //baos.close();
                }
            }
        }
        // (notaFiscalCompra.getEmpresa() == null || notaFiscalCompra.getEmpresa().getId() == null || notaFiscalCompra.getEmpresa().getId() <= 0)

        if (produto.getAlertaQtdeEstoque() && produto.getQtdeEstoque() <= 1) {
            StringBuilder mensagemHtml = new StringBuilder();
            mensagemHtml.append("<!DOCTYPE html>");
            mensagemHtml.append("<html>");
            mensagemHtml.append("<head><meta charset='UTF-8'></head>");
            mensagemHtml.append("<body>");
            mensagemHtml.append("<h2>Quantidade Estoque - Loja Virtual</h2>");
            //mensagemHtml.append("<p><b>Segue abaixo seus dados do estoque da loja virtual:</b></p>");
            mensagemHtml.append("<p><b>Produto :</b> ").
                    append(produto.getNome()).
                    append(" com estoque baixo" + produto.getQtdeEstoque()).append("</p>");
            mensagemHtml.append("<p> Id Prod.: ").append(produto.getId()).append("</p>");
            mensagemHtml.append("<p>Atenção!</p>");
            mensagemHtml.append("</body>");
            mensagemHtml.append("</html>");
            try {
                serviceSendEmail.enviarEmailHtml("Produto sem qtde de estoque", mensagemHtml.toString(), produto.getEmpresa().getEmail());
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

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
