package jdev.lojavirtual_fs.lojavirtual_fs.dto;

import java.io.Serializable;

public class ObjetoReqRelatorioProdAlertaEstoqueDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // Campos de filtro (ENTRADA)
    private String nomeProduto;
    private String dataInicial;
    private String dataFinal;
    private String codigoNta;
    private String codigoProduto;
    private String codigoFornecedor;
    private String qtde_estoque;
    private String qtde_alerta_estoque;
    private String alerta_qtde_estoque;

    // Campos de resultado (SAÍDA)
    private String nomeProdutoResult; // Renomeado para evitar conflito
    private String valorProduto;
    private String quantidadeComprada;
    private String nomeForcenecedor;
    private String dataCompra;
    private String nfCompra;
    private String qtdeEstoque;
    private String qtdAlertaEstoque;
    private String alertaEstoque;

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getCodigoNta() {
        return codigoNta;
    }

    public void setCodigoNta(String codigoNta) {
        this.codigoNta = codigoNta;
    }

    public String getCodigoProduto() {
        return codigoProduto;
    }

    public void setCodigoProduto(String codigoProduto) {
        this.codigoProduto = codigoProduto;
    }

    public String getValorProduto() {
        return valorProduto;
    }

    public void setValorProduto(String valorProduto) {
        this.valorProduto = valorProduto;
    }

    public String getQuantidadeComprada() {
        return quantidadeComprada;
    }

    public void setQuantidadeComprada(String quantidadeComprada) {
        this.quantidadeComprada = quantidadeComprada;
    }

    public String getCodigoFornecedor() {
        return codigoFornecedor;
    }

    public void setCodigoFornecedor(String codigoFornecedor) {
        this.codigoFornecedor = codigoFornecedor;
    }

    public String getNomeForcenecedor() {
        return nomeForcenecedor;
    }

    public void setNomeForcenecedor(String nomeForcenecedor) {
        this.nomeForcenecedor = nomeForcenecedor;
    }

    public String getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(String dataCompra) {
        this.dataCompra = dataCompra;
    }

    public String getNomeProdutoResult() {
        return nomeProdutoResult;
    }

    public void setNomeProdutoResult(String nomeProdutoResult) {
        this.nomeProdutoResult = nomeProdutoResult;
    }

    public String getNfCompra() {
        return nfCompra;
    }

    public void setNfCompra(String nfCompra) {
        this.nfCompra = nfCompra;
    }

    public String getQtde_estoque() {
        return qtde_estoque;
    }

    public void setQtde_estoque(String qtde_estoque) {
        this.qtde_estoque = qtde_estoque;
    }

    public String getQtde_alerta_estoque() {
        return qtde_alerta_estoque;
    }

    public void setQtde_alerta_estoque(String qtde_alerta_estoque) {
        this.qtde_alerta_estoque = qtde_alerta_estoque;
    }

    public String getAlertaEstoque() {
        return alertaEstoque;
    }

    public void setAlertaEstoque(String alertaEstoque) {
        this.alertaEstoque = alertaEstoque;
    }

    public String getQtdeEstoque() {
        return qtdeEstoque;
    }

    public void setQtdeEstoque(String qtdeEstoque) {
        this.qtdeEstoque = qtdeEstoque;
    }

    public String getQtdAlertaEstoque() {
        return qtdAlertaEstoque;
    }

    public void setQtdAlertaEstoque(String qtdAlertaEstoque) {
        this.qtdAlertaEstoque = qtdAlertaEstoque;
    }

    public String getAlerta_qtde_estoque() {
        return alerta_qtde_estoque;
    }

    public void setAlerta_qtde_estoque(String alerta_qtde_estoque) {
        this.alerta_qtde_estoque = alerta_qtde_estoque;
    }
}
