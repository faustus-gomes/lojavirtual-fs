package jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AsaasInvoiceRequestDTO implements Serializable {
    /** PARTE 1- AGENDAR NOTA FISCAL
     Link parte 13.9 - https://docs.asaas.com/reference/agendar-nota-fiscal
     * */

    private static final long serialVersionUID = 1L; // Controle de versão

    @JsonProperty("payment")
    private   String paymentId; //Id da cobrança (aar nota vinculada)

    @JsonProperty("installment")
    private String installmentId; // Id do parcelamento

    @JsonProperty("customer")
    private String customerId; //Id do cliente (para nota avulsa)

    @JsonProperty("serviceDescription")
    private String serviceDescription; //Descrição detalhada do serviço

    @JsonProperty("observations")
    private String observations; // Observações adcionais

    @JsonProperty("externalReference")
    private String externalReference; // Identificador da nota fiscal no meu sistema

    @JsonProperty("value")
    private String value; // valor total da nota

    @JsonProperty("deductions")
    private BigDecimal deductions = BigDecimal.ZERO; // Deduções

    @JsonProperty("effectiveDate")
    private LocalDate effectiveDate; // Data da emissão (YYYY-MM-DD)

    @JsonProperty("municipalServiceCode")
    private String municipalServiceCode; //Código do Serviço (Se não houver ID)

    @JsonProperty("municipalServiceName")
    private String municipalServiceName; //Nome do Serviço

    @JsonProperty("updatePayment")
    private boolean updatePayment; //Atualizar o valor da cobrança com os impostos da nota já descontados.

    @JsonProperty("taxes")
    private Taxes taxes; //Impostos

    @Data
    public static class Taxes {
        /*** *****************************************************
         Importante sobre os campos de serviço :

         -Se a prefeitura tem lista de serviços → use municipalServiceId
         -Se a prefeitura NÃO tem lista → use municipalServiceCode
         -NUNCA envie os dois juntos!
         ********************************************************* */


        @JsonProperty("nbsCode")
        private String nbsCode;  //Código NBS (Nomenclatura Brasileira de Serviços)

        @JsonProperty("taxSituationCode")
        private String taxSituationCode; //Código de situação tributária

        @JsonProperty("taxClassificationCode")
        private String taxClassificationCode; //Código de classificação tributária

        @JsonProperty("operationIndicatorCode")
        private String operationIndicatorCode; //Código do indicador de operação

        @JsonProperty("retainIss")
        private Boolean retainIss = false;  // ISS retido na fonte

        @JsonProperty("iss")
        private BigDecimal iss;              // Alíquota ISS

        @JsonProperty("pisCofinsRetentionType")
        private String pisCofinsRetentionType; // Tipo de retenção do PIS/COFINS

        @JsonProperty("pisCofinsTaxStatus")
        private String pisCofinsTaxStatus; // Situação tributária do PIS/COFINS

        @JsonProperty("pis")
        private BigDecimal pis;               // Alíquota PIS

        @JsonProperty("cofins")
        private BigDecimal cofins;           // Alíquota COFINS

        @JsonProperty("csll")
        private BigDecimal csll;             // Alíquota CSLL

        @JsonProperty("inss")
        private BigDecimal inss;              // Alíquota INSS

        @JsonProperty("ir")
        private BigDecimal ir;                // Alíquota IR

    }
}
