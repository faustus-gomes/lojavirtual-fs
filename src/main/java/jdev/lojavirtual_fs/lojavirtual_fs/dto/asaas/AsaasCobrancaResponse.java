package jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsaasCobrancaResponse {

    private String id;                    // ID da cobrança no Asaas
    private String customer;              // ID do cliente
    private String billingType;           // "PIX", "BOLETO", "CREDIT_CARD"
    private Double value;                 // Valor
    private LocalDate dueDate;            // Data de vencimento
    private String status;                // PENDING, RECEIVED, CONFIRMED, etc.
    private String invoiceUrl;            // URL da fatura
    private String bankSlipUrl;           // URL do boleto (se for boleto)
    private String pixQrCode;             // QR Code PIX (base64 ou URL)
    private String pixCopyPaste;          // Código copia e cola do PIX
    private LocalDate paymentDate;        // Data do pagamento
    private String description;           // Descrição
    private String externalReference;     // Sua referência
}
