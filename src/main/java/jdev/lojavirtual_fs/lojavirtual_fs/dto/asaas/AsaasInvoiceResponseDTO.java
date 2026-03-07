package jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AsaasInvoiceResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private String id;

    @JsonProperty("status")
    private String status; // SCHEDULED, AUTHORIZED, CANCELLED, etc.

    @JsonProperty("customer")
    private String customerId;

    @JsonProperty("payment")
    private String paymentId;

    @JsonProperty("installment")
    private String installmentId;

    @JsonProperty("serviceDescription")
    private String serviceDescription;

    @JsonProperty("externalReference")
    private String externalReference;

    @JsonProperty("value")
    private BigDecimal value;

    @JsonProperty("deductions")
    private BigDecimal deductions;

    @JsonProperty("effectiveDate")
    private LocalDate effectiveDate;

    @JsonProperty("municipalServiceCode")
    private String municipalServiceCode;

    @JsonProperty("municipalServiceName")
    private String municipalServiceName;

    @JsonProperty("updatePayment")
    private Boolean updatePayment;

    @JsonProperty("taxes")
    private Taxes taxes;

    // Campos específicos da NFS-e quando autorizada
    @JsonProperty("nfseNumber")
    private String nfseNumber;

    @JsonProperty("nfseVerificationCode")
    private String nfseVerificationCode;

    @JsonProperty("pdfUrl")
    private String pdfUrl;

    @JsonProperty("xmlUrl")
    private String xmlUrl;

    @JsonProperty("createdDate")
    private LocalDate createdDate;

    @JsonProperty("authorizedDate")
    private LocalDate authorizedDate;

    @JsonProperty("cancelledDate")
    private LocalDate cancelledDate;

    @Data
    public static class Taxes {
        @JsonProperty("retainIss")
        private Boolean retainIss;

        @JsonProperty("iss")
        private BigDecimal iss;

        @JsonProperty("pis")
        private BigDecimal pis;

        @JsonProperty("cofins")
        private BigDecimal cofins;

        @JsonProperty("csll")
        private BigDecimal csll;

        @JsonProperty("inss")
        private BigDecimal inss;

        @JsonProperty("ir")
        private BigDecimal ir;

        @JsonProperty("pisCofinsRetentionType")
        private String pisCofinsRetentionType;

        @JsonProperty("pisCofinsTaxStatus")
        private String pisCofinsTaxStatus;
    }
}
