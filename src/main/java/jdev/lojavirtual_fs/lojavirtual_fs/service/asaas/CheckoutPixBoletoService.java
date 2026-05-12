package jdev.lojavirtual_fs.lojavirtual_fs.service.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasCobrancaRequest;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasCobrancaResponse;
import jdev.lojavirtual_fs.lojavirtual_fs.model.*;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.VdCpLojaVirtRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Slf4j
public class CheckoutPixBoletoService {

    @Autowired
    private VdCpLojaVirtRepository vendaRepository;

    @Autowired
    private AsaasClienteService asaasClienteService;

    @Autowired
    private AsaasCobrancaService asaasCobrancaService;

    /**
     * Gera cobrança PIX
     */
    public AsaasCobrancaResponse gerarPix(Long idVenda) {
        return gerarCobranca(idVenda, "PIX");
    }

    /**
     * Gera cobrança BOLETO
     */
    public AsaasCobrancaResponse gerarBoleto(Long idVenda) {
        return gerarCobranca(idVenda, "BOLETO");
    }

    /**
     * Gera cobrança genérica (PIX ou BOLETO)
     */
    private AsaasCobrancaResponse gerarCobranca(Long idVenda, String billingType) {
        log.info("=== Gerando cobrança {} para venda ID: {} ===", billingType, idVenda);

        // 1. Buscar a venda
        Optional<VendaCompraLojaVirtual> vendaOpt = vendaRepository.findById(idVenda);
        if (vendaOpt.isEmpty()) {
            throw new RuntimeException("Venda não encontrada com ID: " + idVenda);
        }

        VendaCompraLojaVirtual venda = vendaOpt.get();

        // 2. Obter ou criar cliente no Asaas
        String cpfLimpo = venda.getPessoa().getCpf().replaceAll("\\D", "");
        String asaasCustomerId = obterCustomerId(cpfLimpo, venda.getPessoa());

        // 3. Montar requisição de cobrança
        AsaasCobrancaRequest request = AsaasCobrancaRequest.builder()
                .customer(asaasCustomerId)
                .billingType(billingType)
                .value(venda.getValorTotal().doubleValue())
                .dueDate(LocalDate.now().plusDays(3))  // Vencimento em 3 dias
                .description("Pagamento da Venda #" + venda.getId())
                .externalReference(venda.getId().toString())
                .daysDueDate(3)
                .build();

        // 4. Criar cobrança no Asaas
        AsaasCobrancaResponse resposta = asaasCobrancaService.criarCobranca(request);

        // 5. Atualizar venda
        venda.setPagamentoId(resposta.getId());
        venda.setStatusPagamento(resposta.getStatus());
        vendaRepository.save(venda);

        log.info("Cobrança {} gerada com sucesso. ID: {}, Status: {}", billingType, resposta.getId(), resposta.getStatus());

        return resposta;
    }

    private String obterCustomerId(String cpfLimpo, Pessoa pessoa) {
        // Verificar se já tem AsaasId
        if (pessoa.getAsaasId() != null && !pessoa.getAsaasId().isEmpty()) {
            return pessoa.getAsaasId();
        }

        // Buscar por CPF
        var clientePorCpfOpt = asaasClienteService.buscarClientePorCpfCnpj(cpfLimpo);
        if (clientePorCpfOpt.isPresent()) {
            String asaasId = clientePorCpfOpt.get().getId();
            pessoa.setAsaasId(asaasId);
            return asaasId;
        }

        // Buscar por email
        var clientePorEmailOpt = asaasClienteService.buscarClientePorEmail(pessoa.getEmail());
        if (clientePorEmailOpt.isPresent()) {
            String asaasId = clientePorEmailOpt.get().getId();
            pessoa.setAsaasId(asaasId);
            return asaasId;
        }

        // Criar novo cliente
        var novoCliente = asaasClienteService.criarCliente(pessoa);
        return novoCliente.getId();
    }
}
