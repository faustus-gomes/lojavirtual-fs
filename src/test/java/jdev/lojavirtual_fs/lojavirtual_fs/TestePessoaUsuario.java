package jdev.lojavirtual_fs.lojavirtual_fs;

import jdev.lojavirtual_fs.lojavirtual_fs.controller.PessoaController;
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
        pessoaJuridica.setNome("Netgom Empresa");
        pessoaJuridica.setEmail("faustus.gomes@gmail.com");
        pessoaJuridica.setTelefone("1999995789");
        pessoaJuridica.setInscEstadual("000001111");
        pessoaJuridica.setInscMunicipal("656566666");
        pessoaJuridica.setNomeFantasia("Netgom - Desenvolvimento");
        pessoaJuridica.setRazaoSocial("128973246478");

        pessoaController.salvarPj(pessoaJuridica);

        /*PessoaFisica pessoaFisica = new PessoaFisica();

        pessoaFisica.setCpf("23356756478");
        pessoaFisica.setNome("Fausto Pereira Gomes");
        pessoaFisica.setEmail("faustus.gomes@gmail.com");
        pessoaFisica.setTelefone("1999995789");
        pessoaFisica.setEmpresa(pessoaFisica);*/
    }
}
