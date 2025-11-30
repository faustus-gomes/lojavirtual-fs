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

@SpringBootTest(classes = LojavirtualFsApplication.class)
public class TestePessoaUsuario {
    @Autowired
    private PessoaController pessoaController;

    @Test
    public void testCadPessoaFisica() throws ExceptionLoja {
        PessoaJuridica pessoaJuridica = new PessoaJuridica();

        pessoaJuridica.setCnpj("" + Calendar.getInstance().getTimeInMillis());
        pessoaJuridica.setNome("Netgom - Dev-Saúde");
        pessoaJuridica.setEmail("gomfape@yahoo.com.br");
        pessoaJuridica.setTelefone("1999995789");
        pessoaJuridica.setInscEstadual("000001111");
        pessoaJuridica.setInscMunicipal("656566666");
        pessoaJuridica.setNomeFantasia("Netgom - Desenvolvimento_teste");
        pessoaJuridica.setRazaoSocial("128973246487");

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

        /*PessoaFisica pessoaFisica = new PessoaFisica();

        pessoaFisica.setCpf("23356756478");
        pessoaFisica.setNome("Fausto Pereira Gomes");
        pessoaFisica.setEmail("faustus.gomes@gmail.com");
        pessoaFisica.setTelefone("1999995789");
        pessoaFisica.setEmpresa(pessoaFisica);*/
    }
}
