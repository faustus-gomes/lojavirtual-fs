package jdev.lojavirtual_fs.lojavirtual_fs;

import jdev.lojavirtual_fs.lojavirtual_fs.controller.PessoaController;
import jdev.lojavirtual_fs.lojavirtual_fs.enums.TipoEndereco;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Endereco;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaFisica;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaJuridica;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.PessoaRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.PessoaUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.List;

@SpringBootTest(classes = LojavirtualFsApplication.class)
public class TestePessoaUsuario {
    @Autowired
    private PessoaController pessoaController;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Test
    public void testCadJuridica() throws ExceptionLoja, InterruptedException {
        PessoaJuridica pessoaJuridica = new PessoaJuridica();

        pessoaJuridica.setCnpj("59078600000115" );//+ Calendar.getInstance().getTimeInMillis());
        pessoaJuridica.setNome("Netgom - Dev-Saúde");
        pessoaJuridica.setEmail("gomfape@yahoo.com.br");
        pessoaJuridica.setTelefone("1999995789");
        pessoaJuridica.setInscEstadual("000001118");
        pessoaJuridica.setInscMunicipal("656566689");
        pessoaJuridica.setNomeFantasia("Netgom - Desenvolvimento_teste");
        pessoaJuridica.setRazaoSocial("128973246487-Teste");

        Endereco endereco1 = new Endereco();
        endereco1.setBairro("Jd Independencia");
        endereco1.setCep("13082567");
        endereco1.setComplemento("Perto Mercadinho");
        endereco1.setEmpresa(pessoaJuridica);
        endereco1.setNumero("234");
        endereco1.setPessoa(pessoaJuridica);
        endereco1.setRuaLogra("Rua das Hortências");
        endereco1.setTipoEndereco(TipoEndereco.COBRANCA);
        endereco1.setCidade("Campinas");
        endereco1.setUf("SP");

        Endereco endereco2 = new Endereco();
        endereco2.setBairro("Jd Sta Isabel");
        endereco2.setCep("13082598");
        endereco2.setComplemento("Próx. Igreja");
        endereco2.setEmpresa(pessoaJuridica);
        endereco2.setNumero("1041");
        endereco2.setPessoa(pessoaJuridica);
        endereco2.setRuaLogra("Rua 13");
        endereco2.setTipoEndereco(TipoEndereco.ENTREGA);
        endereco2.setCidade("Campinas");
        endereco2.setUf("SP");

        pessoaJuridica.getEnderecos().add(endereco2);
        pessoaJuridica.getEnderecos().add(endereco1);

        pessoaController.salvarPj(pessoaJuridica);

        Thread.sleep(3000);
    }

    @Test
    public void testCadFisica() throws ExceptionLoja, InterruptedException {
        // Agora retorna List, então pegue o primeiro se existir
        List<PessoaJuridica> pjs = pessoaRepository.existeCnpjCadastrado("59078600000115");
        PessoaJuridica pessoaJuridica = null;
        if (pjs != null && !pjs.isEmpty()) {
            pessoaJuridica = pjs.get(0); // Pega o primeiro da lista
            System.out.println("PJ encontrada: " + pessoaJuridica.getNome());
        } else {
            System.out.println("PJ com CNPJ 59078600000115 não encontrada. Criando uma...");
            // Crie uma PJ se não existir
            pessoaJuridica = new PessoaJuridica();
            pessoaJuridica.setCnpj("69695249000165");
            pessoaJuridica.setNome("TRIOTECH");
            pessoaJuridica.setEmail("empresa.testepf@teste.com");
            pessoaJuridica.setInscEstadual("IE_" + System.currentTimeMillis());
            pessoaJuridica = pessoaRepository.save(pessoaJuridica);
        }

        PessoaFisica pessoaFisica = new PessoaFisica();

        pessoaFisica.setCpf("497.252.688-22");
        pessoaFisica.setNome("João V. Candido Costa");
        pessoaFisica.setEmail("joao.costa@saudebeneficencia.com.br");
        pessoaFisica.setTelefone("1999995789");
        pessoaFisica.setEmpresa(pessoaJuridica);

        Endereco endereco1 = new Endereco();
        endereco1.setBairro("Jd Independencia");
        endereco1.setCep("13082567");
        endereco1.setComplemento("Perto Mercadinho");
        //endereco1.setEmpresa(pessoaFisica);
        endereco1.setNumero("234");
        endereco1.setPessoa(pessoaFisica);
        endereco1.setRuaLogra("Rua das Hortências");
        endereco1.setTipoEndereco(TipoEndereco.COBRANCA);
        endereco1.setCidade("Campinas");
        endereco1.setUf("SP");
        endereco1.setEmpresa(pessoaJuridica);

        Endereco endereco2 = new Endereco();
        endereco2.setBairro("Jd Sta Isabel");
        endereco2.setCep("13082598");
        endereco2.setComplemento("Próx. Igreja");
        //endereco2.setEmpresa(pessoaFisica);
        endereco2.setNumero("1041");
        endereco2.setPessoa(pessoaFisica);
        endereco2.setRuaLogra("Rua 13");
        endereco2.setTipoEndereco(TipoEndereco.ENTREGA);
        endereco2.setCidade("Campinas");
        endereco2.setUf("SP");
        endereco2.setEmpresa(pessoaJuridica);

        pessoaFisica.getEnderecos().add(endereco2);
        pessoaFisica.getEnderecos().add(endereco1);

        pessoaController.salvarPf(pessoaFisica);

        Thread.sleep(3000);
    }
}
