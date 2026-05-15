package jdev.lojavirtual_fs.lojavirtual_fs.service.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasPagamentoRequest;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasPagamentoResponse;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.PagamentoCartaoRequest;
import jdev.lojavirtual_fs.lojavirtual_fs.enums.StatusVendaLojaVirtual;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Pessoa;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaFisica;
import jdev.lojavirtual_fs.lojavirtual_fs.model.VendaCompraLojaVirtual;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.PessoaFisicaRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.VdCpLojaVirtRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Slf4j
public class CartaoPagamentoService {
    @Autowired
    private VdCpLojaVirtRepository vendaRepository;

    @Autowired
    private PessoaFisicaRepository pessoaFisicaRepository;

    @Autowired
    private AsaasClienteService asaasClienteService;

    @Autowired
    @Qualifier("asaasRestTemplate")
    private RestTemplate asaasRestTemplate;

    @Value("${asaas.api.access-token}")
    private String apiKey;

    @Value("${asaas.api.base-url}")
    private String asaasUrl;

    /**
     * AULA 12.17 - Iniciando Pagamento por Cartão - Parte 1
     * Método principal que inicia todo o processo
     */
    public String processarPagamentoCartao(PagamentoCartaoRequest request) {
        log.info("=== AULA 12.17 - Iniciando Pagamento por Cartão ===");

        // 1. Validar dados da requisição
        validarRequisicao(request);

        // 2. Buscar a venda no banco
        VendaCompraLojaVirtual venda = buscarVenda(request.getIdVendaCampo());

        // 3. Obter ou criar cliente no Asaas
        String customerId = obterCustomerId(request.getCpf(), venda.getPessoa());

        // 4. Montar objeto de cobrança (Aulas 12.18, 12.19, 12.20)
        AsaasPagamentoRequest pagamentoRequest = montarCobrancaCartao(request, venda, customerId);

        // 5. Enviar para API do Asaas (Aula 12.21)
        AsaasPagamentoResponse resposta = enviarPagamentoAsaas(pagamentoRequest);

        // 6. Processar resposta e atualizar venda
        return processarResposta(resposta, venda);
    }

    /**
     * AULA 12.17 - Validação da requisição
     */
    private void validarRequisicao(PagamentoCartaoRequest request) {
        log.info("AULA 12.17 - Validando requisição");

        if (request.getIdVendaCampo() == null) {
            throw new RuntimeException("ID da venda é obrigatório");
        }
        if (request.getHashCartao() == null || request.getHashCartao().isEmpty()) {
            throw new RuntimeException("Dados do cartão são obrigatórios");
        }
        if (request.getCpf() == null || request.getCpf().isEmpty()) {
            throw new RuntimeException("CPF é obrigatório");
        }
        if (request.getNome() == null || request.getNome().isEmpty()) {
            throw new RuntimeException("Nome do titular é obrigatório");
        }

        log.info("Requisição validada com sucesso");
    }

    /**
     * Buscar venda no banco
     */
    private VendaCompraLojaVirtual buscarVenda(Long idVenda) {
        log.info("Buscando venda ID: {}", idVenda);

        Optional<VendaCompraLojaVirtual> vendaOpt = vendaRepository.findById(idVenda);
        if (vendaOpt.isEmpty()) {
            throw new RuntimeException("Venda não encontrada com ID: " + idVenda);
        }

        VendaCompraLojaVirtual venda = vendaOpt.get();
        log.info("Venda encontrada. ID: {}, Valor: R$ {}", venda.getId(), venda.getValorTotal());
        return venda;
    }

    /**
     * Obter ou criar Customer ID no Asaas
     */
    private String obterCustomerId(String cpf, Pessoa pessoa) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        log.info("Buscando cliente com CPF: {}", cpfLimpo);

        // Verificar se já tem ID na base local
        if (pessoa.getAsaasId() != null && !pessoa.getAsaasId().isEmpty()) {
            log.info("Usando AsaasId existente: {}", pessoa.getAsaasId());
            return pessoa.getAsaasId();
        }

        // Buscar por CPF no Asaas
        var clientePorCpfOpt = asaasClienteService.buscarClientePorCpfCnpj(cpfLimpo);
        if (clientePorCpfOpt.isPresent()) {
            String asaasId = clientePorCpfOpt.get().getId();
            log.info("Cliente encontrado por CPF. ID: {}", asaasId);
            pessoa.setAsaasId(asaasId);
            salvarPessoa(pessoa);
            return asaasId;
        }

        // Buscar por email no Asaas
        var clientePorEmailOpt = asaasClienteService.buscarClientePorEmail(pessoa.getEmail());
        if (clientePorEmailOpt.isPresent()) {
            String asaasId = clientePorEmailOpt.get().getId();
            log.info("Cliente encontrado por email. ID: {}", asaasId);
            pessoa.setAsaasId(asaasId);
            salvarPessoa(pessoa);
            return asaasId;
        }

        // Criar novo cliente
        log.info("Cliente não encontrado. Criando novo cliente...");
        var novoCliente = asaasClienteService.criarCliente(pessoa);
        String asaasId = novoCliente.getId();
        pessoa.setAsaasId(asaasId);
        salvarPessoa(pessoa);

        log.info("Cliente criado com sucesso. AsaasId: {}", asaasId);
        return asaasId;
    }

    private void salvarPessoa(Pessoa pessoa) {
        if (pessoa instanceof PessoaFisica) {
            pessoaFisicaRepository.save((PessoaFisica) pessoa);
        }
    }

    /**
     * AULA 12.18 - Criando objetos de cobrança de Cartão - Parte 2
     * AULA 12.19 - Montando Objeto de cobrança de cartão - Parte 3
     * AULA 12.20 - Montando Objeto de cobrança de cartão - Parte 4
     */
    private AsaasPagamentoRequest montarCobrancaCartao(PagamentoCartaoRequest request,
                                                       VendaCompraLojaVirtual venda,
                                                       String customerId) {
        log.info("=== AULA 12.18 - Criando objeto do cartão ===");

        // AULA 12.18 - Criar objeto do cartão
        AsaasPagamentoRequest.CreditCard cartao = AsaasPagamentoRequest.CreditCard.builder()
                .holderName(request.getNome())
                .number(request.getHashCartao())  // Para teste: "5162306219378829"
                .expiryMonth(request.getExpirationMonth())
                .expiryYear(request.getExpirationYear())
                .ccv("318")  // CVV fixo para teste
                .build();

        log.info("Cartão criado - Titular: {}", cartao.getHolderName());

        // AULA 12.19 - Criar objeto do titular
        log.info("=== AULA 12.19 - Criando objeto do titular ===");

        AsaasPagamentoRequest.CreditCardHolderInfo holderInfo = AsaasPagamentoRequest.CreditCardHolderInfo.builder()
                .name(request.getNome())
                .email(request.getEmail())
                .cpfCnpj(request.getCpf().replaceAll("\\D", ""))
                .postalCode(request.getCep().replaceAll("\\D", ""))
                .addressNumber(request.getNumero())
                .phone(venda.getPessoa().getTelefone())
                .build();

        log.info("Titular criado - Nome: {}, Email: {}", holderInfo.getName(), holderInfo.getEmail());

        // AULA 12.20 - Montar objeto completo de cobrança
        log.info("=== AULA 12.20 - Montando objeto completo de cobrança ===");

        Double valorTotal = venda.getValorTotal() != null ? venda.getValorTotal().doubleValue() : 0.0;

        AsaasPagamentoRequest pagamentoRequest = AsaasPagamentoRequest.builder()
                .customer(customerId)
                .billingType("CREDIT_CARD")
                .value(valorTotal)
                .dueDate(LocalDate.now().plusDays(1))
                .installmentCount(request.getQtdparcela() != null ? request.getQtdparcela() : 1)
                .creditCard(cartao)
                .creditCardHolderInfo(holderInfo)
                .description("Pagamento da Venda #" + venda.getId())
                .build();

        log.info("Cobrança montada - Cliente: {}, Valor: {}, Parcelas: {}",
                customerId, valorTotal, pagamentoRequest.getInstallmentCount());

        return pagamentoRequest;
    }

    /**
     * AULA 12.21 - Enviar Pagamento para Asaas Api Rest Cartão - Parte 5
     */
    private AsaasPagamentoResponse enviarPagamentoAsaas(AsaasPagamentoRequest request) {
        log.info("=== AULA 12.21 - Enviando Pagamento para API Asaas ===");

        String url = asaasUrl + "/payments";
        log.info("URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("access_token", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AsaasPagamentoRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<AsaasPagamentoResponse> response = asaasRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    AsaasPagamentoResponse.class
            );

            AsaasPagamentoResponse resposta = response.getBody();
            log.info("Resposta recebida - Status: {}, ID: {}", resposta.getStatus(), resposta.getId());

            return resposta;

        } catch (Exception e) {
            log.error("Erro ao enviar pagamento: {}", e.getMessage());
            throw new RuntimeException("Erro ao processar pagamento: " + e.getMessage());
        }
    }

    /**
     * Processar resposta e atualizar venda
     */
    private String processarResposta(AsaasPagamentoResponse resposta, VendaCompraLojaVirtual venda) {
        log.info("=== AULA 12.23 - Gravando pagamento no banco ===");

        // Gravar dados do pagamento
        venda.setPagamentoId(resposta.getId());
        venda.setStatusPagamento(resposta.getStatus());
        venda.setValorTotal(BigDecimal.valueOf(resposta.getValue()));

        if (resposta.getPaymentDate() != null) {
            venda.setDataVenda(java.sql.Date.valueOf(resposta.getPaymentDate()));
        }

        vendaRepository.save(venda);
        log.info("Pagamento gravado com sucesso. ID: {}, Status: {}", resposta.getId(), resposta.getStatus());

        if ("CONFIRMED".equals(resposta.getStatus())) {
            venda.setStatusVendaLojaVirtual(StatusVendaLojaVirtual.FINALIZADA);
            vendaRepository.save(venda);
            return "sucesso";

        } else if ("PENDING".equals(resposta.getStatus())) {
            venda.setStatusVendaLojaVirtual(StatusVendaLojaVirtual.PENDENTE);
            vendaRepository.save(venda);
            log.info("Pagamento PENDENTE para venda ID: {}", venda.getId());
            return "pagamento_pendente";

        } else {
            //vendaRepository.save(venda);
            //log.warn("Status inesperado: {} para venda ID: {}", resposta.getStatus(), venda.getId());
            return "status_pagamento: " + resposta.getStatus();
        }
    }
    /**
     * AULA 12.22 - Obtendo resposta de erros do Cartão - Parte 6
     * Tratamento específico para erros de cartão
     */
    private String tratarErroCartao(Exception e, AsaasPagamentoResponse resposta) {
        log.error("=== AULA 12.22 - Tratando erro de cartão ===");

        if (resposta != null && resposta.getStatus() != null) {
            switch (resposta.getStatus()) {
                case "REFUSED":
                    return "erro: Cartão recusado - Verifique os dados ou tente outro cartão";
                case "CANCELED":
                    return "erro: Pagamento cancelado";
                case "CHARGEBACK":
                    return "erro: Transação contestada";
                default:
                    return "erro: Status não processado - " + resposta.getStatus();
            }
        }

        if (e.getMessage().contains("invalid card number")) {
            return "erro: Número do cartão inválido";
        }
        if (e.getMessage().contains("invalid cvv")) {
            return "erro: CVV inválido";
        }
        if (e.getMessage().contains("expired")) {
            return "erro: Cartão expirado";
        }
        if (e.getMessage().contains("insufficient")) {
            return "erro: Saldo insuficiente";
        }

        return "erro: " + e.getMessage();
    }

    /**
     * AULA 12.24 - Obtendo o status do pagamento com cartão - Parte 8
     * Consulta status atualizado no Asaas
     */
    public String consultarStatusPagamento(String pagamentoId) {
        log.info("=== AULA 12.24 - Consultando status do pagamento ===");
        log.info("Pagamento ID: {}", pagamentoId);

        try {
            String url = asaasUrl + "/payments/" + pagamentoId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("access_token", apiKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<AsaasPagamentoResponse> response = asaasRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    AsaasPagamentoResponse.class
            );

            AsaasPagamentoResponse resposta = response.getBody();
            log.info("Status atual: {}", resposta.getStatus());

            // Atualizar no banco
            Optional<VendaCompraLojaVirtual> vendaOpt = vendaRepository.findByPagamentoId(pagamentoId);
            if (vendaOpt.isPresent()) {
                VendaCompraLojaVirtual venda = vendaOpt.get();
                venda.setStatusPagamento(resposta.getStatus());
                if ("CONFIRMED".equals(resposta.getStatus())) {
                    venda.setStatusVendaLojaVirtual(StatusVendaLojaVirtual.FINALIZADA);
                }
                vendaRepository.save(venda);
            }

            return resposta.getStatus();

        } catch (Exception e) {
            log.error("Erro ao consultar status: {}", e.getMessage());
            return "erro: " + e.getMessage();
        }
    }
}
