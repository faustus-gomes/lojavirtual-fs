package jdev.lojavirtual_fs.lojavirtual_fs.model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "produto")
@SequenceGenerator(name = "seq_produto", sequenceName = "seq_produto", allocationSize = 1, initialValue = 1)
public class Produto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_produto")
    private Long id;

    @NotNull(message = "Tipo unidade deve ser informado")
    @Column(nullable = false)
    private String tipoUnidade;

    @Size(min = 10, message = "O nome do produto tem que ter no mínimo 10 letras.")
    @NotNull(message = "Nome do produto deve ser informado")
    @Column(nullable = false)
    private String nome;
    @Column(nullable = false)
    private Boolean ativo = Boolean.TRUE;

    @NotNull(message = "Descricão do produto deve ser informado")
    @Column(columnDefinition = "text", length = 2000, nullable = false)
    private String descricao;
    /** nota_item_produto - associar*/

    @NotNull(message = "Peso do produto deve ser informado")
    @Column(nullable = false)
    private Double peso; /*1000.55 G*/

    @NotNull(message = "Largura do produto deve ser informado")
    @Column(nullable = false)
    private Double largura;

    @NotNull(message = "Altura do produto deve ser informado")
    @Column(nullable = false)
    private Double altura;

    @NotNull(message = "Profundidade do produto deve ser informado")
    @Column(nullable = false)
    private Double profundidade;

    @NotNull(message = "Valor de venda do produto deve ser informado")
    @Column(nullable = false)
    private BigDecimal valorVenda = BigDecimal.ZERO;
    @Column(nullable = false)
    private Integer qtdeEstoque = 0;
    private Integer qtdeAlertaEstoque = 0;
    private String linkYoutube;
    private Boolean alertaQtdeEstoque = Boolean.FALSE;
    private Integer qtdeClique = 0;

    @NotNull(message = "Empresa responsável do produto deve ser informado")
    @ManyToOne(targetEntity = Pessoa.class)
    @JoinColumn(name = "empresa_id", nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "empresa_fk"))
    private PessoaJuridica empresa;

    @NotNull(message = "Categoria do produto deve ser informado")
    @ManyToOne(targetEntity = CategoriaProduto.class)
    @JoinColumn(name = "categoria_produto_id", nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "categoria_produto_id_fk"))
    private CategoriaProduto categoriaProduto = new CategoriaProduto();


    @NotNull(message = "Marca do produto deve ser informado")
    @ManyToOne(targetEntity = MarcaProduto.class)
    @JoinColumn(name = "marca_produto_id", nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "marca_produto_id_fk"))
    private MarcaProduto marcaProduto = new MarcaProduto();

    @NotNull(message = "Nota ìtem do produto deve ser informado")
    @ManyToOne(targetEntity = NotaItemProduto.class)
    @JoinColumn(name = "nota_item_produto_id", nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "nota_item_produto_id_fk"))
    private NotaItemProduto notaItemProduto = new NotaItemProduto();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoUnidade() {
        return tipoUnidade;
    }

    public void setTipoUnidade(String tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getLargura() {
        return largura;
    }

    public void setLargura(Double largura) {
        this.largura = largura;
    }

    public Double getAltura() {
        return altura;
    }

    public void setAltura(Double altura) {
        this.altura = altura;
    }

    public Double getProfundidade() {
        return profundidade;
    }

    public void setProfundidade(Double profundidade) {
        this.profundidade = profundidade;
    }

    public BigDecimal getValorVenda() {
        return valorVenda;
    }

    public void setValorVenda(BigDecimal valorVenda) {
        this.valorVenda = valorVenda;
    }

    public Integer getQtdeEstoque() {
        return qtdeEstoque;
    }

    public void setQtdeEstoque(Integer qtdeEstoque) {
        this.qtdeEstoque = qtdeEstoque;
    }

    public Integer getQtdeAlertaEstoque() {
        return qtdeAlertaEstoque;
    }

    public void setQtdeAlertaEstoque(Integer qtdeAlertaEstoque) {
        this.qtdeAlertaEstoque = qtdeAlertaEstoque;
    }

    public String getLinkYoutube() {
        return linkYoutube;
    }

    public void setLinkYoutube(String linkYoutube) {
        this.linkYoutube = linkYoutube;
    }

    public Boolean getAlertaQtdeEstoque() {
        return alertaQtdeEstoque;
    }

    public void setAlertaQtdeEstoque(Boolean alertaQtdeEstoque) {
        this.alertaQtdeEstoque = alertaQtdeEstoque;
    }

    public Integer getQtdeClique() {
        return qtdeClique;
    }

    public void setQtdeClique(Integer qtdeClique) {
        this.qtdeClique = qtdeClique;
    }

    public PessoaJuridica getEmpresa() {
        return empresa;
    }

    public void setEmpresa(PessoaJuridica empresa) {
        this.empresa = empresa;
    }

    public CategoriaProduto getCategoriaProduto() {
        return categoriaProduto;
    }

    public void setCategoriaProduto(CategoriaProduto categoriaProduto) {
        this.categoriaProduto = categoriaProduto;
    }

    public MarcaProduto getMarcaProduto() {
        return marcaProduto;
    }

    public void setMarcaProduto(MarcaProduto marcaProduto) {
        this.marcaProduto = marcaProduto;
    }

    public NotaItemProduto getNotaItemProduto() {
        return notaItemProduto;
    }

    public void setNotaItemProduto(NotaItemProduto notaItemProduto) {
        this.notaItemProduto = notaItemProduto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produto produto)) return false;
        return Objects.equals(getId(), produto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
