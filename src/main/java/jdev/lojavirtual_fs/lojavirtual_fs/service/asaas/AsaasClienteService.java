package jdev.lojavirtual_fs.lojavirtual_fs.service.asaas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.config.AsaasConfig;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasClienteRequest;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasClienteResponse;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasListResponse;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Endereco;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Pessoa;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaFisica;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaJuridica;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;


@Service
@Slf4j
public class AsaasClienteService {
    @Autowired
    @Qualifier("asaasRestTemplate")
    private RestTemplate asaasRestTemplate;

    @Autowired
    private AsaasConfig asaasConfig;

    @Value("${asaas.api.access-token}")
    private String apiKey;

    /**
     * Cria um cliente no Asaas a partir da sua entidade Pessoa
     */
    public AsaasClienteResponse criarCliente(Pessoa pessoa) {

        log.info("Asaas - Criando cliente: {} - {}", pessoa.getEmail(), pessoa.getTipoPessoa());

        AsaasClienteRequest request = montarRequestCliente(pessoa);

        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AsaasClienteRequest> entity = new HttpEntity<>(request, headers);

        try {
            String url = asaasConfig.getBaseUrl() + "/customers";
            log.debug("URL: {}", url);
            log.debug("Request: {}", request);

            // 🔹 Primeiro, pegar a resposta como String para ver o que o Asaas retorna
            ResponseEntity<String> responseRaw = asaasRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class  // ← Pega como String primeiro
            );

            log.info("Resposta BRUTA do Asaas: {}", responseRaw.getBody());

            // 🔹 Depois converte para o objeto
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());  // ← Adicionar suporte a Java 8 dates
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            AsaasClienteResponse response = mapper.readValue(responseRaw.getBody(), AsaasClienteResponse.class);

            log.info("Asaas - Cliente criado com sucesso. ID: {}", response.getId());
            return response;

        } catch (HttpClientErrorException e) {
            // 🔹 Aqui vai cair se for erro 400, 401, 403, 404, etc
            log.error("Asaas - Erro HTTP ao criar cliente: Status: {}, Body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new ExceptionLoja("Erro ao criar cliente no Asaas: " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            log.error("Asaas - Erro ao criar cliente: {}", e.getMessage());
            throw new ExceptionLoja("Erro ao criar cliente no Asaas: " + e.getMessage());
        } catch (Exception e) {
            log.error("Asaas - Erro inesperado: ", e);
            throw new ExceptionLoja("Erro inesperado: " + e.getMessage());
        }
    }

    /**
     * Busca cliente no Asaas por email
     */
    public Optional<AsaasClienteResponse> buscarClientePorEmail(String email) {
        log.info("Asaas - Buscando cliente por email: {}", email);

        HttpHeaders headers = createHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            String url = asaasConfig.getBaseUrl() + "/customers?email=" + email;

            ResponseEntity<AsaasListResponse<AsaasClienteResponse>> response =
                    asaasRestTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            new ParameterizedTypeReference<AsaasListResponse<AsaasClienteResponse>>() {}
                    );

            if (response.getBody() != null && response.getBody().getData() != null
                    && !response.getBody().getData().isEmpty()) {
                log.info("Asaas - Cliente encontrado: {}", response.getBody().getData().get(0).getId());
                return Optional.of(response.getBody().getData().get(0));
            }

            log.info("Asaas - Cliente não encontrado com email: {}", email);
            return Optional.empty();

        } catch (RestClientException e) {
            log.error("Asaas - Erro ao buscar cliente: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Busca cliente no Asaas por ID
     */
    public Optional<AsaasClienteResponse> buscarClientePorId(String asaasId) {
        log.info("Asaas - Buscando cliente por ID: {}", asaasId);

        HttpHeaders headers = createHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            String url = asaasConfig.getBaseUrl() + "/customers/" + asaasId;

            ResponseEntity<AsaasClienteResponse> response = asaasRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    AsaasClienteResponse.class
            );

            if (response.getBody() != null) {
                log.info("Asaas - Cliente encontrado: {}", response.getBody().getId());
                return Optional.of(response.getBody());
            }

            return Optional.empty();

        } catch (HttpClientErrorException.NotFound e) {
            log.info("Asaas - Cliente não encontrado com ID: {}", asaasId);
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Asaas - Erro ao buscar cliente por ID: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Atualiza cliente no Asaas
     */
    public Optional<AsaasClienteResponse> atualizarCliente(String asaasId, Pessoa pessoa) {
        log.info("Asaas - Atualizando cliente: {}", asaasId);

        AsaasClienteRequest request = montarRequestCliente(pessoa);

        HttpHeaders headers = createHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AsaasClienteRequest> entity = new HttpEntity<>(request, headers);

        try {
            String url = asaasConfig.getBaseUrl() + "/customers/" + asaasId;

            ResponseEntity<AsaasClienteResponse> response = asaasRestTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    AsaasClienteResponse.class
            );

            log.info("Asaas - Cliente atualizado com sucesso: {}", asaasId);
            return Optional.of(response.getBody());

        } catch (RestClientException e) {
            log.error("Asaas - Erro ao atualizar cliente: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Remove cliente no Asaas
     */
    public boolean deletarCliente(String asaasId) {
        log.info("Asaas - Deletando cliente: {}", asaasId);

        HttpHeaders headers = createHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            String url = asaasConfig.getBaseUrl() + "/customers/" + asaasId;

            ResponseEntity<Void> response = asaasRestTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );

            log.info("Asaas - Cliente deletado com sucesso: {}", asaasId);
            return response.getStatusCode() == HttpStatus.OK;

        } catch (RestClientException e) {
            log.error("Asaas - Erro ao deletar cliente: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Monta o request do Asaas a partir da entidade Pessoa
     */
    private AsaasClienteRequest montarRequestCliente(Pessoa pessoa) {
        AsaasClienteRequest request = new AsaasClienteRequest();
        request.setName(pessoa.getNome());
        request.setEmail(pessoa.getEmail());
        request.setMobilePhone(pessoa.getTelefone());
        request.setExternalReference(pessoa.getId().toString());

        // Setar CPF ou CNPJ baseado no tipo
        if ("FISICA".equals(pessoa.getTipoPessoa()) && pessoa instanceof PessoaFisica) {
            PessoaFisica pf = (PessoaFisica) pessoa;
            request.setCpfCnpj(pf.getCpf());
            request.setPersonType("FISICA");
        } else if ("JURIDICA".equals(pessoa.getTipoPessoa()) && pessoa instanceof PessoaJuridica) {
            PessoaJuridica pj = (PessoaJuridica) pessoa;
            request.setCpfCnpj(pj.getCnpj());
            request.setPersonType("JURIDICA");
        }

        // Adicionar endereço se existir (usando seu método enderecoEntrega)
        if (pessoa.getEnderecos() != null && !pessoa.getEnderecos().isEmpty()) {
            Endereco endereco = pessoa.enderecoEntrega();
            if (endereco != null) {
                request.setPostalCode(endereco.getCep());
                request.setAddress(endereco.getRuaLogra());
                request.setAddressNumber(endereco.getNumero());
                request.setComplement(endereco.getComplemento());
                request.setProvince(endereco.getBairro());

                if (endereco.getCidade() != null) {
                    request.setCity(endereco.getCidade());
                    request.setState(endereco.getUf());
                }
                request.setCountry("BR");
            }
        }

        return request;
    }

    /**
     * Cria headers com token de autenticação
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("access_token", apiKey);
        return headers;
    }
}
