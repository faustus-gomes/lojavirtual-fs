package jdev.lojavirtual_fs.lojavirtual_fs;

import jdev.lojavirtual_fs.lojavirtual_fs.repository.AcessoRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.AcessoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = LojavirtualFsApplication.class)
public class LojavirtualFsApplicationTests {

	@Autowired
	private AcessoService acessoService;
	@Autowired
	private AcessoRepository acessoRepository;
	@Test
	public void testCadastraAcesso() {
	}

}
