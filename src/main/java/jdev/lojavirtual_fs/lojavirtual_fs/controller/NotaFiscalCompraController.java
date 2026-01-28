package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.ObjetoReqRelatorioProdAlertaEstoqueDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.ObjetoReqRelatorioProdNFDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.model.NotaFiscalCompra;
import jdev.lojavirtual_fs.lojavirtual_fs.model.NotaFiscalVenda;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.NotaFiscalCompraRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.NotaFiscalVendaRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.NotaFiscalCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class NotaFiscalCompraController {

    @Autowired
    private NotaFiscalCompraRepository notaFiscalCompraRepository;
    @Autowired
    private NotaFiscalVendaRepository notaFiscalVendaRepository;
    @Autowired
    private NotaFiscalCompraService notaFiscalCompraService;

    @ResponseBody
    @PostMapping(value = "/relatorioProdCompradoNF")
    public ResponseEntity<List<ObjetoReqRelatorioProdNFDTO>> relatorioProdCompradoNF(
            @RequestBody ObjetoReqRelatorioProdNFDTO objetoReqRelatorioProdNFDTO) {
        List<ObjetoReqRelatorioProdNFDTO> retorno = new ArrayList<ObjetoReqRelatorioProdNFDTO>();

        retorno = notaFiscalCompraService.gerarRelatorioProdCompraNF(objetoReqRelatorioProdNFDTO);

        return new ResponseEntity<List<ObjetoReqRelatorioProdNFDTO>>(retorno, HttpStatus.OK);
    }

    // Opcional: Adicionar endpoint GET também
    @GetMapping(value = "/relatorioProdCompradoNF")
    public ResponseEntity<List<ObjetoReqRelatorioProdNFDTO>> relatorioProdCompradoNFGet(
            @RequestParam(required = false) String nomeProduto,
            @RequestParam(required = false) String codigoProduto,
            @RequestParam(required = false) String codigoNta,
            @RequestParam(required = false) String codigoFornecedor,
            @RequestParam(required = false) String nomeForcenecedor,
            @RequestParam(required = false) String dataInicial,
            @RequestParam(required = false) String dataFinal,
            @RequestParam(required = false) String dataCompra) {

        ObjetoReqRelatorioProdNFDTO filtros = new ObjetoReqRelatorioProdNFDTO();
        filtros.setNomeProduto(nomeProduto);
        filtros.setCodigoProduto(codigoProduto);
        filtros.setCodigoNta(codigoNta);
        filtros.setCodigoFornecedor(codigoFornecedor);
        filtros.setNomeForcenecedor(nomeForcenecedor);
        filtros.setDataInicial(dataInicial);
        filtros.setDataFinal(dataFinal);
        filtros.setDataCompra(dataCompra);

        List<ObjetoReqRelatorioProdNFDTO> retorno =
                notaFiscalCompraService.gerarRelatorioProdCompraNF(filtros);

        return new ResponseEntity<>(retorno, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/relatorioAlertaProdNF")
    public ResponseEntity<List<ObjetoReqRelatorioProdAlertaEstoqueDTO>> relatorioAlertaProdNF(
            @RequestBody ObjetoReqRelatorioProdAlertaEstoqueDTO objetoReqRelatorioProdAlertaEstoqueDTO) {
        List<ObjetoReqRelatorioProdAlertaEstoqueDTO> retorno = new ArrayList<ObjetoReqRelatorioProdAlertaEstoqueDTO>();

        retorno = notaFiscalCompraService.gerarRelatorioAlertaEstoque(objetoReqRelatorioProdAlertaEstoqueDTO);

        return new ResponseEntity<List<ObjetoReqRelatorioProdAlertaEstoqueDTO>>(retorno, HttpStatus.OK);
    }

    @ResponseBody
    @PostMapping(value = "/salvarNotaFiscalCompra")
    public ResponseEntity<NotaFiscalCompra> salvarNotaFiscalCompra(@RequestBody @Valid NotaFiscalCompra notaFiscalCompra) throws ExceptionLoja {
        if (notaFiscalCompra.getEmpresa() == null || notaFiscalCompra.getEmpresa().getId() == null || notaFiscalCompra.getEmpresa().getId() <= 0) {

            throw new ExceptionLoja("Empresa Responsável deve ser informada.");
        }

        if (notaFiscalCompra.getPessoa() == null || notaFiscalCompra.getPessoa().getId() == null || notaFiscalCompra.getPessoa().getId() <= 0) {

            throw new ExceptionLoja("Pessoa Responsável deve ser informada.");
        }

        if (notaFiscalCompra.getContaPagar() == null || notaFiscalCompra.getContaPagar().getId() == null || notaFiscalCompra.getContaPagar().getId() <= 0) {

            throw new ExceptionLoja("Contas Pagar deve ser informada.");
        }

        if (notaFiscalCompra.getId() == null) {
            //List<NotaFiscalCompra> notaFiscalCompras =  notaFiscalCompraRepository.buscarNotaDesc(notaFiscalCompra.getDescricaoObs().toUpperCase());

            if (notaFiscalCompra.getDescricaoObs() != null) {

                //List<NotaFiscalCompra> fiscalCompras = notaFiscalCompraRepository.buscarNotaDesc(notaFiscalCompra.getDescricaoObs().toUpperCase());
                 boolean existe = notaFiscalCompraRepository.existeNotaComDescricao(notaFiscalCompra.getDescricaoObs().toUpperCase());
                if (existe) {
                    throw new ExceptionLoja("Já existe Nota Fiscal de Compra com essa mesma descrição :" + notaFiscalCompra.getDescricaoObs());
                }
            }
        }



        NotaFiscalCompra notaFiscalCompraSalvo= notaFiscalCompraRepository.save(notaFiscalCompra);

        return new ResponseEntity<NotaFiscalCompra>(notaFiscalCompraSalvo, HttpStatus.OK);
    }

    /*@ResponseBody
    @PostMapping(value = "/deleteNotaFiscalCompra")
    public ResponseEntity<?> deleteNotaFiscalCompra(@RequestBody NotaFiscalCompra notaFiscalCompra) {

        notaFiscalCompraRepository.deleteById(notaFiscalCompra.getId());
        return new ResponseEntity("Nota Fiscal de Compra Removida",HttpStatus.OK);
    }*/

    @ResponseBody
    @DeleteMapping(value = "/deleteNotaFiscalCompraPorId/{id}")
    public ResponseEntity<?> deleteNotaFiscalCompraPorId(@PathVariable("id")Long id) {

        notaFiscalCompraRepository.deleteItemNotaFiscalCompra(id);
        notaFiscalCompraRepository.deleteById(id);

        return new ResponseEntity("Nota Fiscal de Compra Removida",HttpStatus.OK);
    }
    @ResponseBody
    @GetMapping(value = "/obterNotaFiscalCompra/{id}")
    public ResponseEntity<NotaFiscalCompra> obterNotaFiscalCompra(@PathVariable("id")Long id) throws ExceptionLoja {

        NotaFiscalCompra notaFiscalCompra=  notaFiscalCompraRepository.findById(id).orElse(null);

        if (notaFiscalCompra == null) {
            throw new ExceptionLoja("Não econtrado a Nota Fiscal de Compra com o código "+ id);
        }
        return new ResponseEntity<NotaFiscalCompra>(notaFiscalCompra,HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/obterNotaFiscalCompradaVanda/{idVenda}")
    public ResponseEntity<List<NotaFiscalVenda>> obterNotaFiscalCompradaVanda(@PathVariable("idVenda")Long  idVenda) throws ExceptionLoja {

        List<NotaFiscalVenda> notaFiscalVenda =  notaFiscalVendaRepository.buscaNotaPorVenda(idVenda);

        if (notaFiscalVenda == null) {
            throw new ExceptionLoja("Não econtrado a Nota Fiscal de Venda com o código "+ idVenda);
        }
        return new ResponseEntity<List<NotaFiscalVenda>>(notaFiscalVenda,HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/obterNotaFiscalCompradaVendaUnico/{idVenda}")
    public ResponseEntity<NotaFiscalVenda> obterNotaFiscalCompradaVendaUnico(@PathVariable("idVenda")Long idVenda) throws ExceptionLoja {

        NotaFiscalVenda notaFiscalVenda= notaFiscalVendaRepository.buscaNotaPorVendaUnica(idVenda);

        if (notaFiscalVenda == null) {
            throw new ExceptionLoja("Não econtrado a Nota Fiscal de Venda com o código "+ idVenda);
        }
        return new ResponseEntity<NotaFiscalVenda>(notaFiscalVenda,HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/buscarNotaFiscalCompraPorDesc/{desc}")
    public ResponseEntity<List<NotaFiscalCompra>> buscarNotaFiscalCompraPorDesc(@PathVariable("desc")String desc) {

        List<NotaFiscalCompra> notaFiscalCompras =  notaFiscalCompraRepository.buscarNotaDesc(desc.toUpperCase());
        return new ResponseEntity<List<NotaFiscalCompra>>(notaFiscalCompras,HttpStatus.OK);
    }

}
