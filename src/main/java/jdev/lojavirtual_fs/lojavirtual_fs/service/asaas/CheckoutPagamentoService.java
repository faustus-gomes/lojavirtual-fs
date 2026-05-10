package jdev.lojavirtual_fs.lojavirtual_fs.service.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.PagamentoCartaoRequest;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.*;
import jdev.lojavirtual_fs.lojavirtual_fs.enums.StatusVendaLojaVirtual;
import jdev.lojavirtual_fs.lojavirtual_fs.model.*;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@Slf4j
public class CheckoutPagamentoService {

    @Autowired
    private VdCpLojaVirtRepository vendaRepository;  // OK, nome do repositório

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

    public String processarPagamento(PagamentoCartaoRequest request) {
        try {
            log.info("=== Iniciando processamento de pagamento ===");
            log.info("ID Venda recebido: {}", request.getIdVendaCampo());

            if (request.getIdVendaCampo() == null) {
                return "erro: ID da venda é nulo";
            }

            // 1. Buscar a venda - usando findById padrão do JpaRepository
            Optional<VendaCompraLojaVirtual> vendaOpt = vendaRepository.findById(request.getIdVendaCampo());

            if (vendaOpt.isEmpty()) {
                log.error("Venda não encontrada com ID: {}", request.getIdVendaCampo());
                return "erro: Venda não encontrada com ID " + request.getIdVendaCampo();
            }

            VendaCompraLojaVirtual venda = vendaOpt.get();
            log.info("Venda encontrada. ID: {}, Valor: R$ {}", venda.getId(), venda.getValorTotal());

            if (venda.getPessoa() == null) {
                return "erro: Venda não possui cliente associado";
            }

            // 2. Obter ou criar cliente no Asaas
            String cpfLimpo = request.getCpf().replaceAll("\\D", "");
            log.info("CPF Limpo: {}", cpfLimpo);

            String asaasCustomerId = obterCustomerId(cpfLimpo, venda.getPessoa());
            log.info("Customer ID Asaas: {}", asaasCustomerId);

            // 3. Montar requisição de pagamento
            AsaasPagamentoRequest pagamentoRequest = montarPagamentoRequest(request, venda, asaasCustomerId);

            // 4. Executar pagamento
            AsaasPagamentoResponse resposta = executarPagamentoCartao(pagamentoRequest);
            log.info("Resposta do Asaas: Status={}, ID={}", resposta.getStatus(), resposta.getId());

            // 5. Atualizar venda com os dados do pagamento
            venda.setPagamentoId(resposta.getId());
            venda.setStatusPagamento(resposta.getStatus());

            if ("CONFIRMED".equals(resposta.getStatus())) {
                venda.setStatusVendaLojaVirtual(StatusVendaLojaVirtual.FINALIZADA);
                vendaRepository.save(venda);
                return "sucesso";
            } else if ("PENDING".equals(resposta.getStatus())) {
                venda.setStatusVendaLojaVirtual(StatusVendaLojaVirtual.PENDENTE);
                vendaRepository.save(venda);
                return "pagamento_pendente";
            } else {
                vendaRepository.save(venda);
                return "status_pagamento: " + resposta.getStatus();
            }

        } catch (Exception e) {
            log.error("Erro ao processar pagamento: ", e);
            return "erro: " + e.getMessage();
        }
    }

    private String obterCustomerId(String cpfLimpo, Pessoa pessoa) {
        if (pessoa.getAsaasId() != null && !pessoa.getAsaasId().isEmpty()) {
            log.info("Usando AsaasId existente: {}", pessoa.getAsaasId());
            return pessoa.getAsaasId();
        }

        // ✅ BUSCAR PRIMEIRO POR CPF
        var clientePorCpfOpt = asaasClienteService.buscarClientePorCpfCnpj(cpfLimpo);
        if (clientePorCpfOpt.isPresent()) {
            String asaasId = clientePorCpfOpt.get().getId();
            log.info("Cliente encontrado no Asaas por CPF. ID: {}", asaasId);
            pessoa.setAsaasId(asaasId);
            if (pessoa instanceof PessoaFisica) {
                pessoaFisicaRepository.save((PessoaFisica) pessoa);
            }
            return asaasId;
        }

        // DEPOIS BUSCAR POR EMAIL
        var clienteOpt = asaasClienteService.buscarClientePorEmail(pessoa.getEmail());
        if (clienteOpt.isPresent()) {
            String asaasId = clienteOpt.get().getId();
            pessoa.setAsaasId(asaasId);
            if (pessoa instanceof PessoaFisica) {
                pessoaFisicaRepository.save((PessoaFisica) pessoa);
            }
            return asaasId;
        }

        // POR FIM, CRIAR NOVO CLIENTE
        var novoCliente = asaasClienteService.criarCliente(pessoa);
        String asaasId = novoCliente.getId();

        pessoa.setAsaasId(asaasId);
        if (pessoa instanceof PessoaFisica) {
            pessoaFisicaRepository.save((PessoaFisica) pessoa);
        }

        return asaasId;
    }

    private AsaasPagamentoRequest montarPagamentoRequest(PagamentoCartaoRequest request, VendaCompraLojaVirtual venda, String customerId) {
        LocalDate dueDate = LocalDate.now().plusDays(1);
        // Converter BigDecimal para Double com segurança
        Double valorTotal = venda.getValorTotal() != null ? venda.getValorTotal().doubleValue() : 0.0;

        AsaasPagamentoRequest.CreditCard cartao = AsaasPagamentoRequest.CreditCard.builder()
                .holderName(request.getNome())
                .number(request.getHashCartao())  // Para teste use "5162306219378829"
                .expiryMonth(request.getExpirationMonth())
                .expiryYear(request.getExpirationYear())
                .ccv("318")  // CVV fixo para teste
                .build();

        AsaasPagamentoRequest.CreditCardHolderInfo holderInfo = AsaasPagamentoRequest.CreditCardHolderInfo.builder()
                .name(request.getNome())
                .email(request.getEmail())
                .cpfCnpj(request.getCpf().replaceAll("\\D", ""))
                .postalCode(request.getCep().replaceAll("\\D", ""))
                .addressNumber(request.getNumero())
                .phone(venda.getPessoa().getTelefone())
                .build();

        return AsaasPagamentoRequest.builder()
                .customer(customerId)
                .billingType("CREDIT_CARD")
                .value(valorTotal)  // ← Agora é Double
                .dueDate(dueDate)
                .installmentCount(request.getQtdparcela() != null ? request.getQtdparcela() : 1)
                .creditCard(cartao)
                .creditCardHolderInfo(holderInfo)
                .description("Pagamento da Venda #" + venda.getId())
                .build();
    }

    private AsaasPagamentoResponse executarPagamentoCartao(AsaasPagamentoRequest request) {
        String url = asaasUrl + "/payments";
        log.info("URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("access_token", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AsaasPagamentoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<AsaasPagamentoResponse> response = asaasRestTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                AsaasPagamentoResponse.class
        );

        return response.getBody();
    }
}
