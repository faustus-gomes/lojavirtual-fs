package jdev.lojavirtual_fs.lojavirtual_fs.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jdev.lojavirtual_fs.lojavirtual_fs.enums.StatusContaPagar;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "conta_pagar")
@SequenceGenerator(name = "seq_conta_pagar", sequenceName = "seq_conta_pagar", allocationSize = 1, initialValue = 1)
public class ContaPagar implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_conta_pagar")
    private Long id;

    //@NotEmpty(message = "Informe a descrição da Conta Pagar")
    @NotNull(message = "Informe a descrição da Conta Pagar")
    @Column(nullable = false)
    private String descricao;

    //@NotEmpty(message = "Informe o status da Conta Pagar")
    @NotNull(message = "Informe o status da Conta Pagar")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusContaPagar status;

    //@NotEmpty(message = "Informe a data de vencimento da Conta Pagar")
    @NotNull(message = "Informe a data de vencimento da Conta Pagar")
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dtVencimento;

    @Temporal(TemporalType.DATE)
    private Date dtPagamento;

    @NotNull(message = "Informe o valor total da Conta Pagar")
    @Column(nullable = false)
    private BigDecimal valorTotal;
    private BigDecimal valorDesconto;

    @ManyToOne(targetEntity = PessoaFisica.class)
    @JoinColumn(name = "pessoa_id", nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "pessoa_fk"))
    private PessoaFisica pessoa;

    @ManyToOne(targetEntity = PessoaJuridica.class)
    @JoinColumn(name = "pessoa_forn_id", nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "pessoa_forn_fk"))
    private PessoaJuridica pessoa_fornecedor;

    @ManyToOne(targetEntity = PessoaJuridica.class)
    @JoinColumn(name = "empresa_id", nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "empresa_fk"))
    private PessoaJuridica empresa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public StatusContaPagar getStatus() {
        return status;
    }

    public void setStatus(StatusContaPagar status) {
        this.status = status;
    }

    public PessoaJuridica getPessoa_fornecedor() {
        return pessoa_fornecedor;
    }

    public void setPessoa_fornecedor(PessoaJuridica pessoa_fornecedor) {
        this.pessoa_fornecedor = pessoa_fornecedor;
    }

    public Date getDtVencimento() {
        return dtVencimento;
    }

    public void setDtVencimento(Date dtVencimento) {
        this.dtVencimento = dtVencimento;
    }

    public Date getDtPagamento() {
        return dtPagamento;
    }

    public void setDtPagamento(Date dtPagamento) {
        this.dtPagamento = dtPagamento;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public PessoaFisica getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaFisica pessoa) {
        this.pessoa = pessoa;
    }

    public Pessoa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(PessoaJuridica empresa) {
        this.empresa = empresa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContaPagar that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
