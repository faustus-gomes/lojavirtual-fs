package jdev.lojavirtual_fs.lojavirtual_fs.service;

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
}
