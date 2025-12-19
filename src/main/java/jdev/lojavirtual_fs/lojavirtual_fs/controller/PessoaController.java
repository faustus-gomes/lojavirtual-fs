package jdev.lojavirtual_fs.lojavirtual_fs.controller;

import jakarta.validation.Valid;
import jdev.lojavirtual_fs.lojavirtual_fs.ExceptionLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.CepDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.ConsultaCnpjDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.enums.TipoPessoa;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Endereco;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaFisica;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaJuridica;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.EnderecoReposity;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.PessoaFisicaRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.PessoaRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.ContagemAcessoApiService;
import jdev.lojavirtual_fs.lojavirtual_fs.service.PessoaUserService;
import jdev.lojavirtual_fs.lojavirtual_fs.util.ValidaCNPJ;
import jdev.lojavirtual_fs.lojavirtual_fs.util.ValidaCPF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Controller
@RestController
public class PessoaController {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaUserService pessoaUserService;

    @Autowired
    private EnderecoReposity enderecoReposity;

    @Autowired
    private PessoaFisicaRepository pessoaFisicaRepository;

    @Autowired
    private ContagemAcessoApiService contagemAcessoApiService;

    @ResponseBody
    @GetMapping(value = "/consultaPFNome/{nome}")
    public  ResponseEntity<List<PessoaFisica>> consultaPFNome(@PathVariable("nome") String nome) {
        List<PessoaFisica> fisicas = pessoaFisicaRepository.pesquisaPorNomePF(nome.trim().toUpperCase());
        //contagemAcessoApiService.atualizaAcessoEndPointPF();
        return new ResponseEntity<List<PessoaFisica>>(fisicas, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/consultaCPF/{cpf}")
    public  ResponseEntity<List<PessoaFisica>> consultaCPF(@PathVariable("cpf") String cpf) {
        List<PessoaFisica> fisicas = pessoaFisicaRepository.pesquisaPorCPF(cpf);

        return new ResponseEntity<List<PessoaFisica>>(fisicas, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/consultaPorNomePJ/{nome}")
    public  ResponseEntity<List<PessoaJuridica>> consultaPorNomePJ(@PathVariable("nome") String nome) {
        List<PessoaJuridica> juridico = pessoaRepository.pesquisaPorNomePJ(nome.trim().toUpperCase());

        return new ResponseEntity<List<PessoaJuridica>>(juridico, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/consultaPorCNPJ/{cnpj}")
    public  ResponseEntity<List<PessoaJuridica>> consultaPorCNPJ(@PathVariable("cnpj") String cnpj) {
        List<PessoaJuridica> juridico = pessoaRepository.existeCnpjCadastrado(cnpj);

        return new ResponseEntity<List<PessoaJuridica>>(juridico, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/consultaCep/{cep}")
    public ResponseEntity<CepDTO> consultaCep(@PathVariable("cep") String cep) {
        CepDTO cepDTO = pessoaUserService.consultaCep(cep);

        return new ResponseEntity<CepDTO>(cepDTO, HttpStatus.OK);
    }

    @ResponseBody
    @GetMapping(value = "/consultaCnpjReceitaWS/{cnpj}")
    public ResponseEntity<ConsultaCnpjDTO> consultaCnpjReceitaWS(@PathVariable("cnpj") String cnpj) {
        ConsultaCnpjDTO consultaCnpjDTO = pessoaUserService.consultaCnpjReceitaWS(cnpj);

        return new ResponseEntity<ConsultaCnpjDTO>(consultaCnpjDTO, HttpStatus.OK);
    }

    //@ResponseBody
    @PostMapping(value = "/salvarPj")
    public ResponseEntity<PessoaJuridica> salvarPj(@RequestBody @Valid PessoaJuridica pessoaJuridica) throws ExceptionLoja {
        System.out.println("=== SALVAR PJ CHAMADO ===");
        System.out.println("PessoaJuridica: " + pessoaJuridica);
        System.out.println("É null? " + (pessoaJuridica == null));

        if (pessoaJuridica == null) {
            System.out.println("=== LANÇANDO EXCEÇÃO ===");
            throw new ExceptionLoja("Pessoa Jurídica não pode ser Null. É necessário enviar um JSON no corpo da requisição.");
        }

        // Validações de campos obrigatórios
        // Adicionei o Validation na ModelPessoa
        /*if (pessoaJuridica.getCnpj() == null || pessoaJuridica.getCnpj().trim().isEmpty()) {
            throw new ExceptionLoja("CNPJ é obrigatório");
        }*/

        if (pessoaJuridica.getNome() == null || pessoaJuridica.getNome().trim().isEmpty()) {
            throw new ExceptionLoja("Nome é obrigatório");
        }

        if (pessoaJuridica.getEmail() == null || pessoaJuridica.getEmail().trim().isEmpty()) {
            throw new ExceptionLoja("Email é obrigatório");
        }

        // Crie uma variável FINAL para usar no lambda
        final Long idAtualPJ = pessoaJuridica.getId();
        final String cnpjAtual = pessoaJuridica.getCnpj();
        final String ieAtual = pessoaJuridica.getInscEstadual();

        // VALIDAÇÃO CNPJ (AGORA COM LIST)
        List<PessoaJuridica> pessoasComMesmoCNPJ = pessoaRepository.existeCnpjCadastrado(cnpjAtual);
        if (pessoasComMesmoCNPJ != null && !pessoasComMesmoCNPJ.isEmpty()) {
            // Para INSERT (ID null)
            if (idAtualPJ == null) {
                throw new ExceptionLoja("Já existe pessoa cadastrada com este CNPJ " + cnpjAtual);
            }

            // Para UPDATE (ID não null) - use a variável final idAtualPJ
            boolean existeOutroComMesmoCNPJ = pessoasComMesmoCNPJ.stream()
                    .anyMatch(p -> p.getId() != null && !p.getId().equals(idAtualPJ));

            if (existeOutroComMesmoCNPJ) {
                throw new ExceptionLoja("CNPJ já cadastrado em outra empresa");
            }
        }

        if (pessoaJuridica.getTipoPessoa() == null) {
              throw new ExceptionLoja("Informe o tipo Jurídico ou Fornecedor");
        }

        // VALIDAÇÃO INSCRIÇÃO ESTADUAL (AGORA COM LIST)
        List<PessoaJuridica> pessoasComMesmaIE = pessoaRepository.existeInsEstadualCadastrado(ieAtual);
        if (pessoasComMesmaIE != null && !pessoasComMesmaIE.isEmpty()) {
            // Para INSERT (ID null)
            if (idAtualPJ == null) {
                throw new ExceptionLoja("Já existe Inscrição Estadual cadastrada com o número " + ieAtual);
            }

            // Para UPDATE (ID não null) - use a variável final idAtualPJ
            boolean existeOutroComMesmaIE = pessoasComMesmaIE.stream()
                    .anyMatch(p -> p.getId() != null && !p.getId().equals(idAtualPJ));

            if (existeOutroComMesmaIE) {
                throw new ExceptionLoja("Inscrição Estadual já cadastrada em outra empresa");
            }
        }

        if (!ValidaCNPJ.isCNPJ(pessoaJuridica.getCnpj())) {
           throw new ExceptionLoja("CNPJ : " + pessoaJuridica.getCnpj() + " é inválido.");
        }
        //Consulta CEP
        if (pessoaJuridica.getId() == null || pessoaJuridica.getId() <= 0)  {
            for (int p = 0; p < pessoaJuridica.getEnderecos().size(); p++) {
                CepDTO cepDTO = pessoaUserService.consultaCep(pessoaJuridica.getEnderecos().get(p).getCep());

                pessoaJuridica.getEnderecos().get(p).setBairro(cepDTO.getBairro());
                pessoaJuridica.getEnderecos().get(p).setCidade(cepDTO.getLocalidade());
                pessoaJuridica.getEnderecos().get(p).setComplemento(cepDTO.getComplemento());
                pessoaJuridica.getEnderecos().get(p).setRuaLogra(cepDTO.getLogradouro());
                pessoaJuridica.getEnderecos().get(p).setUf(cepDTO.getUf());
            }

        } else { // Ser este Else para atualizar quando o PJ trocar o Endereó
            for (int p = 0; p < pessoaJuridica.getEnderecos().size(); p++) {
                Endereco enderecoTemp = enderecoReposity.findById(pessoaJuridica.getEnderecos().get(p).getId()).get();

                if (!enderecoTemp.getCep().equals(pessoaJuridica.getEnderecos().get(p).getCep())) {
                    CepDTO cepDTO = pessoaUserService.consultaCep(pessoaJuridica.getEnderecos().get(p).getCep());

                    pessoaJuridica.getEnderecos().get(p).setBairro(cepDTO.getBairro());
                    pessoaJuridica.getEnderecos().get(p).setCidade(cepDTO.getLocalidade());
                    pessoaJuridica.getEnderecos().get(p).setComplemento(cepDTO.getComplemento());
                    pessoaJuridica.getEnderecos().get(p).setRuaLogra(cepDTO.getLogradouro());
                    pessoaJuridica.getEnderecos().get(p).setUf(cepDTO.getUf());
                }
            }
        }

        pessoaJuridica = pessoaUserService.salvarPessoaJuridica(pessoaJuridica);

        return new ResponseEntity<PessoaJuridica>(pessoaJuridica, HttpStatus.CREATED);
    }


    @PostMapping(value = "/salvarPf")
    public ResponseEntity<PessoaFisica> salvarPf(@RequestBody @Valid PessoaFisica pessoaFisica) throws ExceptionLoja {
        System.out.println("=== SALVAR PF CHAMADO ===");
        System.out.println("PessoaFisica: " + pessoaFisica);
        System.out.println("É null? " + (pessoaFisica == null));

        if (pessoaFisica == null) {
            throw new ExceptionLoja("Pessoa Física não pode ser Null.");
        }

        // Validações de campos obrigatórios
        if (pessoaFisica.getCpf() == null || pessoaFisica.getCpf().trim().isEmpty()) {
            throw new ExceptionLoja("CPF é obrigatório");
        }

        if (pessoaFisica.getNome() == null || pessoaFisica.getNome().trim().isEmpty()) {
            throw new ExceptionLoja("Nome é obrigatório");
        }

        if (pessoaFisica.getEmail() == null || pessoaFisica.getEmail().trim().isEmpty()) {
            throw new ExceptionLoja("Email é obrigatório");
        }

        if (pessoaFisica.getTipoPessoa() == null) {
            pessoaFisica.setTipoPessoa(TipoPessoa.FISICA.name());
        }

        // Crie variáveis FINAL para usar no lambda
        final Long idAtualPF = pessoaFisica.getId();
        final String cpfAtual = pessoaFisica.getCpf();

        // VALIDAÇÃO CPF (AGORA COM LIST)
        List<PessoaFisica> pessoasComMesmoCPF = pessoaRepository.existeCpfCadastrado(cpfAtual);
        if (pessoasComMesmoCPF != null && !pessoasComMesmoCPF.isEmpty()) {
            // Para INSERT (ID null)
            if (idAtualPF == null) {
                throw new ExceptionLoja("Já existe pessoa cadastrada com este CPF " + cpfAtual);
            }

            // Para UPDATE (ID não null) - use a variável final idAtualPF
            boolean existeOutroComMesmoCPF = pessoasComMesmoCPF.stream()
                    .anyMatch(p -> p.getId() != null && !p.getId().equals(idAtualPF));

            if (existeOutroComMesmoCPF) {
                throw new ExceptionLoja("CPF já cadastrado para outra pessoa");
            }
        }

        if (!ValidaCPF.isCPF(cpfAtual)) {
            throw new ExceptionLoja("CPF : " + cpfAtual + " é inválido.");
        }

        pessoaFisica = pessoaUserService.salvarPessoaFisica(pessoaFisica);

        return new ResponseEntity<PessoaFisica>(pessoaFisica, HttpStatus.CREATED);
    }
}
