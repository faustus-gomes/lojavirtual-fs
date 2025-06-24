package jdev.lojavirtual_fs.lojavirtual_fs;

import jdev.lojavirtual_fs.lojavirtual_fs.controller.AcessoController;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Acesso;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.AcessoRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.AcessoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = LojavirtualFsApplication.class)
public class LojavirtualFsApplicationTests {

	//@Autowired
	//private AcessoService acessoService;//injeção de dependencia
	//@Autowired
	//private AcessoRepository acessoRepository;

	@Autowired
	private AcessoController acessoController;
	@Test
	public void testCadastraAcesso() {
		Acesso acesso = new Acesso();
		acesso.setDescricao("ROLE_ADMIN");

		//acessoService.save(acesso);
		acessoController.salvarAcesso(acesso);
	}

}
