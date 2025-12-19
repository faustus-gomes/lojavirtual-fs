package jdev.lojavirtual_fs.lojavirtual_fs.dto;

import jdev.lojavirtual_fs.lojavirtual_fs.model.Endereco;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Pessoa;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VendaCompraLojaVirtualDTO {
    private Long id;
    private Pessoa pessoa;
    private Endereco cobranca;
    private Endereco entrega;
    private BigDecimal valorTotal;

    private BigDecimal valorDesc;
    private BigDecimal valorfrete;

    private List<ItemVendaDTO> itemVendaLoja = new ArrayList<ItemVendaDTO>();

    public List<ItemVendaDTO> getItemVendaLoja() {
        return itemVendaLoja;
    }

    public void setItemVendaLoja(List<ItemVendaDTO> itemVendaLoja) {
        this.itemVendaLoja = itemVendaLoja;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorfrete() {
        return valorfrete;
    }

    public void setValorfrete(BigDecimal valorfrete) {
        this.valorfrete = valorfrete;
    }

    public BigDecimal getValorDesc() {
        return valorDesc;
    }

    public void setValorDesc(BigDecimal valorDesc) {
        this.valorDesc = valorDesc;
    }

    public Endereco getCobranca() {
        return cobranca;
    }

    public void setCobranca(Endereco cobranca) {
        this.cobranca = cobranca;
    }

    public Endereco getEntrega() {
        return entrega;
    }

    public void setEntrega(Endereco entrega) {
        this.entrega = entrega;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
