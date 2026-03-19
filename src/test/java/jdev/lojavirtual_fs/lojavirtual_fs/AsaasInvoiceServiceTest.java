package jdev.lojavirtual_fs.lojavirtual_fs;

import jdev.lojavirtual_fs.lojavirtual_fs.config.AsaasConfig;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasInvoiceRequestDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasInvoiceResponseDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.service.asaas.AsaasHeaderBuilderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsaasInvoiceServiceTest {

    private final RestTemplate asaasRestTemplate;
    private final AsaasConfig asaasConfig;
    private final AsaasHeaderBuilderService headerBuilder;

    /**
     * TESTE1: Verfificar se o token está funcionando
     * */
    public void testarConexao() {
        String url = asaasConfig.getBaseUrl() + "/invoices/municipalServices?limit=1";

        try {
            HttpEntity<?> entity = new HttpEntity<>(headerBuilder.buildHeaders());
            ResponseEntity<String> response = asaasRestTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );

            log.info("✅ CONEXÃO OK! Status: {}", response.getStatusCode());
            log.info("Resposta: {}", response.getBody());

        } catch (Exception e){
            log.error("❌ ERRO NA CONEXÃO: {}", e.getMessage());
        }
    }

    /**
     * TESTE 2: Emitir nota fiscal de teste
     * */

    public AsaasInvoiceResponseDTO testarEmissaoSimples() {
        LocalDate data = LocalDate.now();
        AsaasInvoiceRequestDTO request = new AsaasInvoiceRequestDTO();

        request.setCustomerId("cus_test_123456");
        request.setServiceDescription("Nota de teste - Sistema Loja Virtual Faustus");
        request.setValue(new BigDecimal("1.00"));
        request.setEffectiveDate(data.toString());
        request.setMunicipalServiceCode("0321");
        request.setExternalReference("TESTE-" + System.currentTimeMillis());

        AsaasInvoiceRequestDTO.Taxes taxes = new AsaasInvoiceRequestDTO.Taxes();
        taxes.setRetainIss(false);
        taxes.setIss(new BigDecimal("2.00"));
        request.setTaxes(taxes);

        String idempotencyKey = UUID.randomUUID().toString();
        String url = asaasConfig.getBaseUrl() + "/invoices";

        HttpEntity<AsaasInvoiceRequestDTO> entity = new HttpEntity<>(
                request,
                headerBuilder.buildHeadersWithIdempotency(idempotencyKey)
        );

        try {
            log.info("📤 Enviando nota de teste...");

            ResponseEntity<AsaasInvoiceResponseDTO> response = asaasRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    AsaasInvoiceResponseDTO.class
            );

            AsaasInvoiceResponseDTO invoice = response.getBody();
            log.info("✅ NOTA CRIADA! ID: {}, Status: {}", invoice.getId(), invoice.getStatus());

            return invoice;

        } catch (Exception e) {
            log.error("❌ ERRO NA EMISSÃO: {}", e.getMessage());
            throw new RuntimeException("Falha no teste de emissão", e);
        }
    }
}
