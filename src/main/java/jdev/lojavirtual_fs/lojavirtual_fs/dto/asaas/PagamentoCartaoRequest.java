package jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas;
import lombok.Data;

@Data
public class PagamentoCartaoRequest {

    private String hashCartao;
    private String expirationMonth;
    private String expirationYear;
    private Long idVendaCampo;
    private String cpf;
    private Integer qtdparcela;
    private String cep;
    private String rua;
    private String numero;
    private String estado;
    private String cidade;
    private String nome;
    private String email;
}
