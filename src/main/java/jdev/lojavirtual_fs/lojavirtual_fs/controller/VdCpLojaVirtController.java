package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import com.jayway.jsonpath.JsonPath;
import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.FiltroVendaDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.ItemVendaDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.VdCpLojaVirtRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.VendaCompraLojaVirtualDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.model.*;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.*;
import jdev.lojavirtual_fs.lojavirtual_fs.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RestController
public class VdCpLojaVirtController {

    @Autowired
    private VdCpLojaVirtRepository vdCpLojaVirtRepository;
    @Autowired
    private EnderecoReposity enderecoReposity;
    @Autowired
    private PessoaController pessoaController;
    @Autowired
    private NotaFiscalVendaRepository notaFiscalVendaRepository;
    @Autowired
    private StatusRastreioRepository statusRastreioRepository;
    @Autowired
    private VendaService vendaService;

    @ResponseBody
    @PostMapping(value = "/salvarVendaLoja")
    public ResponseEntity<VendaCompraLojaVirtualDTO> salvarVendaLoja(@RequestBody @Valid VendaCompraLojaVirtual vendaCompraLojaVirtual) {

        vendaCompraLojaVirtual.getPessoa().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
        PessoaFisica pessoaFisica = pessoaController.salvarPf(vendaCompraLojaVirtual.getPessoa()).getBody();
        vendaCompraLojaVirtual.setPessoa(pessoaFisica);

        vendaCompraLojaVirtual.getEnderecoCobranca().setPessoa(pessoaFisica);
        vendaCompraLojaVirtual.getEnderecoCobranca().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
        Endereco enderecCobranca =  enderecoReposity.save(vendaCompraLojaVirtual.getEnderecoCobranca());
        vendaCompraLojaVirtual.setEnderecoCobranca(enderecCobranca);

        vendaCompraLojaVirtual.getEnderecoEntrega().setPessoa(pessoaFisica);
        vendaCompraLojaVirtual.getEnderecoEntrega().setEmpresa(vendaCompraLojaVirtual.getEmpresa());
        Endereco enderecoEntrega = enderecoReposity.save(vendaCompraLojaVirtual.getEnderecoEntrega());
        vendaCompraLojaVirtual.setEnderecoEntrega(enderecoEntrega);

        vendaCompraLojaVirtual.getNotaFiscalVenda().setEmpresa(vendaCompraLojaVirtual.getEmpresa());

        for (int i = 0; i < vendaCompraLojaVirtual.getItemVendaLojas().size(); i++) {
             vendaCompraLojaVirtual.getItemVendaLojas().get(i).setEmpresa(vendaCompraLojaVirtual.getEmpresa());
             vendaCompraLojaVirtual.getItemVendaLojas().get(i).setVendaCompraLojaVirtual(vendaCompraLojaVirtual);
        }

        /* Salva primeiramente a venda gravada no banco com aNF */
        vendaCompraLojaVirtual = vdCpLojaVirtRepository.saveAndFlush(vendaCompraLojaVirtual);

        StatusRastreio statusRastreio = new StatusRastreio();
        statusRastreio.setCentroDistribuicao("Loja Local");
        statusRastreio.setCidade("Local");
        statusRastreio.setEmpresa(vendaCompraLojaVirtual.getEmpresa());
        statusRastreio.setEstado("Local");
        statusRastreio.setVendaCompraLojaVirtual(vendaCompraLojaVirtual);

        statusRastreioRepository.save(statusRastreio);


        /* Associa a venda gravada no banco com a NF */
        vendaCompraLojaVirtual.getNotaFiscalVenda().setVendaCompraLojaVirtual(vendaCompraLojaVirtual);

        /* Persiste novamenre as NFs para amarrar na venda*/
        notaFiscalVendaRepository.saveAndFlush(vendaCompraLojaVirtual.getNotaFiscalVenda());
        /* **************************************************************************
         * Estamos usando o DTO, para evitar em alguns casos Loops demorados        *
         * e recursividade.                                                         *
        *****************************************************************************/
        VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();
        compraLojaVirtualDTO.setValorTotal(vendaCompraLojaVirtual.getValorTotal());
        compraLojaVirtualDTO.setPessoa(vendaCompraLojaVirtual.getPessoa());

        compraLojaVirtualDTO.setEntrega(vendaCompraLojaVirtual.getEnderecoEntrega());
        compraLojaVirtualDTO.setCobranca(vendaCompraLojaVirtual.getEnderecoCobranca());

        compraLojaVirtualDTO.setValorDesc(vendaCompraLojaVirtual.getValorDesconto());
        compraLojaVirtualDTO.setValorfrete(vendaCompraLojaVirtual.getValorFret());

        for (ItemVendaLoja item: vendaCompraLojaVirtual.getItemVendaLojas()){

            ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
            itemVendaDTO.setQuantidade(item.getQuantidade());
            itemVendaDTO.setProduto(item.getProduto());

            compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
        }

        return  new ResponseEntity<VendaCompraLojaVirtualDTO>(compraLojaVirtualDTO, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/consultaVendaId/{id}")
    public ResponseEntity<VendaCompraLojaVirtualDTO> consultaVendaId(@PathVariable("id") Long idVenda) {

        VendaCompraLojaVirtual compraLojaVirtual = vdCpLojaVirtRepository.findByIdExclusao(idVenda);
        if (compraLojaVirtual == null) {
            compraLojaVirtual = new VendaCompraLojaVirtual();
        }


        VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();

        compraLojaVirtualDTO.setValorTotal(compraLojaVirtual.getValorTotal());
        compraLojaVirtualDTO.setPessoa(compraLojaVirtual.getPessoa());

        compraLojaVirtualDTO.setEntrega(compraLojaVirtual.getEnderecoEntrega());
        compraLojaVirtualDTO.setCobranca(compraLojaVirtual.getEnderecoCobranca());

        compraLojaVirtualDTO.setValorDesc(compraLojaVirtual.getValorDesconto());
        compraLojaVirtualDTO.setValorfrete(compraLojaVirtual.getValorFret());
        compraLojaVirtualDTO.setId(compraLojaVirtual.getId());

        for (ItemVendaLoja item: compraLojaVirtual.getItemVendaLojas()){

            ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
            itemVendaDTO.setQuantidade(item.getQuantidade());
            itemVendaDTO.setProduto(item.getProduto());

            compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
        }

        return  new ResponseEntity<VendaCompraLojaVirtualDTO>(compraLojaVirtualDTO, HttpStatus.OK);
    }

    @ResponseBody
    @DeleteMapping(value = "/deleteVendaTotalBanco/{idVenda}")
    public ResponseEntity<String> deleteVendaTotalBanco(@PathVariable(value = "idVenda") Long idVenda) {

        vdCpLojaVirtRepository.excluiTotalVendaBanco(idVenda);
        return new ResponseEntity<String>("Venda excluída com sucesso", HttpStatus.OK);
    }

    //Exclusão Lógica
    @ResponseBody
    @DeleteMapping(value = "/deleteVendaTotal/{idVenda}")
    public ResponseEntity<String> deleteVendaTotal(@PathVariable(value = "idVenda") Long idVenda) {

        vdCpLojaVirtRepository.excluiTotalVenda(idVenda);
        return new ResponseEntity<String>("Venda excluída com sucesso", HttpStatus.OK);
    }

    @ResponseBody
    @PutMapping(value = "/ativaRegistroVendas/{idVenda}")
    public ResponseEntity<String> ativaRegistroVendas(@PathVariable(value = "idVenda") Long idVenda) {

        vdCpLojaVirtRepository.ativaRegistroVendas(idVenda);
        return new ResponseEntity<String>("Venda ativada com sucesso", HttpStatus.OK);
    }


    @Transactional(readOnly = true)
    @ResponseBody
    @GetMapping(value = "/consultaVendaPorProdutoId/{id}")
    public ResponseEntity<?> consultaVendaPorProdutoId(@PathVariable("id") Long idProd) {
        // Adicione logs para debug
        System.out.println("=== Buscando vendas para produto ID: " + idProd + " ===");
        try {
        //Busca Vendas
        List<VendaCompraLojaVirtual> compraLojaVirtual = vdCpLojaVirtRepository.vendaPorProduto(idProd);

        // Log para verificar quantas vendas foram encontradas
        System.out.println("Total de vendas encontradas: " + (compraLojaVirtual != null ? compraLojaVirtual.size() : 0));

        // 2. Verifica se a lista é nula ou vazia
        if (compraLojaVirtual == null || compraLojaVirtual.isEmpty()) {
            System.out.println("Nenhuma venda encontrada para o produto " + idProd);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        }

        if (compraLojaVirtual == null) {
            compraLojaVirtual = new ArrayList<VendaCompraLojaVirtual>();
        }

        // 3. Log dos IDs encontrados
        System.out.print("IDs das vendas encontradas: ");
        for (VendaCompraLojaVirtual venda : compraLojaVirtual) {
            System.out.print(venda.getId() + " ");
        }
        System.out.println();

        // 4. Converte para DTO
        List<VendaCompraLojaVirtualDTO> compraLojaVirtualDTOList = new ArrayList<>();
        List<Long> vendasComErro = new ArrayList<>();

        for (VendaCompraLojaVirtual vcl : compraLojaVirtual) {
            try {
            VendaCompraLojaVirtualDTO compraLojaVirtualDTO = new VendaCompraLojaVirtualDTO();

            compraLojaVirtualDTO.setValorTotal(vcl.getValorTotal());
            compraLojaVirtualDTO.setPessoa(vcl.getPessoa());

            compraLojaVirtualDTO.setEntrega(vcl.getEnderecoEntrega());
            compraLojaVirtualDTO.setCobranca(vcl.getEnderecoCobranca());

            compraLojaVirtualDTO.setValorDesc(vcl.getValorDesconto());
            compraLojaVirtualDTO.setValorfrete(vcl.getValorFret());
            compraLojaVirtualDTO.setId(vcl.getId());

            // Verifica se os itens estão carregados
            if (vcl.getItemVendaLojas() != null) {
                for (ItemVendaLoja item : vcl.getItemVendaLojas()) {

                    ItemVendaDTO itemVendaDTO = new ItemVendaDTO();
                    itemVendaDTO.setQuantidade(item.getQuantidade());
                    itemVendaDTO.setProduto(item.getProduto());

                    compraLojaVirtualDTO.getItemVendaLoja().add(itemVendaDTO);
                }
            }else {
                System.out.println("Atenção: Venda ID " + vcl.getId() + " não tem itens carregados!");
            }

            compraLojaVirtualDTOList.add(compraLojaVirtualDTO);
                System.out.println("Venda ID " + vcl.getId() + " convertida com sucesso");

            }catch (ExceptionLoja e) {

                vendasComErro.add(vcl.getId());
                System.err.println("ERRO ao converter venda ID " + vcl.getId() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Total de DTOs criados: " + compraLojaVirtualDTOList.size());
        System.out.println("Vendas com erro: " + vendasComErro);
        return  new ResponseEntity<>(compraLojaVirtualDTOList, HttpStatus.OK);
    } catch (Exception e) {
            System.err.println("ERRO GERAL: " + e.getMessage());
            e.printStackTrace();

            // Cria um Map para o erro
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao processar consulta");
            errorResponse.put("details", e.getMessage());

            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    @GetMapping(value = "/consultaVendaPorProdutoId2/{id}")
    public ResponseEntity<List<Map<String, Object>>> consultaVendaPorProdutoId2(@PathVariable("id") Long idProd) {

        System.out.println("=== INICIANDO CONSULTA PARA PRODUTO " + idProd + " ===");

        // Use a consulta com FETCH
        List<VendaCompraLojaVirtual> vendas = vdCpLojaVirtRepository.vendaPorProdutoComFetch(idProd);
        System.out.println("Total de vendas encontradas (com fetch): " + vendas.size());

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (VendaCompraLojaVirtual venda : vendas) {
            try {
                Map<String, Object> vendaMap = criarMapVenda(venda, idProd);
                resultado.add(vendaMap);
                System.out.println("✓ Venda ID " + venda.getId() + " processada");

            } catch (Exception e) {
                System.err.println("✗ ERRO na venda ID " + venda.getId() + ": " + e.getMessage());
                e.printStackTrace();

                // Adiciona versão mínima
                Map<String, Object> vendaMinima = new HashMap<>();
                vendaMinima.put("id", venda.getId());
                vendaMinima.put("erro", e.getMessage());
                resultado.add(vendaMinima);
            }
        }

        System.out.println("=== CONSULTA FINALIZADA ===");
        System.out.println("Total no resultado: " + resultado.size());
        return ResponseEntity.ok(resultado);
    }


    @Transactional(readOnly = true)
    @GetMapping(value = "/consultaVendaPorCliente/{id}")
    public ResponseEntity<List<Map<String, Object>>> consultaVendaPorCliente(@PathVariable("id") Long idCliente) {

        System.out.println("=== INICIANDO CONSULTA PARA Cliente " + idCliente + " ===");

        // Use a consulta com FETCH
        List<VendaCompraLojaVirtual> vendas = vdCpLojaVirtRepository.vendaPorClienteComFetch(idCliente);
        System.out.println("Total de vendas encontradas (com fetch): " + vendas.size());

        List<Map<String, Object>> resultado = new ArrayList<>();

        for (VendaCompraLojaVirtual venda : vendas) {
            try {
                Map<String, Object> vendaMap = criarMapVenda(venda, idCliente);
                resultado.add(vendaMap);
                System.out.println("✓ Venda ID " + venda.getId() + " processada");

            } catch (Exception e) {
                System.err.println("✗ ERRO na venda ID " + venda.getId() + ": " + e.getMessage());
                e.printStackTrace();

                // Adiciona versão mínima
                Map<String, Object> vendaMinima = new HashMap<>();
                vendaMinima.put("id", venda.getId());
                vendaMinima.put("erro", e.getMessage());
                resultado.add(vendaMinima);
            }
        }

        System.out.println("=== CONSULTA FINALIZADA ===");
        System.out.println("Total no resultado: " + resultado.size());
        return ResponseEntity.ok(resultado);
    }

    private Map<String, Object> criarMapVenda(VendaCompraLojaVirtual venda, Long produtoId) {
        Map<String, Object> map = new HashMap<>();

        // Dados básicos
        map.put("id", venda.getId());
        map.put("valorTotal", venda.getValorTotal());
        map.put("valorDesconto", venda.getValorDesconto());
        map.put("valorFrete", venda.getValorFret());
        map.put("dataVenda", venda.getDataVenda());

        // Pessoa (somente dados essenciais)
        if (venda.getPessoa() != null) {
            Map<String, Object> pessoaMap = new HashMap<>();
            pessoaMap.put("id", venda.getPessoa().getId());
            pessoaMap.put("nome", venda.getPessoa().getNome());
            pessoaMap.put("email", venda.getPessoa().getEmail());
            pessoaMap.put("telefone", venda.getPessoa().getTelefone());
            // NÃO inclua endereços ou outros relacionamentos
            map.put("pessoa", pessoaMap);
        }

        // Endereços (apenas IDs)
        if (venda.getEnderecoEntrega() != null) {
            map.put("enderecoEntregaId", venda.getEnderecoEntrega().getId());
        }
        if (venda.getEnderecoCobranca() != null) {
            map.put("enderecoCobrancaId", venda.getEnderecoCobranca().getId());
        }

        // Itens (apenas do produto buscado)
        List<Map<String, Object>> itens = new ArrayList<>();
        if (venda.getItemVendaLojas() != null) {
            for (ItemVendaLoja item : venda.getItemVendaLojas()) {
                if (item.getProduto() != null && produtoId.equals(item.getProduto().getId())) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("quantidade", item.getQuantidade());

                    if (item.getProduto() != null) {
                        Map<String, Object> produtoMap = new HashMap<>();
                        produtoMap.put("id", item.getProduto().getId());
                        produtoMap.put("nome", item.getProduto().getNome());
                        produtoMap.put("valorVenda", item.getProduto().getValorVenda());
                        // NÃO inclua empresa, categoria, imagens, etc.
                        itemMap.put("produto", produtoMap);
                    }

                    itens.add(itemMap);
                }
            }
        }
        map.put("itens", itens);

        return map;
    }

    //Padrão de Consultas
    //*******************************************************
    @Transactional(readOnly = true)
    @GetMapping(value = "/consultaVendasComFiltros")
    public ResponseEntity<List<Map<String, Object>>> consultaVendasComFiltros(
            @RequestParam(value = "idProduto", required = false) Long idProduto,
            @RequestParam(value = "nomeProduto", required = false) String nomeProduto,
            @RequestParam(value = "idCliente", required = false) Long idCliente,
            @RequestParam(value = "nomeCliente", required = false) String nomeCliente,
            @RequestParam(value = "cpfCliente", required = false) String cpfCliente,
            @RequestParam(value = "emailCliente", required = false) String emailCliente,
            @RequestParam(value = "dataInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(value = "dataFim", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            // Novos parâmetros para endereço de entrega
            @RequestParam(value = "cidadeEntrega", required = false) String cidadeEntrega,
            @RequestParam(value = "estadoEntrega", required = false) String estadoEntrega,
            @RequestParam(value = "bairroEntrega", required = false) String bairroEntrega,
            @RequestParam(value = "cepEntrega", required = false) String cepEntrega,
            // Novos parâmetros para endereço de cobrança
            @RequestParam(value = "cidadeCobranca", required = false) String cidadeCobranca,
            @RequestParam(value = "estadoCobranca", required = false) String estadoCobranca,
            @RequestParam(value = "bairroCobranca", required = false) String bairroCobranca,
            @RequestParam(value = "cepCobranca", required = false) String cepCobranca) {
        // Criar DTO de filtro
        FiltroVendaDTO filtro = FiltroVendaDTO.fromParams(
                idProduto, nomeProduto,idCliente, nomeCliente,
                cpfCliente, emailCliente, dataInicio, dataFim,
                cidadeEntrega, estadoEntrega, bairroEntrega, cepEntrega,
                cidadeCobranca, estadoCobranca, bairroCobranca, cepCobranca);

        // Buscar vendas com filtros
        List<VendaCompraLojaVirtual> vendas = vendaService.buscarVendasComFiltros(filtro);

        // Converter para Map
        List<Map<String, Object>> resultado = vendas.stream()
                .map(venda -> criarMapVendaCompleto(venda, idProduto))
                .collect(Collectors.toList());


        return ResponseEntity.ok(resultado);
    }


    private Map<String, Object> criarMapVendaCompleto(VendaCompraLojaVirtual venda, Long produtoIdFiltro) {
        Map<String, Object> map = new HashMap<>();

        // Dados básicos
        map.put("id", venda.getId());
        map.put("valorTotal", venda.getValorTotal());
        map.put("valorDesconto", venda.getValorDesconto());
        map.put("valorFrete", venda.getValorFret());
        map.put("dataVenda", venda.getDataVenda());

        // Pessoa
        if (venda.getPessoa() != null) {
            Map<String, Object> pessoaMap = new HashMap<>();
            pessoaMap.put("id", venda.getPessoa().getId());
            pessoaMap.put("nome", venda.getPessoa().getNome());
            pessoaMap.put("email", venda.getPessoa().getEmail());
            pessoaMap.put("telefone", venda.getPessoa().getTelefone());
            map.put("pessoa", pessoaMap);
        }

        // Endereço de Entrega
        if (venda.getEnderecoEntrega() != null) {
            Map<String, Object> enderecoEntregaMap = new HashMap<>();
            enderecoEntregaMap.put("id", venda.getEnderecoEntrega().getId());
            enderecoEntregaMap.put("rua", venda.getEnderecoEntrega().getRuaLogra());
            enderecoEntregaMap.put("numero", venda.getEnderecoEntrega().getNumero());
            enderecoEntregaMap.put("bairro", venda.getEnderecoEntrega().getBairro());
            enderecoEntregaMap.put("cidade", venda.getEnderecoEntrega().getCidade());
            enderecoEntregaMap.put("estado", venda.getEnderecoEntrega().getUf());
            enderecoEntregaMap.put("cep", venda.getEnderecoEntrega().getCep());
            enderecoEntregaMap.put("complemento", venda.getEnderecoEntrega().getComplemento());
            map.put("enderecoEntrega", enderecoEntregaMap);
        }

        // Endereço de Cobrança
        if (venda.getEnderecoCobranca() != null) {
            Map<String, Object> enderecoCobrancaMap = new HashMap<>();
            enderecoCobrancaMap.put("id", venda.getEnderecoCobranca().getId());
            enderecoCobrancaMap.put("rua", venda.getEnderecoCobranca().getRuaLogra());
            enderecoCobrancaMap.put("numero", venda.getEnderecoCobranca().getNumero());
            enderecoCobrancaMap.put("bairro", venda.getEnderecoCobranca().getBairro());
            enderecoCobrancaMap.put("cidade", venda.getEnderecoCobranca().getCidade());
            enderecoCobrancaMap.put("estado", venda.getEnderecoCobranca().getUf());
            enderecoCobrancaMap.put("cep", venda.getEnderecoCobranca().getCep());
            enderecoCobrancaMap.put("complemento", venda.getEnderecoCobranca().getComplemento());
            map.put("enderecoCobranca", enderecoCobrancaMap);
        }

        // Itens - filtra por produtoIdFiltro se fornecido
        List<Map<String, Object>> itens =venda.getItemVendaLojas().stream()
                .filter(item -> produtoIdFiltro == null ||
                        (item.getProduto() != null && produtoIdFiltro.equals(item.getProduto().getId())))
                .map(item -> {
                            Map<String, Object> itemMap = new HashMap<>();
                            itemMap.put("id", item.getId());
                            itemMap.put("quantidade", item.getQuantidade());
                            //itemMap.put("valorUnitario", item.getValorUnitario());

                    if (item.getProduto() != null) {
                        Map<String, Object> produtoMap = new HashMap<>();
                        produtoMap.put("id", item.getProduto().getId());
                        produtoMap.put("nome", item.getProduto().getNome());
                        produtoMap.put("valorVenda", item.getProduto().getValorVenda());
                        itemMap.put("produto", produtoMap);
                    }

                   return itemMap;
                }).collect(Collectors.toList());


        map.put("itens", itens);

        return map;
    }


}
