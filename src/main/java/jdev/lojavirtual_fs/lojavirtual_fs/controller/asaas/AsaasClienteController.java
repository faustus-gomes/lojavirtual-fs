package jdev.lojavirtual_fs.lojavirtual_fs.controller.asaas;

import jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas.PessoaRequest;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Pessoa;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaFisica;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaJuridica;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.PessoaFisicaRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.PessoaRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.asaas.ClienteVerificacaoAsaasService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/asaas/clientes")
@Slf4j
public class AsaasClienteController {
    @Autowired
    PessoaFisicaRepository pessoaFisicaRepository;
    @Autowired
    PessoaRepository pessoaJuridicaRepository;
    @Autowired
    private ClienteVerificacaoAsaasService clienteVerificacaoAsaasService;

    /**
     * 12.8 - Endpoint para verificar se cliente existe
     * GET /api/asaas/clientes/verificar?email=joao@email.com&documento=12345678900
     */
    @GetMapping("/verificar")
    public ResponseEntity<?> verificarCliente(
            @RequestParam String email,
            @RequestParam(required = false) String documento) {

        log.info("=== 12.8 - Verificando cliente existente ===");
        log.info("Email: {}, Documento: {}", email, documento);

        try {
            Optional<Pessoa> pessoaOpt = clienteVerificacaoAsaasService
                    .verificarClienteExistente(email, documento);

            if (pessoaOpt.isPresent()) {
                Pessoa pessoa = pessoaOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("existe", true);
                response.put("mensagem", "Cliente encontrado na base local");
                response.put("id", pessoa.getId());
                response.put("nome", pessoa.getNome());
                response.put("email", pessoa.getEmail());
                response.put("telefone", pessoa.getTelefone());
                response.put("tipoPessoa", pessoa.getTipoPessoa());
                response.put("asaasId", pessoa.getAsaasId());
                response.put("dataCadastro", pessoa.getDataCadastro());

                // Adiciona documento específico
                if (pessoa instanceof PessoaFisica) {
                    response.put("cpf", ((PessoaFisica) pessoa).getCpf());
                } else if (pessoa instanceof PessoaJuridica) {
                    response.put("cnpj", ((PessoaJuridica) pessoa).getCnpj());
                }

                log.info("Cliente encontrado: {} - AsaasId: {}", pessoa.getNome(), pessoa.getAsaasId());
                return ResponseEntity.ok(response);

            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("existe", false);
                response.put("mensagem", "Cliente não encontrado na base local. É necessário cadastrar.");
                response.put("email", email);
                if (documento != null) {
                    response.put("documento", documento);
                }

                log.info("Cliente não encontrado: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

        } catch (Exception e) {
            log.error("Erro ao verificar cliente: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("erro", "Erro ao verificar cliente: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 12.9 - Endpoint para cadastrar cliente (via Asaas)
     * POST /api/asaas/clientes/cadastrar
     */
    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarCliente(@RequestBody PessoaRequest request) {

        log.info("=== 12.9 - Cadastrando novo cliente via Asaas ===");
        log.info("Nome: {}, Email: {}", request.getNome(), request.getEmail());

        try {
            Pessoa pessoa;

            // Verifica o tipo de pessoa
            if ("FISICA".equals(request.getTipoPessoa())) {
                PessoaFisica pf = new PessoaFisica();
                pf.setNome(request.getNome());
                pf.setEmail(request.getEmail());
                pf.setTelefone(request.getTelefone());
                pf.setCpf(request.getCpf());
                pf.setDataNascimento(request.getDataNascimento());
                pf.setTipoPessoa("FISICA");
                pessoa = pf;
            } else if ("JURIDICA".equals(request.getTipoPessoa())) {
                PessoaJuridica pj = new PessoaJuridica();
                pj.setNome(request.getNome());
                pj.setEmail(request.getEmail());
                pj.setTelefone(request.getTelefone());
                pj.setCnpj(request.getCnpj());
                pj.setInscEstadual(request.getInscEstadual());
                pj.setTipoPessoa("JURIDICA");
                pessoa = pj;
            } else {
                return ResponseEntity.badRequest().body(Map.of("erro", "Tipo de pessoa deve ser FISICA ou JURIDICA"));
            }

            Pessoa novaPessoa = clienteVerificacaoAsaasService.cadastrarClienteAsaas(pessoa);

            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", true);
            response.put("mensagem", "Cliente cadastrado com sucesso");
            response.put("id", novaPessoa.getId());
            response.put("nome", novaPessoa.getNome());
            response.put("email", novaPessoa.getEmail());
            response.put("asaasId", novaPessoa.getAsaasId());
            response.put("tipoPessoa", novaPessoa.getTipoPessoa());

            log.info("Cliente cadastrado com sucesso. ID local: {}, AsaasID: {}",
                    novaPessoa.getId(), novaPessoa.getAsaasId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Erro ao cadastrar cliente: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Listar todos os clientes (PF e PJ)
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listarClientes() {
        try {
            List<PessoaFisica> pessoasFisicas = (List<PessoaFisica>) pessoaFisicaRepository.findAll();
            List<PessoaJuridica> pessoasJuridicas = (List<PessoaJuridica>) pessoaJuridicaRepository.findAll();

            Map<String, Object> response = new HashMap<>();
            response.put("pessoasFisicas", pessoasFisicas);
            response.put("pessoasJuridicas", pessoasJuridicas);
            response.put("total", pessoasFisicas.size() + pessoasJuridicas.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}
