package jdev.lojavirtual_fs.lojavirtual_fs.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GeneratorType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "boleto_juno")
@SequenceGenerator(name = "seq_boleto_juno", sequenceName = "seq_boleto_juno", allocationSize = 1, initialValue = 1)
public class BoletoJuno implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_boleto_juno")
    private Long id;

    /*Código de controle do boleto*/
    private String code = "";
    /*Imprime boleto completo com todas parcelas*/
    private String link = "";
    /*Mostra tela de checkout da juno com os boletos, pix e cartão pagos ou vencidos*/
    private String checkoutUrl = "";

    private boolean quitado = false;
    private String dataVencimento = "";
    private BigDecimal valor = BigDecimal.ZERO;
    private Integer recorrencia = 0;

    /*Id de controle de boleto para poder cancelar pela API*/
    private String idChrBoleto = "";
    /*Link da parcela do boleto*/
    private String installmentLink = "";
    private String IdPix = "";
    @Column(columnDefinition = "text")
    private String payloadInBase64 = "";
    @Column(columnDefinition = "text")
    private String imageInBase64 = "";
    private String chargeICartao = "";

    @ManyToOne
    @JoinColumn(name = "venda_compra_loja_virt_id", nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "venda_compra_loja_virt_fk"))
    private VendaCompraLojaVirtual vendaCompraLojaVirtual;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public boolean isQuitado() {
        return quitado;
    }

    public void setQuitado(boolean quitado) {
        this.quitado = quitado;
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getRecorrencia() {
        return recorrencia;
    }

    public void setRecorrencia(Integer recorrencia) {
        this.recorrencia = recorrencia;
    }

    public String getIdChrBoleto() {
        return idChrBoleto;
    }

    public void setIdChrBoleto(String idChrBoleto) {
        this.idChrBoleto = idChrBoleto;
    }

    public String getInstallmentLink() {
        return installmentLink;
    }

    public void setInstallmentLink(String installmentLink) {
        this.installmentLink = installmentLink;
    }

    public String getIdPix() {
        return IdPix;
    }

    public void setIdPix(String idPix) {
        IdPix = idPix;
    }

    public String getPayloadInBase64() {
        return payloadInBase64;
    }

    public void setPayloadInBase64(String payloadInBase64) {
        this.payloadInBase64 = payloadInBase64;
    }

    public String getImageInBase64() {
        return imageInBase64;
    }

    public void setImageInBase64(String imageInBase64) {
        this.imageInBase64 = imageInBase64;
    }

    public String getChargeICartao() {
        return chargeICartao;
    }

    public void setChargeICartao(String chargeICartao) {
        this.chargeICartao = chargeICartao;
    }

    public VendaCompraLojaVirtual getVendaCompraLojaVirtual() {
        return vendaCompraLojaVirtual;
    }

    public void setVendaCompraLojaVirtual(VendaCompraLojaVirtual vendaCompraLojaVirtual) {
        this.vendaCompraLojaVirtual = vendaCompraLojaVirtual;
    }

    public PessoaJuridica getEmpresa() {
        return empresa;
    }

    public void setEmpresa(PessoaJuridica empresa) {
        this.empresa = empresa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoletoJuno that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
