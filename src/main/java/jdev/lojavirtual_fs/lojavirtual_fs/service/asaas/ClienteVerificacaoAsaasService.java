package jdev.lojavirtual_fs.lojavirtual_fs.service.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.AsaasClienteResponse;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Pessoa;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaFisica;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaJuridica;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.PessoaFisicaRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.PessoaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class ClienteVerificacaoAsaasService {
    @Autowired
    private PessoaFisicaRepository pessoaFisicaRepository;

    @Autowired
    private PessoaRepository pessoaJuridicaRepository;

    @Autowired
    private AsaasClienteService asaasClienteService;


    /**
     * 12.8 - Verifica se existe cliente na base local e sincroniza com Asaas
     */
    public Optional<Pessoa> verificarClienteExistente(String email, String documento) {
        log.info("Asaas - Verificando cliente existente - Email: {}, Documento: {}", email, documento);

        String docLimpo = documento != null ? documento.replaceAll("[^0-9]", "") : null;

        // Buscar por email ou documento
        Pessoa pessoa = buscarPessoa(email, docLimpo);

        if (pessoa != null) {
            log.info("Asaas - Cliente encontrado na base local: {} - Tipo: {}",
                    pessoa.getNome(), pessoa.getTipoPessoa());

            // Sincronizar com Asaas se necessário
            if (pessoa.getAsaasId() == null || pessoa.getAsaasId().isEmpty()) {
                sincronizarComAsaas(pessoa);
            }

            return Optional.of(pessoa);
        }

        log.info("Asaas - Cliente NÃO encontrado na base local");
        return Optional.empty();
    }

    private Pessoa buscarPessoa(String email, String documento) {
        // Busca por email
        if (email != null && !email.isEmpty()) {
            Optional<PessoaFisica> pf = pessoaFisicaRepository.findByEmail(email);
            if (pf.isPresent()) return pf.get();

            Optional<PessoaJuridica> pj = pessoaJuridicaRepository.findByEmail(email);
            if (pj.isPresent()) return pj.get();
        }

        // Busca por documento
        if (documento != null) {
            if (documento.length() <= 11) {
                List<PessoaFisica> pfList = pessoaFisicaRepository.pesquisaPorCPF(documento);
                if (!pfList.isEmpty()) return pfList.get(0);
            } else {
                List<PessoaJuridica> pjList = pessoaJuridicaRepository.existeCnpjCadastrado(documento);
                if (!pjList.isEmpty()) return pjList.get(0);
            }
        }

        return null;
    }

    private void sincronizarComAsaas(Pessoa pessoa) {
        try {
            Optional<AsaasClienteResponse> asaasCliente =
                    asaasClienteService.buscarClientePorEmail(pessoa.getEmail());

            if (asaasCliente.isPresent()) {
                pessoa.setAsaasId(asaasCliente.get().getId());
                salvarPessoa(pessoa);
                log.info("Asaas - ID atualizado: {}", asaasCliente.get().getId());
            } else {
                AsaasClienteResponse novoCliente = asaasClienteService.criarCliente(pessoa);
                pessoa.setAsaasId(novoCliente.getId());
                salvarPessoa(pessoa);
                log.info("Asaas - Cliente criado com ID: {}", novoCliente.getId());
            }
        } catch (ExceptionLoja e) {  // ← Usando sua exceção
            log.error("Asaas - Erro na sincronização: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Asaas - Erro inesperado na sincronização: {}", e.getMessage());
        }
    }

    private void salvarPessoa(Pessoa pessoa) {
        if (pessoa instanceof PessoaFisica) {
            pessoaFisicaRepository.save((PessoaFisica) pessoa);
        } else if (pessoa instanceof PessoaJuridica) {
            pessoaJuridicaRepository.save((PessoaJuridica) pessoa);
        }
    }

    /**
     * 12.9 - Cadastrar cliente (somente para uso do Asaas)
     */
    public Pessoa cadastrarClienteAsaas(Pessoa pessoa) {
        log.info("Asaas - Cadastrando novo cliente: {}", pessoa.getEmail());

        // Verificar se já existe email
        Pessoa existente = buscarPessoa(pessoa.getEmail(), null);
        if (existente != null) {
            throw new ExceptionLoja("Cliente já cadastrado com este email");
        }

        // Validação: verificar se a empresa foi setada (para PF)
        if (pessoa instanceof PessoaFisica && pessoa.getEmpresa() == null) {
            throw new ExceptionLoja("Pessoa Física deve estar vinculada a uma empresa");
        }

        // Salvar localmente primeiro
        pessoa.setDataCadastro(new Date());
        if (pessoa instanceof PessoaFisica) {
            pessoa.setTipoPessoa("FISICA");
            pessoa = pessoaFisicaRepository.save((PessoaFisica) pessoa);
            log.info("Pessoa Física salva - ID: {}, Empresa ID: {}",
                    pessoa.getId(),
                    pessoa.getEmpresa() != null ? pessoa.getEmpresa().getId() : "null");
        } else if (pessoa instanceof PessoaJuridica) {
            pessoa.setTipoPessoa("JURIDICA");
            pessoa = pessoaJuridicaRepository.save((PessoaJuridica) pessoa);
            log.info("Pessoa Jurídica salva - ID: {}", pessoa.getId());
        }

        // Criar no Asaas
        try {
            AsaasClienteResponse asaasResponse = asaasClienteService.criarCliente(pessoa);
            pessoa.setAsaasId(asaasResponse.getId());
            salvarPessoa(pessoa);
            log.info("Asaas - Cliente cadastrado com sucesso. ID Asaas: {}", asaasResponse.getId());
        } catch (ExceptionLoja e) {
            log.error("Asaas - Cliente salvo localmente mas erro no Asaas: {}", e.getMessage());
            // Não relançamos a exceção para não perder o cadastro local
        }

        return pessoa;
    }

    /**
     * Método para obter ou criar Customer ID no Asaas
     * Usado pelo CheckoutPagamentoService
     */
    public String obterCustomerId(String cpfLimpo, Pessoa pessoa) {
        // 1. Verificar se pessoa já tem asaasId na base local
        if (pessoa.getAsaasId() != null && !pessoa.getAsaasId().isEmpty()) {
            log.info("Usando AsaasId existente na base local: {}", pessoa.getAsaasId());
            return pessoa.getAsaasId();
        }

        // 2. Buscar no Asaas por CPF (vamos tentar primeiro por CPF)
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

        // 3. Buscar no Asaas por email
        var clientePorEmailOpt = asaasClienteService.buscarClientePorEmail(pessoa.getEmail());

        if (clientePorEmailOpt.isPresent()) {
            String asaasId = clientePorEmailOpt.get().getId();
            log.info("Cliente encontrado no Asaas por email. ID: {}", asaasId);
            pessoa.setAsaasId(asaasId);
            if (pessoa instanceof PessoaFisica) {
                pessoaFisicaRepository.save((PessoaFisica) pessoa);
            }
            return asaasId;
        }

        // 4. Criar novo cliente no Asaas
        log.info("Cliente não encontrado. Criando novo cliente no Asaas...");
        var novoCliente = asaasClienteService.criarCliente(pessoa);
        String asaasId = novoCliente.getId();

        pessoa.setAsaasId(asaasId);
        if (pessoa instanceof PessoaFisica) {
            pessoaFisicaRepository.save((PessoaFisica) pessoa);
        }

        log.info("Cliente criado com sucesso. AsaasId: {}", asaasId);
        return asaasId;
    }

}
