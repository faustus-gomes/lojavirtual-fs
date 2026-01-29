package jdev.lojavirtual_fs.lojavirtual_fs.service;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.ObjetoRelatorioSatusCompraVendasDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.ObjetoReqRelatorioProdAlertaEstoqueDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.ObjetoReqRelatorioProdNFDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotaFiscalCompraService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Title: Relatório de Histórico de Compras
     * Este relatório permite saber as compras de produtos nesta loja
     * Not Fiscal  de Compra/ Venda
     * @param ->gerarRelatorioProdCompraNF
     * @return ->BeanPropertyRowMapper<>(ObjetoReqRelatorioProdNFDTO.class)
     */
    public List<ObjetoReqRelatorioProdNFDTO> gerarRelatorioProdCompraNF(ObjetoReqRelatorioProdNFDTO filtros) {

        // Lista para armazenar os parâmetros da query
        List<Object> parametros = new ArrayList<>();

        //List<ObjetoReqRelatorioProdNFDTO> retorno = new ArrayList<ObjetoReqRelatorioProdNFDTO>();
        // Construir a query SQL dinamicamente
        StringBuilder sql = new StringBuilder();

        // Parte fixa do SQL
        sql.append("SELECT p.id AS codigoProduto, ");
        sql.append("       p.nome AS nomeProduto, ");
        sql.append("       p.valor_venda AS valorProduto, ");
        sql.append("       nip.quantidade AS quantidadeComprada, ");
        sql.append("       pj.id AS codigoFornecedor, ");
        sql.append("       pj.nome AS nomeForcenecedor, ");
        sql.append("       nfc.data_compra AS dataCompra, ");
        sql.append("       nfc.id AS nfCompra ");
        sql.append("FROM nota_fiscal_compra nfc ");
        sql.append("INNER JOIN nota_item_produto nip ON nip.nota_fiscal_compra_id = nfc.id ");
        sql.append("INNER JOIN produto p ON nip.produto_id = p.id ");
        sql.append("INNER JOIN pessoa_juridica pj ON pj.id = nfc.pessoa_id ");
        sql.append("WHERE 1 = 1 ");
        // Facilita adicionar condições com AND
        //retorno = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ObjetoReqRelatorioProdNFDTO.class));

        // Adicionar filtros dinamicamente

        // 1. Filtro por nome do produto (LIKE - busca parcial)
        if (filtros.getNomeProduto() != null && !filtros.getNomeProduto().trim().isEmpty()) {
            sql.append("AND UPPER(p.nome) LIKE UPPER(?) ");
            parametros.add("%" + filtros.getNomeProduto().trim() + "%");
        }

        // 2. Filtro por código do produto
        if (filtros.getCodigoProduto() != null && !filtros.getCodigoProduto().trim().isEmpty()) {
            try {
                // Tentar converter para número
                Integer codigoProduto = Integer.parseInt(filtros.getCodigoProduto().trim());
                sql.append("AND p.id = ? ");
                parametros.add(codigoProduto);
            } catch (NumberFormatException e) {
                // Se não for número, tratar como string
                sql.append("AND CAST(p.id AS TEXT) = ? ");
                parametros.add(filtros.getCodigoProduto().trim());
            }
        }

        // 3. Filtro por código NTA (Nota Fiscal)
        if (filtros.getCodigoNta() != null && !filtros.getCodigoNta().trim().isEmpty()) {
            try {
                Integer codigoNta = Integer.parseInt(filtros.getCodigoNta().trim());
                sql.append("AND nfc.id = ? ");
                parametros.add(codigoNta);
            } catch (NumberFormatException e) {
                sql.append("AND CAST(nfc.id AS TEXT) = ? ");
                parametros.add(filtros.getCodigoNta().trim());
            }
        }

        // 4. Filtro por código do fornecedor
        if (filtros.getCodigoFornecedor() != null && !filtros.getCodigoFornecedor().trim().isEmpty()) {
            try {
                Integer codigoFornecedor = Integer.parseInt(filtros.getCodigoFornecedor().trim());
                sql.append("AND pj.id = ? ");
                parametros.add(codigoFornecedor);
            } catch (NumberFormatException e) {
                sql.append("AND CAST(pj.id AS TEXT) = ? ");
                parametros.add(filtros.getCodigoFornecedor().trim());
            }
        }

        // 5. Filtro por nome do fornecedor (LIKE - busca parcial)
        if (filtros.getNomeForcenecedor() != null && !filtros.getNomeForcenecedor().trim().isEmpty()) {
            sql.append("AND UPPER(pj.nome) LIKE UPPER(?) ");
            parametros.add("%" + filtros.getNomeForcenecedor().trim() + "%");
        }

        // 6. Filtro por data inicial
        if (filtros.getDataInicial() != null && !filtros.getDataInicial().trim().isEmpty()) {
            sql.append("AND nfc.data_compra >= ?::date ");
            parametros.add(filtros.getDataInicial().trim());
        }

        // 7. Filtro por data final
        if (filtros.getDataFinal() != null && !filtros.getDataFinal().trim().isEmpty()) {
            sql.append("AND nfc.data_compra <= ?::date ");
            parametros.add(filtros.getDataFinal().trim());
        }

        // 8. Filtro por data de compra específica
        if (filtros.getDataCompra() != null && !filtros.getDataCompra().trim().isEmpty()) {
            sql.append("AND nfc.data_compra = ?::date ");
            parametros.add(filtros.getDataCompra().trim());
        }

        // Adicionar ordenação
        sql.append("ORDER BY nfc.data_compra DESC, p.nome ASC");

        String sql1 = sql.toString();

        // Log para debug (opcional)
        System.out.println("SQL Executado: " + sql1);
        System.out.println("Parâmetros: " + parametros);

        // Executar a query
        if (parametros.isEmpty()) {
            return jdbcTemplate.query(sql1,
                    new BeanPropertyRowMapper<>(ObjetoReqRelatorioProdNFDTO.class));
        } else {
            return jdbcTemplate.query(sql1,
                    parametros.toArray(),
                    new BeanPropertyRowMapper<>(ObjetoReqRelatorioProdNFDTO.class));
        }

    }

    public List<ObjetoReqRelatorioProdAlertaEstoqueDTO> gerarRelatorioAlertaEstoque(ObjetoReqRelatorioProdAlertaEstoqueDTO filtros) {

        // Lista para armazenar os parâmetros da query
        List<Object> parametros = new ArrayList<>();

        //List<ObjetoReqRelatorioProdNFDTO> retorno = new ArrayList<ObjetoReqRelatorioProdNFDTO>();
        // Construir a query SQL dinamicamente
        StringBuilder sql = new StringBuilder();

        // Parte fixa do SQL
        sql.append("SELECT p.id AS codigoProduto, ");
        sql.append("       p.nome AS nomeProduto, ");
        sql.append("       p.valor_venda AS valorProduto, ");
        sql.append("       nip.quantidade AS quantidadeComprada, ");
        sql.append("       pj.id AS codigoFornecedor, ");
        sql.append("       pj.nome AS nomeForcenecedor, ");
        sql.append("       nfc.data_compra AS dataCompra, ");
        sql.append("       nfc.id AS nfCompra, ");
        sql.append("       p.qtde_estoque AS qtdeEstoque, ");
        sql.append("       p.qtde_alerta_estoque AS qtdAlertaEstoque, ");
        sql.append("       p.alerta_qtde_estoque AS alertaEstoque ");
        sql.append("FROM nota_fiscal_compra nfc ");
        sql.append("INNER JOIN nota_item_produto nip ON nip.nota_fiscal_compra_id = nfc.id ");
        sql.append("INNER JOIN produto p ON nip.produto_id = p.id ");
        sql.append("INNER JOIN pessoa_juridica pj ON pj.id = nfc.pessoa_id ");
        sql.append("WHERE 1 = 1 ");
        sql.append("AND p.alerta_qtde_estoque  = true ");
        sql.append("AND p.qtde_estoque <= p.qtde_alerta_estoque ");
        // Facilita adicionar condições com AND
        //retorno = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ObjetoReqRelatorioProdNFDTO.class));

        // Adicionar filtros dinamicamente

        // 1. Filtro por nome do produto (LIKE - busca parcial)
        if (filtros.getNomeProduto() != null && !filtros.getNomeProduto().trim().isEmpty()) {
            sql.append("AND UPPER(p.nome) LIKE UPPER(?) ");
            parametros.add("%" + filtros.getNomeProduto().trim() + "%");
        }

        // 2. Filtro por código do produto
        if (filtros.getCodigoProduto() != null && !filtros.getCodigoProduto().trim().isEmpty()) {
            try {
                // Tentar converter para número
                Integer codigoProduto = Integer.parseInt(filtros.getCodigoProduto().trim());
                sql.append("AND p.id = ? ");
                parametros.add(codigoProduto);
            } catch (NumberFormatException e) {
                // Se não for número, tratar como string
                sql.append("AND CAST(p.id AS TEXT) = ? ");
                parametros.add(filtros.getCodigoProduto().trim());
            }
        }

        // 3. Filtro por código NTA (Nota Fiscal)
        if (filtros.getCodigoNta() != null && !filtros.getCodigoNta().trim().isEmpty()) {
            try {
                Integer codigoNta = Integer.parseInt(filtros.getCodigoNta().trim());
                sql.append("AND nfc.id = ? ");
                parametros.add(codigoNta);
            } catch (NumberFormatException e) {
                sql.append("AND CAST(nfc.id AS TEXT) = ? ");
                parametros.add(filtros.getCodigoNta().trim());
            }
        }

        // 4. Filtro por código do fornecedor
        if (filtros.getCodigoFornecedor() != null && !filtros.getCodigoFornecedor().trim().isEmpty()) {
            try {
                Integer codigoFornecedor = Integer.parseInt(filtros.getCodigoFornecedor().trim());
                sql.append("AND pj.id = ? ");
                parametros.add(codigoFornecedor);
            } catch (NumberFormatException e) {
                sql.append("AND CAST(pj.id AS TEXT) = ? ");
                parametros.add(filtros.getCodigoFornecedor().trim());
            }
        }

        // 5. Filtro por nome do fornecedor (LIKE - busca parcial)
        if (filtros.getNomeForcenecedor() != null && !filtros.getNomeForcenecedor().trim().isEmpty()) {
            sql.append("AND UPPER(pj.nome) LIKE UPPER(?) ");
            parametros.add("%" + filtros.getNomeForcenecedor().trim() + "%");
        }

        // 6. Filtro por data inicial
        if (filtros.getDataInicial() != null && !filtros.getDataInicial().trim().isEmpty()) {
            sql.append("AND nfc.data_compra >= ?::date ");
            parametros.add(filtros.getDataInicial().trim());
        }

        // 7. Filtro por data final
        if (filtros.getDataFinal() != null && !filtros.getDataFinal().trim().isEmpty()) {
            sql.append("AND nfc.data_compra <= ?::date ");
            parametros.add(filtros.getDataFinal().trim());
        }

        // 8. Filtro por data de compra específica
        if (filtros.getDataCompra() != null && !filtros.getDataCompra().trim().isEmpty()) {
            sql.append("AND nfc.data_compra = ?::date ");
            parametros.add(filtros.getDataCompra().trim());
        }

        // Adicionar ordenação
        sql.append("ORDER BY nfc.data_compra DESC, p.nome ASC");

        String sql1 = sql.toString();

        // Log para debug (opcional)
        System.out.println("SQL Executado: " + sql1);
        System.out.println("Parâmetros: " + parametros);

        // Executar a query
        if (parametros.isEmpty()) {
            return jdbcTemplate.query(sql1,
                    new BeanPropertyRowMapper<>(ObjetoReqRelatorioProdAlertaEstoqueDTO.class));
        } else {
            return jdbcTemplate.query(sql1,
                    parametros.toArray(),
                    new BeanPropertyRowMapper<>(ObjetoReqRelatorioProdAlertaEstoqueDTO.class));
        }

    }


    /**
     * Title: Relatório de Status de Vendas
     * Este relatório permite saber as compras e seus status
     * Not Fiscal  de Compra/ Venda
     * @param ->gerarRelatorioSatusCompraVendas
     * @return ->BeanPropertyRowMapper<>(ObjetoRelatorioSatusCompraVendasDTO.class)
     */
    public List<ObjetoRelatorioSatusCompraVendasDTO> gerarRelatorioSatusCompraVendas(ObjetoRelatorioSatusCompraVendasDTO filtros) {

        // Lista para armazenar os parâmetros da query
        List<Object> parametros = new ArrayList<>();

        //List<ObjetoReqRelatorioProdNFDTO> retorno = new ArrayList<ObjetoReqRelatorioProdNFDTO>();
        // Construir a query SQL dinamicamente
        StringBuilder sql = new StringBuilder();

        // Parte fixa do SQL
        sql.append("select vclv.id numVenda, ");
        sql.append("   vclv.nota_fiscal_venda_id NFVenda, ");
        sql.append("   p.id  codProduto, ");
        sql.append("   p.nome nomeProduto, ");
        sql.append("   p.valor_venda valorProduto, ");
        sql.append("   p.empresa_id fornProduto, ");
        sql.append("   ivl.quantidade  quantidadeVenda, ");
        sql.append("   pf.id codigoCliente, ");
        sql.append("   pf.nome nomeCliente, ");
        sql.append("   pf.email emailCliente, ");
        sql.append("   pf.telefone foneCliente, ");
        sql.append("   pf.cpf cpfCliente,  ");
        sql.append("   ivl.empresa_id , ");
        sql.append("   vclv.data_entrega dataEntrega, ");
        sql.append("   vclv.data_venda dataVenda, ");
        sql.append("   vclv.excluido, ");
        sql.append("   vclv.status_venda_loja_virtual sstatusVenda ");
        sql.append("from vd_cp_loja_virt vclv ");
        sql.append("inner join item_venda_loja ivl on ivl.venda_compraloja_virtual_id  = vclv.id ");
        sql.append("inner join produto p on ivl.produto_id  = p.id ");
        sql.append("inner join pessoa_fisica pf on pf.id = vclv.pessoa_id ");
        sql.append("where 1 =1  ");
        // Facilita adicionar condições com AND
        //retorno = jdbcTemplate.query(sql, new BeanPropertyRowMapper(ObjetoReqRelatorioProdNFDTO.class));

        // Adicionar filtros dinamicamente

        // 1. Filtro por nome do produto (LIKE - busca parcial)
        if (filtros.getNomeProduto_p() != null && !filtros.getNomeProduto_p().trim().isEmpty()) {
            sql.append("AND UPPER(p.nome) LIKE UPPER(?) ");
            parametros.add("%" + filtros.getNomeProduto_p().trim() + "%");
        }

        // 2. Filtro por código do produto
        if (filtros.getCodProduto_p() != null && !filtros.getCodProduto_p().trim().isEmpty()) {
            try {
                // Tentar converter para número
                Integer codigoProduto = Integer.parseInt(filtros.getCodProduto_p().trim());
                sql.append("AND p.id = ? ");
                parametros.add(codigoProduto);
            } catch (NumberFormatException e) {
                // Se não for número, tratar como string
                sql.append("AND CAST(p.id AS TEXT) = ? ");
                parametros.add(filtros.getCodProduto_p().trim());
            }
        }

        // 3. Filtro por código Venda (Codigo Venda)
        if (filtros.getNumVenda_p() != null && !filtros.getNumVenda_p().trim().isEmpty()) {
            try {
                Integer numVenda = Integer.parseInt(filtros.getNumVenda_p().trim());
                sql.append("AND vclv.id = ? ");
                parametros.add(numVenda);
            } catch (NumberFormatException e) {
                sql.append("AND CAST(vclv.id AS TEXT) = ? ");
                parametros.add(filtros.getNumVenda_p().trim());
            }
        }

        // 4. Filtro por Status Venda
        if (filtros.getStatusVenda_p() != null && !filtros.getStatusVenda_p().trim().isEmpty()) {
            sql.append("AND UPPER(vclv.status_venda_loja_virtual) LIKE UPPER(?) ");
            parametros.add("%" + filtros.getStatusVenda_p().trim() + "%");
        }

        // 5. Filtro por código NTA (Nota Fiscal Venda)
        if (filtros.getNFVenda_p() != null && !filtros.getNFVenda_p().trim().isEmpty()) {
            try {
                Integer NFVenda = Integer.parseInt(filtros.getNFVenda_p().trim());
                sql.append("AND vclv.nota_fiscal_venda_id = ? ");
                parametros.add(NFVenda);
            } catch (NumberFormatException e) {
                sql.append("AND CAST(vclv.nota_fiscal_venda_id AS TEXT) = ? ");
                parametros.add(filtros.getNFVenda_p().trim());
            }
        }

        // 6. Filtro por código PF (PF)
        if (filtros.getCodigoCliente_p() != null && !filtros.getCodigoCliente_p().trim().isEmpty()) {
            try {
                Integer codigoCliente = Integer.parseInt(filtros.getCodigoCliente_p().trim());
                sql.append("AND pf.id = ? ");
                parametros.add(codigoCliente);
            } catch (NumberFormatException e) {
                sql.append("AND CAST(pf.id AS TEXT) = ? ");
                parametros.add(filtros.getCodigoCliente_p().trim());
            }
        }

        // 7. Filtro por nome do Cliente (LIKE - busca parcial)
        if (filtros.getNomeCliente_p() != null && !filtros.getNomeCliente_p().trim().isEmpty()) {
            sql.append("AND UPPER(pf.nome) LIKE UPPER(?) ");
            parametros.add("%" + filtros.getNomeCliente_p().trim() + "%");
        }

        // 8. Filtro por data inicial Entrega
        if (filtros.getDataEntInicial_p() != null && !filtros.getDataEntInicial_p().trim().isEmpty()) {
            sql.append("AND vclv.data_entrega >= ?::date ");
            parametros.add(filtros.getDataEntInicial_p().trim());
        }

        // 9. Filtro por data final Entrega
        if (filtros.getDataEntFinal_p() != null && !filtros.getDataEntFinal_p().trim().isEmpty()) {
            sql.append("AND vclv.data_entrega <= ?::date ");
            parametros.add(filtros.getDataEntFinal_p().trim());
        }

        // 10. Filtro por data inicial Venda
        if (filtros.getDataVendaIncial_p() != null && !filtros.getDataVendaIncial_p().trim().isEmpty()) {
            sql.append("AND vclv.data_venda >= ?::date ");
            parametros.add(filtros.getDataVendaIncial_p().trim());
        }

        // 11. Filtro por data final Venda
        if (filtros.getDataVendaFinal_p() != null && !filtros.getDataVendaFinal_p().trim().isEmpty()) {
            sql.append("AND vclv.data_venda <= ?::date ");
            parametros.add(filtros.getDataVendaFinal_p().trim());
        }

        // 12. Filtro por código Fornecedor Produto
        if (filtros.getFornProduto_p() != null && !filtros.getFornProduto_p().trim().isEmpty()) {
            try {
                Integer fornProduto = Integer.parseInt(filtros.getFornProduto_p().trim());
                sql.append("AND p.empresa_id = ? ");
                parametros.add(fornProduto);
            } catch (NumberFormatException e) {
                sql.append("AND CAST(p.empresa_id AS TEXT) = ? ");
                parametros.add(filtros.getFornProduto_p().trim());
            }
        }


        // 13. Filtro por campo excluído (boolean)
        if (filtros.getExcluido_p() != null && !filtros.getExcluido_p().trim().isEmpty()) {
            String excluidoStr = filtros.getExcluido_p().trim().toLowerCase();
            if (excluidoStr.equals("true") || excluidoStr.equals("false") ||
                    excluidoStr.equals("1") || excluidoStr.equals("0")) {

                Boolean valorExcluido;
                if (excluidoStr.equals("true") || excluidoStr.equals("1")) {
                    valorExcluido = true;
                } else {
                    valorExcluido = false;
                }

                sql.append("AND vclv.excluido = ? ");
                parametros.add(valorExcluido);
            }
        }

        // 14. Filtro CPF do Cliente (LIKE - busca parcial)
        if (filtros.getCpfCliente_p() != null && !filtros.getCpfCliente_p().trim().isEmpty()) {
            sql.append("AND UPPER(pf.cpf) LIKE UPPER(?) ");
            parametros.add("%" + filtros.getCpfCliente_p().trim() + "%");
        }

        // 15. Filtro E-mail do Cliente (LIKE - busca parcial)
        if (filtros.getEmailCliente_p() != null && !filtros.getEmailCliente_p().trim().isEmpty()) {
            sql.append("AND UPPER(pf.email) LIKE UPPER(?) ");
            parametros.add("%" + filtros.getEmailCliente_p().trim() + "%");
        }

        // Adicionar ordenação
        sql.append("ORDER BY vclv.data_venda DESC, pf.nome ASC");

        String sql1 = sql.toString();

        // Log para debug (opcional)
        System.out.println("SQL Executado: " + sql1);
        System.out.println("Parâmetros: " + parametros);

        // Executar a query
        if (parametros.isEmpty()) {
            return jdbcTemplate.query(sql1,
                    new BeanPropertyRowMapper<>(ObjetoRelatorioSatusCompraVendasDTO.class));
        } else {
            return jdbcTemplate.query(sql1,
                    parametros.toArray(),
                    new BeanPropertyRowMapper<>(ObjetoRelatorioSatusCompraVendasDTO.class));
        }

    }
}
