package jdev.lojavirtual_fs.lojavirtual_fs.service.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.config.AsaasConfig;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasInvoiceResponseDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasPageResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AsaasInvoiceQueryService {

    @Autowired
    private RestTemplate asaasRestTemplate;

    @Autowired
    private AsaasConfig asaasConfig;

    @Autowired
    private AsaasHeaderBuilderService headerBuilder;

    /**
     * Lista todas as notas fiscais
     */

    public AsaasPageResponseDTO<AsaasInvoiceResponseDTO> listarTodasNotas() {
        String url = asaasConfig.getBaseUrl() + "/invoices";
        HttpEntity<?> entity = new HttpEntity<>(headerBuilder.buildHeaders());

        ResponseEntity<AsaasPageResponseDTO<AsaasInvoiceResponseDTO>> response = asaasRestTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<AsaasPageResponseDTO<AsaasInvoiceResponseDTO>>() {}
        );

        return response.getBody();
    }

    /**
     * Encontra uma nota pelo número
     */
    public AsaasInvoiceResponseDTO encontrarPorNumero(String numeroNota) {
        AsaasPageResponseDTO<AsaasInvoiceResponseDTO> page = listarTodasNotas();

        return page.getData().stream()
                .filter(invoice -> numeroNota.equals(invoice.getNfseNumber()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Lista todas as notas fiscais e encontra pelo número
     * @param numeroNota Número da nota a ser encontrado (ex: 312363)
     * @return O invoiceId da nota encontrada ou null
     */
    public String encontrarInvoiceIdPorNumero(String numeroNota) {
        String url = asaasConfig.getBaseUrl() + "/invoices";
        HttpEntity<?> entity = new HttpEntity<>(headerBuilder.buildHeaders());

        try {
            log.info("📋 Buscando nota com número: {}", numeroNota);

            ResponseEntity<AsaasPageResponseDTO<AsaasInvoiceResponseDTO>> response = asaasRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<AsaasPageResponseDTO<AsaasInvoiceResponseDTO>>() {}
            );

            for (AsaasInvoiceResponseDTO invoice : response.getBody().getData()) {
                // Verifica se o número da nota corresponde
                if (numeroNota.equals(invoice.getNfseNumber())) {
                    log.info("✅ Nota encontrada! ID: {}, Número: {}",
                            invoice.getId(), invoice.getNfseNumber());
                    return invoice.getId(); // Retorna o invoiceId
                }
            }

            log.warn("⚠️ Nenhuma nota encontrada com o número: {}", numeroNota);
            return null;

        } catch (Exception e) {
            log.error("❌ Erro ao listar notas: {}", e.getMessage());
            return null;
        }
    }
}
