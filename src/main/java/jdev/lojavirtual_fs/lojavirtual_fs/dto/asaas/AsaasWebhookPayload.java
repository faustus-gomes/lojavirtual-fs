package jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsaasWebhookPayload {

    private String id;
    private String event;  // PAYMENT_CONFIRMED, PAYMENT_RECEIVED, PAYMENT_PENDING, etc.
    private String object;  // payment, customer, etc.

    @JsonProperty("payment")
    private PaymentData payment;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentData {
        private String id;
        private String status;  // CONFIRMED, PENDING, RECEIVED, REFUSED, CANCELED
        private Double value;
        private Double netValue;
        private String billingType;

        @JsonProperty("dueDate")
        private String dueDate;

        @JsonProperty("paymentDate")
        private String paymentDate;

        @JsonProperty("customer")
        private String customerId;

        @JsonProperty("externalReference")
        private String externalReference;  // Seu ID da venda
    }
}
