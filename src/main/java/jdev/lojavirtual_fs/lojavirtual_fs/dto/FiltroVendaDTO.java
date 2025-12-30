package jdev.lojavirtual_fs.lojavirtual_fs.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class FiltroVendaDTO {
    private Long idProduto;
    private String nomeProduto;
    private String nomeCliente;
    private String emailCliente;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    public static FiltroVendaDTO fromParams(Long idProduto, String nomeProduto,
                                            String nomeCliente, String emailCliente,
                                            LocalDate dataInicio, LocalDate dataFim) {
        return FiltroVendaDTO.builder()
                .idProduto(idProduto)
                .nomeProduto(tratarStringVazia(nomeProduto))
                .nomeCliente(tratarStringVazia(nomeCliente))
                .emailCliente(tratarStringVazia(emailCliente))
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                .build();
    }

    private static String tratarStringVazia(String valor) {
        return (valor == null || valor.trim().isEmpty()) ? null : valor.trim();
    }

}
