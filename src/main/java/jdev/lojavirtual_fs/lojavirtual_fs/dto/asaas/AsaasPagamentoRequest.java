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
public class AsaasPagamentoRequest {

    private String customer;
    private String billingType;
    private Double value;
    private LocalDate dueDate;
    private String description;
    private Integer installmentCount;
    private CreditCard creditCard;
    private CreditCardHolderInfo creditCardHolderInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditCard {
        private String holderName;
        private String number;
        private String expiryMonth;
        private String expiryYear;
        private String ccv;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditCardHolderInfo {
        private String name;
        private String email;
        private String cpfCnpj;
        private String postalCode;
        private String addressNumber;
        private String phone;
    }
}
