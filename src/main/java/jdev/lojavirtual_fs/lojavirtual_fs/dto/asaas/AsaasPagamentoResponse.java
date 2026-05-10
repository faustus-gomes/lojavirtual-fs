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
public class AsaasPagamentoResponse {
    private String id;
    private String status;
    private Double value;
    private String invoiceUrl;
    private LocalDate paymentDate;
}
