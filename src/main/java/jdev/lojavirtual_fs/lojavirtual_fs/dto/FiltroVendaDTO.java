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

    // Novos filtros para endereços
    private String cidadeEntrega;
    private String estadoEntrega;
    private String bairroEntrega;
    private String cepEntrega;

    private String cidadeCobranca;
    private String estadoCobranca;
    private String bairroCobranca;
    private String cepCobranca;

    private Long idCliente;
    private String cpfCliente;

    public static FiltroVendaDTO fromParams(Long idProduto, String nomeProduto,
                                            Long idCliente,String nomeCliente,
                                            String cpfCliente,String emailCliente,
                                            LocalDate dataInicio, LocalDate dataFim,
                                            // Novos parâmetros
                                            String cidadeEntrega, String estadoEntrega,
                                            String bairroEntrega, String cepEntrega,
                                            String cidadeCobranca, String estadoCobranca,
                                            String bairroCobranca, String cepCobranca) {
        return FiltroVendaDTO.builder()
                .idProduto(idProduto)
                .nomeProduto(tratarStringVazia(nomeProduto))
                .idCliente(idCliente)
                .nomeCliente(tratarStringVazia(nomeCliente))
                .cpfCliente(tratarStringVazia(cpfCliente))
                .emailCliente(tratarStringVazia(emailCliente))
                .dataInicio(dataInicio)
                .dataFim(dataFim)
                // Novos campos
                .cidadeEntrega(tratarStringVazia(cidadeEntrega))
                .estadoEntrega(tratarStringVazia(estadoEntrega))
                .bairroEntrega(tratarStringVazia(bairroEntrega))
                .cepEntrega(tratarStringVazia(cepEntrega))
                .cidadeCobranca(tratarStringVazia(cidadeCobranca))
                .estadoCobranca(tratarStringVazia(estadoCobranca))
                .bairroCobranca(tratarStringVazia(bairroCobranca))
                .cepCobranca(tratarStringVazia(cepCobranca))
                .build();
    }

    private static String tratarStringVazia(String valor) {
        return (valor == null || valor.trim().isEmpty()) ? null : valor.trim();
    }

}
