package jdev.lojavirtual_fs.lojavirtual_fs.service.asaas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdev.lojavirtual_fs.lojavirtual_fs.config.AsaasConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AsaasFiscalInfoService {
    @Autowired
    private RestTemplate asaasRestTemplate;
    @Autowired
    private AsaasConfig asaasConfig;
    @Autowired
    private AsaasHeaderBuilderService headerBuilder;

    // Configurações do application.properties
    @Value("${campinas.inscricao-municipal:}")
    private String inscricaoMunicipal;

    @Value("${campinas.cnae:}")
    private String cnae;

    @Value("${campinas.regime-especial:6}")
    private String regimeEspecial;

    @Value("${campinas.codigo-servico:}")
    private String codigoServico;

    @Value("${campinas.item-lista-servico:}")
    private String itemListaServico;

    @Value("${campinas.simples-nacional:true}")
    private boolean simplesNacional;

    @Value("${certificado.caminho:}")
    private String caminhoCertificado;

    @Value("${certificado.senha:}")
    private String senhaCertificado;

    /**
     * Configura as informações fiscais no Asaas
     */
    public boolean configurarInformacoesFiscais() {
        String url = asaasConfig.getBaseUrl() + "/fiscalInfo";

        try {
            log.info("🔧 INICIANDO CONFIGURAÇÃO FISCAL");
            log.info("   Inscrição Municipal: {}", inscricaoMunicipal);
            log.info("   CNAE: {}", cnae);
            log.info("   Código Serviço: {}", codigoServico);
            log.info("   Item Lista: {}", itemListaServico);
            log.info("   Caminho Certificado: {}", caminhoCertificado);

            // Carregar certificado
            String certificadoBase64 = carregarCertificadoBase64();

            if (certificadoBase64 == null) {
                log.error("❌ CERTIFICADO NÃO CARREGADO - Abortando configuração");
                return false;
            }

            // Montar payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("municipalInscription", inscricaoMunicipal);
            payload.put("cnae", cnae);
            payload.put("simplesNacional", simplesNacional);
            payload.put("specialTaxRegime", regimeEspecial);
            payload.put("municipalServiceCode", codigoServico);
            payload.put("serviceListItem", itemListaServico);
            payload.put("rpsSerie", "A");
            payload.put("rpsNumber", 1);

            // Campos do certificado (CRÍTICO!)
            payload.put("certificate", certificadoBase64);
            payload.put("certificatePassword", senhaCertificado);

            log.info("📤 Payload sendo enviado (campos): {}", payload.keySet());
            log.info("   certificate está presente? {}", payload.containsKey("certificate"));
            log.info("   certificatePassword está presente? {}", payload.containsKey("certificatePassword"));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(
                    payload, headerBuilder.buildHeaders()
            );

            ResponseEntity<String> response = asaasRestTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class
            );

            log.info("✅ Resposta HTTP: {}", response.getStatusCode());
            log.info("   Corpo: {}", response.getBody());

            return response.getStatusCode() == HttpStatus.OK ||
                    response.getStatusCode() == HttpStatus.CREATED;

        } catch (Exception e) {
            log.error("❌ Erro na configuração fiscal: {}", e.getMessage(), e);
            return false;
        }

    }

    /**
     * Verifica se as informações fiscais já estão configuradas
     */
    public boolean verificarConfiguracaoFiscal() {
        String url = asaasConfig.getBaseUrl() + "/fiscalInfo";

        try {
            HttpEntity<?> entity = new HttpEntity<>(headerBuilder.buildHeaders());

            ResponseEntity<String> response = asaasRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(response.getBody());

                // Verifica se tem inscrição municipal (indica que está configurado)
                boolean configurado = json.has("municipalInscription") &&
                        json.get("municipalInscription") != null &&
                        !json.get("municipalInscription").asText().isEmpty();

                log.info("Status da configuração fiscal: {}", configurado ? "✅ CONFIGURADO" : "❌ NÃO CONFIGURADO");
                return configurado;
            }

            return false;

        } catch (Exception e) {
            log.error("Erro ao verificar configuração fiscal: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Valida se todas as configurações necessárias foram preenchidas
     */
    private boolean validarConfiguracoes() {
        if (inscricaoMunicipal == null || inscricaoMunicipal.isEmpty()) {
            log.error("❌ campinas.inscricao-municipal não configurado");
            return false;
        }

        if (cnae == null || cnae.isEmpty()) {
            log.error("❌ campinas.cnae não configurado");
            return false;
        }

        if (codigoServico == null || codigoServico.isEmpty()) {
            log.error("❌ campinas.codigo-servico não configurado");
            return false;
        }

        if (itemListaServico == null || itemListaServico.isEmpty()) {
            log.error("❌ campinas.item-lista-servico não configurado");
            return false;
        }

        return true;
    }
    /**
     * Carrega o certificado e converte para Base64
     */
    private String carregarCertificadoBase64() {
        /*try {
            if (caminhoCertificado == null || caminhoCertificado.isEmpty()) {
                log.warn("⚠️ Certificado não configurado. Campinas exige certificado!");
                return null;
            }

            byte[] certificadoBytes = Files.readAllBytes(Paths.get(caminhoCertificado));
            return Base64.getEncoder().encodeToString(certificadoBytes);

        } catch (Exception e) {
            log.error("❌ Erro ao carregar certificado: {}", e.getMessage());
            return null;
        }*/
        /*try {
            if (caminhoCertificado == null || caminhoCertificado.isEmpty()) {
                log.error("❌ CAMINHO DO CERTIFICADO NÃO CONFIGURADO");
                log.error("   Propriedade: certificado.caminho = {}", caminhoCertificado);
                return null;
            }

            log.info("📁 Tentando carregar certificado de: {}", caminhoCertificado);

            Path path = Paths.get(caminhoCertificado);
            if (!Files.exists(path)) {
                log.error("❌ ARQUIVO NÃO ENCONTRADO: {}", caminhoCertificado);
                log.error("   Diretório atual: {}", System.getProperty("user.dir"));
                return null;
            }

            byte[] certificadoBytes = Files.readAllBytes(path);
            String base64 = Base64.getEncoder().encodeToString(certificadoBytes);

            log.info("✅ Certificado carregado com sucesso!");
            log.info("   Tamanho do arquivo: {} bytes", certificadoBytes.length);
            log.info("   Tamanho do Base64: {} caracteres", base64.length());
            log.info("   Primeiros 50 chars: {}...", base64.substring(0, Math.min(50, base64.length())));

            return base64;

        } catch (Exception e) {
            log.error("❌ Erro ao carregar certificado: {}", e.getMessage());
            log.error("   Exceção: ", e);
            return null;
        }*/

        try {
            // Opção 1: Ler de um arquivo só com o Base64
            String caminhoBase64 = "/Users/faustusgomes/faustusdoc/netgom/certificado-base64.txt";
            Path path = Paths.get(caminhoBase64);

            if (Files.exists(path)) {
                return Files.readString(path).trim();
            }

            // Opção 2: Fallback para o método anterior
            return extrairBase64DoPem(Files.readString(Paths.get(caminhoCertificado)));

        } catch (Exception e) {
            log.error("❌ Erro: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extrai apenas o conteúdo Base64 do arquivo PEM (remove BEGIN/END)
     */
    private String extrairBase64DoPem(String conteudoPem) {
        try {
            // Remove linhas BEGIN e END, e quebras de linha
            String[] linhas = conteudoPem.split("\\n");
            StringBuilder base64 = new StringBuilder();

            for (String linha : linhas) {
                linha = linha.trim();
                if (!linha.startsWith("-----BEGIN") && !linha.startsWith("-----END") && !linha.isEmpty()) {
                    base64.append(linha);
                }
            }

            return base64.length() > 0 ? base64.toString() : null;

        } catch (Exception e) {
            log.error("Erro ao extrair Base64 do PEM: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Salva a configuração localmente (opcional)
     */
    private void salvarConfiguracaoLocal(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(responseBody);
            log.info("Configuração salva. ID Fiscal: {}", json.has("id") ? json.get("id") : "N/A");
        } catch (Exception e) {
            log.error("Erro ao processar resposta: {}", e.getMessage());
        }
    }

    /**
    * Verifica detalhadamente a configuração fiscal
    */
    public void diagnosticarConfiguracao() {
        String url = asaasConfig.getBaseUrl() + "/fiscalInfo";

        try {
            HttpEntity<?> entity = new HttpEntity<>(headerBuilder.buildHeaders());

            ResponseEntity<String> response = asaasRestTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode config = mapper.readTree(response.getBody());

            log.info("🔍 DIAGNÓSTICO DA CONFIGURAÇÃO FISCAL:");
            log.info("   certificateSent: {}", config.get("certificateSent").asBoolean());
            log.info("   passwordSent: {}", config.get("passwordSent").asBoolean());
            log.info("   accessTokenSent: {}", config.get("accessTokenSent").asBoolean());
            log.info("   municipalInscription: {}", config.get("municipalInscription").asText());
            log.info("   cnae: {}", config.get("cnae").asText());
            log.info("   specialTaxRegime: {}", config.get("specialTaxRegime").asText());

        } catch (Exception e) {
            log.error("Erro no diagnóstico: {}", e.getMessage());
        }
    }
}
