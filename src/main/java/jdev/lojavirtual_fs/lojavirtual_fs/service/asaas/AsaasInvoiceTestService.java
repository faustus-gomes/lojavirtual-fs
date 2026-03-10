package jdev.lojavirtual_fs.lojavirtual_fs.service.asaas;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jdev.lojavirtual_fs.lojavirtual_fs.config.AsaasConfig;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasInvoiceRequestDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasInvoiceResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
public class AsaasInvoiceTestService {

    @Autowired
    private RestTemplate asaasRestTemplate;
    @Autowired
    private AsaasConfig asaasConfig;
    @Autowired
    private AsaasHeaderBuilderService headerBuilder;

    @Autowired
    private AsaasInvoiceQueryService queryService;

    public void testarConexao() {

        String url = asaasConfig.getBaseUrl() + "/invoices/municipalServices?limit=1";
        HttpEntity<?> entity = new HttpEntity<>(headerBuilder.buildHeaders());

        try {
            ResponseEntity<String> response = asaasRestTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class
            );

            log.info("✅ CONEXÃO OK! Status: {}", response.getStatusCode());
        } catch (Exception e) {
            log.error("❌ ERRO: {}", e.getMessage());
        }
    }

    /***
     * TESTE 2: Emitir nota fiscal de teste
     * */

    public AsaasInvoiceResponseDTO testarEmissaoSimples() {
        AsaasInvoiceRequestDTO request = new AsaasInvoiceRequestDTO();

        try {  // ← TRY CORRETO
            request.setCustomerId("cus_000007641063");
            request.setServiceDescription("Nota de teste - Sistema Loja Virtual Faustus");
            request.setValue(new BigDecimal("1.00"));
            request.setEffectiveDate(LocalDate.now().toString());
            // 🔍 NOVOS LOGS AQUI - LOGO APÓS SETAR A DATA
            log.info("🔍 DATA CONFIGURADA: {}", request.getEffectiveDate());
            log.info("🔍 TIPO DA DATA: {}", request.getEffectiveDate().getClass().getName());
            request.setMunicipalServiceCode("692060100");
            //request.setExternalReference("TESTE-" + System.currentTimeMillis());
            request.setSeries("A");           // Série
            request.setRpsNumber(1);          // Número do RPS
            // Configurar impostos
            AsaasInvoiceRequestDTO.Taxes taxes = new AsaasInvoiceRequestDTO.Taxes();
            taxes.setRetainIss(false);
            taxes.setIss(new BigDecimal("2.00"));
            taxes.setSpecialTaxRegime("2"); // Regime especial
            request.setTaxes(taxes);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());  // ← ADICIONE ESTA LINHA!
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Opcional: formata como string ISO
            mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
            mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS); // Inclui todos os campos
            mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

            String jsonEnviado = mapper.writeValueAsString(request);
            log.info("🔍 JSON COMPLETO ENVIADO: {}", jsonEnviado);

            // Depois de configurar tudo, faça:
            log.info("🔍 TESTE MANUAL - toString do request: {}", request.toString());

            String idempotencyKey = UUID.randomUUID().toString();
            String url = asaasConfig.getBaseUrl() + "/invoices";

            HttpEntity<AsaasInvoiceRequestDTO> entity = new HttpEntity<>(
                    request,
                    headerBuilder.buildHeadersWithIdempotency(idempotencyKey)
            );
            // Log específico dos campos problemáticos
            log.info("🔍 series: '{}'", request.getSeries());
            log.info("🔍 rpsNumber: {}", request.getRpsNumber());
            if (request.getTaxes() != null) {
                log.info("🔍 specialTaxRegime: '{}'", request.getTaxes().getSpecialTaxRegime());
            } else {
                log.error("❌ taxes está null!");
            }
            //log.info("📤 Enviando nota de teste Faustus...");

            ResponseEntity<AsaasInvoiceResponseDTO> response = asaasRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    AsaasInvoiceResponseDTO.class
            );

            AsaasInvoiceResponseDTO invoice = response.getBody();
            log.info("✅ NOTA CRIADA! ID: {}, Status: {}", invoice.getId(), invoice.getStatus());

            return invoice;

        } catch (Exception e) {  // ← CATCH CORRETO
            log.error("❌ ERRO NA EMISSÃO: {}", e.getMessage());
            throw new RuntimeException("Falha no teste de emissão", e);
        }
    }

    /**
     * TESTE 3: Consultar nota fiscal por ID
     */

    public AsaasInvoiceResponseDTO testarConsulta(String invoiceId) {
        String url = asaasConfig.getBaseUrl() + "/invoices/" + invoiceId;

        HttpEntity<?> entity = new HttpEntity<>(headerBuilder.buildHeaders());

        try {
            log.info("📋 Consultando nota fiscal: {}", invoiceId);

            ResponseEntity<AsaasInvoiceResponseDTO> response = asaasRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    AsaasInvoiceResponseDTO.class
            );

            AsaasInvoiceResponseDTO invoice = response.getBody();
            log.info("✅ CONSULTA REALIZADA! Status: {}", invoice.getStatus());

            return invoice;
        }catch (Exception e) {
            log.error("❌ ERRO NA CONSULTA: {}", e.getMessage());
            throw new RuntimeException("Falha na consulta", e);
        }

    }

    /**
     * 🔥 NOVO MÉTODO: Busca nota pelo número e consulta
     * @param numeroNota Número da nota (ex: 312363)
     * @return Dados completos da nota
     */
    public AsaasInvoiceResponseDTO buscarEConsultarPorNumero(String numeroNota) {
        log.info("🔍 Buscando nota com número: {}", numeroNota);

        // 1. Usa o QueryService para encontrar o invoiceId
        String invoiceId = queryService.encontrarInvoiceIdPorNumero(numeroNota);

        if (invoiceId == null) {
            log.error("❌ Nota com número {} não encontrada", numeroNota);
            return null;
        }

        // 2. Usa o próprio método testarConsulta para obter os dados
        log.info("✅ InvoiceId encontrado: {}, consultando detalhes...", invoiceId);
        return testarConsulta(invoiceId);
    }

}
