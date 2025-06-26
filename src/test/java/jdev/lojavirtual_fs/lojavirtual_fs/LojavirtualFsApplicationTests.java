package jdev.lojavirtual_fs.lojavirtual_fs;

import jdev.lojavirtual_fs.lojavirtual_fs.controller.AcessoController;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Acesso;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.AcessoRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.AcessoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = LojavirtualFsApplication.class)
public class LojavirtualFsApplicationTests {

	//@Autowired
	//private AcessoService acessoService;//injeção de dependencia
	//@Autowired
	//private AcessoRepository acessoRepository;

	@Autowired
	private AcessoController acessoController;

	//Teste persistencia Repository, somente teste
	@Autowired
	private AcessoRepository acessoRepository;
	@Test
	public void testCadastraAcesso() {
		Acesso acesso = new Acesso();
		acesso.setDescricao("ROLE_ADMIN_3");

		//acessoService.save(acesso);
		//acessoController.salvarAcesso(acesso);

		acesso = acessoController.salvarAcesso(acesso).getBody();

		assertEquals(true, acesso.getId() > 0);

		//Validar testes, o assert compara assertEquals(se valor verdadeiro, for igual á este valor verdadeiro)
		//Exemplo  assertEquals(true, acesso.getId() > 0);
		// no caso é verdade que acesso.getId() > 0, pq o test injetou e salvou no banco.
		// OUTRO EXEMPLO: assertEquals(ROLE_ADMIN_ASSERT, acesso.getDESCRICAO);


		/*Teste Carregamento com Repository*/
		Acesso acesso2 = acessoRepository.findById(acesso.getId()).get();
		assertEquals(acesso.getId(), acesso2.getId());

		/*Teste Query do Repository*/
		acesso = new Acesso();
		acesso.setDescricao("ROLE_ADMIN_3");
		acesso = acessoController.salvarAcesso(acesso).getBody();
		List<Acesso> acessos = acessoRepository.buscarAcessoDesc("QUERYS".trim().toUpperCase());
		assertEquals(1, acessos.size());

		acessoRepository.deleteById(acesso.getId());
		acessoRepository.flush();
	}

}
