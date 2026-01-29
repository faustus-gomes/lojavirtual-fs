package jdev.lojavirtual_fs.lojavirtual_fs.dto;

import java.io.Serializable;

public class ObjetoRelatorioSatusCompraVendasDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    // Campos de filtro (ENTRADA)
    private String statusVenda_p;
    private String dataEntInicial_p;
    private String dataEntFinal_p;
    private String dataVendaIncial_p;
    private String dataVendaFinal_p;
    private String numVenda_p;
    private String NFVenda_p;
    private String codProduto_p;
    private String nomeProduto_p;
    private String valorProduto1_p;
    private String valorProduto2_p;
    private String fornProduto_p;
    private String quantidadeVenda_p;
    private String codigoCliente_p;
    private String nomeCliente_p;
    private String emailCliente_p;
    private String cpfCliente_p;
    private String codEmpresa_p;
    private String excluido_p;

    // Campos de resultado (SAÍDA)
    private String numVenda; // Renomeado para evitar conflito
    private String NFVenda;
    private String  codProduto;
    private String nomeProduto;
    private String valorProduto;
    private String fornProduto;
    private String  quantidadeVenda;
    private String codigoCliente;
    private String nomeCliente;
    private String emailCliente;
    private String foneCliente;
    private String cpfCliente;
    private String empresa_id;
    private String dataEntrega;
    private String dataVenda;
    private Boolean excluido;
    private String sstatusVenda;

    public String getStatusVenda_p() {
        return statusVenda_p;
    }

    public void setStatusVenda_p(String statusVenda_p) {
        this.statusVenda_p = statusVenda_p;
    }

    public String getDataEntInicial_p() {
        return dataEntInicial_p;
    }

    public void setDataEntInicial_p(String dataEntInicial_p) {
        this.dataEntInicial_p = dataEntInicial_p;
    }

    public String getDataVendaFinal_p() {
        return dataVendaFinal_p;
    }

    public void setDataVendaFinal_p(String dataVendaFinal_p) {
        this.dataVendaFinal_p = dataVendaFinal_p;
    }

    public String getNumVenda_p() {
        return numVenda_p;
    }

    public void setNumVenda_p(String numVenda_p) {
        this.numVenda_p = numVenda_p;
    }

    public String getNFVenda_p() {
        return NFVenda_p;
    }

    public void setNFVenda_p(String NFVenda_p) {
        this.NFVenda_p = NFVenda_p;
    }

    public String getCodProduto_p() {
        return codProduto_p;
    }

    public void setCodProduto_p(String codProduto_p) {
        this.codProduto_p = codProduto_p;
    }

    public String getNomeProduto_p() {
        return nomeProduto_p;
    }

    public void setNomeProduto_p(String nomeProduto_p) {
        this.nomeProduto_p = nomeProduto_p;
    }

    public String getValorProduto1_p() {
        return valorProduto1_p;
    }

    public void setValorProduto1_p(String valorProduto1_p) {
        this.valorProduto1_p = valorProduto1_p;
    }

    public String getValorProduto2_p() {
        return valorProduto2_p;
    }

    public void setValorProduto2_p(String valorProduto2_p) {
        this.valorProduto2_p = valorProduto2_p;
    }

    public String getFornProduto_p() {
        return fornProduto_p;
    }

    public void setFornProduto_p(String fornProduto_p) {
        this.fornProduto_p = fornProduto_p;
    }

    public String getQuantidadeVenda_p() {
        return quantidadeVenda_p;
    }

    public void setQuantidadeVenda_p(String quantidadeVenda_p) {
        this.quantidadeVenda_p = quantidadeVenda_p;
    }

    public String getCodigoCliente_p() {
        return codigoCliente_p;
    }

    public void setCodigoCliente_p(String codigoCliente_p) {
        this.codigoCliente_p = codigoCliente_p;
    }

    public String getNomeCliente_p() {
        return nomeCliente_p;
    }

    public void setNomeCliente_p(String nomeCliente_p) {
        this.nomeCliente_p = nomeCliente_p;
    }

    public String getEmailCliente_p() {
        return emailCliente_p;
    }

    public void setEmailCliente_p(String emailCliente_p) {
        this.emailCliente_p = emailCliente_p;
    }

    public String getCpfCliente_p() {
        return cpfCliente_p;
    }

    public void setCpfCliente_p(String cpfCliente_p) {
        this.cpfCliente_p = cpfCliente_p;
    }

    public String getCodEmpresa_p() {
        return codEmpresa_p;
    }

    public void setCodEmpresa_p(String codEmpresa_p) {
        this.codEmpresa_p = codEmpresa_p;
    }

    public String getExcluido_p() {
        return excluido_p;
    }

    public void setExcluido_p(String excluido_p) {
        this.excluido_p = excluido_p;
    }

    public String getNumVenda() {
        return numVenda;
    }

    public void setNumVenda(String numVenda) {
        this.numVenda = numVenda;
    }

    public String getNFVenda() {
        return NFVenda;
    }

    public void setNFVenda(String NFVenda) {
        this.NFVenda = NFVenda;
    }

    public String getCodProduto() {
        return codProduto;
    }

    public void setCodProduto(String codProduto) {
        this.codProduto = codProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getValorProduto() {
        return valorProduto;
    }

    public void setValorProduto(String valorProduto) {
        this.valorProduto = valorProduto;
    }

    public String getFornProduto() {
        return fornProduto;
    }

    public void setFornProduto(String fornProduto) {
        this.fornProduto = fornProduto;
    }

    public String getQuantidadeVenda() {
        return quantidadeVenda;
    }

    public void setQuantidadeVenda(String quantidadeVenda) {
        this.quantidadeVenda = quantidadeVenda;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public String getFoneCliente() {
        return foneCliente;
    }

    public void setFoneCliente(String foneCliente) {
        this.foneCliente = foneCliente;
    }

    public String getCpfCliente() {
        return cpfCliente;
    }

    public void setCpfCliente(String cpfCliente) {
        this.cpfCliente = cpfCliente;
    }

    public String getEmpresa_id() {
        return empresa_id;
    }

    public void setEmpresa_id(String empresa_id) {
        this.empresa_id = empresa_id;
    }

    public String getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(String dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public String getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(String dataVenda) {
        this.dataVenda = dataVenda;
    }

    public Boolean getExcluido() {
        return excluido;
    }

    public void setExcluido(Boolean excluido) {
        this.excluido = excluido;
    }

    public String getSstatusVenda() {
        return sstatusVenda;
    }

    public void setSstatusVenda(String sstatusVenda) {
        this.sstatusVenda = sstatusVenda;
    }

    public String getDataEntFinal_p() {
        return dataEntFinal_p;
    }

    public void setDataEntFinal_p(String dataEntFinal_p) {
        this.dataEntFinal_p = dataEntFinal_p;
    }

    public String getDataVendaIncial_p() {
        return dataVendaIncial_p;
    }

    public void setDataVendaIncial_p(String dataVendaIncial_p) {
        this.dataVendaIncial_p = dataVendaIncial_p;
    }
}
