package jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsaasCobrancaRequest {

    private String customer;              // ID do cliente no Asaas
    private String billingType;           // "PIX", "BOLETO", "CREDIT_CARD"
    private Double value;                 // Valor da cobrança
    private LocalDate dueDate;            // Data de vencimento
    private String description;           // Descrição
    private String externalReference;     // Referência externa (seu ID)
    private Integer installmentCount;     // Número de parcelas (para boleto parcelado)
    private Double installmentValue;      // Valor de cada parcela

    // Campos específicos para BOLETO
    private String bankSlipUrl;           // URL do boleto (retornado)
    private String invoiceUrl;            // URL da fatura

    // Campos específicos para PIX
    private String pixKey;                // Chave PIX (opcional)
    private Integer daysDueDate;          // Dias para vencimento (padrão: 1)

}
