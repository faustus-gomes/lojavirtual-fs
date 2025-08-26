package jdev.lojavirtual_fs.lojavirtual_fs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jdev.lojavirtual_fs.lojavirtual_fs.controller.AcessoController;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Acesso;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.AcessoRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.service.AcessoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@Profile("dev")
@SpringBootTest(classes = LojavirtualFsApplication.class)
//@WebMvcTest(AcessoController.class)
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

	@Autowired
	private WebApplicationContext wac;

	// Teste EndPoint SalvarAcesso
	@Test
	public void testRestApiCadastroAcesso() throws Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();

		Acesso acesso = new Acesso();

		acesso.setDescricao("ROLE_COMPRADOR");

		ObjectMapper objectMapper = new ObjectMapper();
		ResultActions retornoApi = mockMvc
				.perform(MockMvcRequestBuilders.post("/salvarAcesso")
						.content(objectMapper.writeValueAsString(acesso))
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON));

		System.out.println("Retorno da API" + retornoApi.andReturn().getResponse().getContentAsString());

		/*Converter o retorno da API para objeto de acesso*/
		Acesso objetoRetorno = objectMapper.
				readValue(retornoApi.andReturn().getResponse().getContentAsString(),
						Acesso.class);

		assertEquals(acesso.getDescricao(), objetoRetorno.getDescricao());
	}


	@Test
	public void testRestApiDeleteAcesso() throws Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();

		Acesso acesso = new Acesso();

		acesso.setDescricao("ROLE_TESTE_DELETE");

		acesso = acessoRepository.save(acesso);

		ObjectMapper objectMapper = new ObjectMapper();
		ResultActions retornoApi = mockMvc
				.perform(MockMvcRequestBuilders.post("/deleteAcesso")
						.content(objectMapper.writeValueAsString(acesso))
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON));

		System.out.println("Retorno da API" + retornoApi.andReturn().getResponse().getContentAsString());
		System.out.println("Status de retorno: " + retornoApi.andReturn().getResponse().getStatus());

		//Testes que podemos fazer
		assertEquals("Acesso Removido", retornoApi.andReturn().getResponse().getContentAsString());
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
		/*Converter o retorno da API para objeto de acesso*/
		/*Acesso objetoRetorno = objectMapper.
				readValue(retornoApi.andReturn().getResponse().getContentAsString(),
						Acesso.class);

		assertEquals(acesso.getDescricao(), objetoRetorno.getDescricao());*/
	}

	@Test
	public void testRestApiDeletePorIDAcesso() throws Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();

		Acesso acesso = new Acesso();

		acesso.setDescricao("ROLE_TESTE_DELETE_ID");

		acesso = acessoRepository.save(acesso);

		ObjectMapper objectMapper = new ObjectMapper();
		ResultActions retornoApi = mockMvc
				.perform(MockMvcRequestBuilders.delete("/deleteAcessoPorId/" + acesso.getId())
						.content(objectMapper.writeValueAsString(acesso))
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON));

		System.out.println("Retorno da API por Id " + retornoApi.andReturn().getResponse().getContentAsString());
		System.out.println("Status de retorno: " + retornoApi.andReturn().getResponse().getStatus());

		//Testes que podemos fazer
		assertEquals("Acesso Removido", retornoApi.andReturn().getResponse().getContentAsString());
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());

	}

	@Test
	public void testRestApiObterAcessoID() throws Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();

		Acesso acesso = new Acesso();

		acesso.setDescricao("ROLE_OBTER_ID");

		acesso = acessoRepository.save(acesso);

		ObjectMapper objectMapper = new ObjectMapper();
		ResultActions retornoApi = mockMvc
				.perform(MockMvcRequestBuilders.get("/obterAcesso/" + acesso.getId())
						.content(objectMapper.writeValueAsString(acesso))
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON));

		//Testes que podemos fazer
		//assertEquals("Consulta Acesso ", retornoApi.andReturn().getResponse().getContentAsString());
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());

		Acesso acessoRetorno =  objectMapper.readValue(retornoApi.andReturn().getResponse().getContentAsString(), Acesso.class);


		assertEquals(acesso.getDescricao(), acessoRetorno.getDescricao());
	}

	@Test
	public void testRestApiObterAcessoDesc() throws Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();

		Acesso acesso = new Acesso();

		acesso.setDescricao("ROLE_OBTER_LIST");

		acesso = acessoRepository.save(acesso);

		ObjectMapper objectMapper = new ObjectMapper();
		ResultActions retornoApi = mockMvc
				.perform(MockMvcRequestBuilders.get("/buscarPorDesc/OBTER_LIST")
						.content(objectMapper.writeValueAsString(acesso))
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON));

		//Testes que podemos fazer
		//assertEquals("Consulta Acesso ", retornoApi.andReturn().getResponse().getContentAsString());
		assertEquals(200, retornoApi.andReturn().getResponse().getStatus());

		List<Acesso> retornoApiList = objectMapper.
								readValue(retornoApi.andReturn().
										getResponse().getContentAsString(),
										new TypeReference<List<Acesso>>() {});

		assertEquals(1, retornoApiList.size());


		assertEquals(acesso.getDescricao(), retornoApiList.get(0).getDescricao());

		acessoRepository.deleteById(acesso.getId());

	}


	@Test
    public void testarSenhaBCryptSimples2() throws Exception {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		String senhaOriginal = "teste123";
		String senhaBCryptBanco = "$2a$10$rIW80WtgxGVqbfdKIU26B.7CO2psTUukdKbCdJrk4bFkc943cfVlu";
		//"$2a$10$rDkPvvAFV8kqwvKJzwlRv.i.q.wz1w1pz0bFX5XYz0t6pXORh2Nua";

		boolean matches = passwordEncoder.matches(senhaOriginal, senhaBCryptBanco);

		System.out.println("Resultado " + matches);
		assertTrue(matches);

		String novoHash = passwordEncoder.encode("teste123");
		System.out.println("Use nova senha, caso queira: " + novoHash);
	}
	//@Autowired
	//private MockMvc mockMvc;

	//@MockBean
	//private AcessoService acessoService;
	//@Autowired
	//private WebApplicationContext wac;
	/*@Test
	public void testRestApiCadastroAcesso() throws JsonProcessingException, Exception {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		MockMvc mockMvc = builder.build();

		Acesso acesso = new Acesso();

		acesso.setDescricao("ROLE_COMPRADOR");

		ObjectMapper objectMapper = new ObjectMapper();

		ResultActions retornoApi = mockMvc
				.perform(MockMvcRequestBuilders.post("/salvarAcesso")
				.content(objectMapper.writeValueAsString(acesso))
				.accept(MediaType.APPLICATION_JSON));
	}*/
	/*@Test
	public void testCadastraAcesso() {
		Acesso acesso = new Acesso();
		acesso.setDescricao("ROLE_ADMIN_NOVO2");

		//acessoService.save(acesso);
		//acessoController.salvarAcesso(acesso);

		acesso = acessoController.salvarAcesso(acesso).getBody();

		assertEquals(true, acesso.getId() > 0);

		//Validar testes, o assert compara assertEquals(se valor verdadeiro, for igual á este valor verdadeiro)
		//Exemplo  assertEquals(true, acesso.getId() > 0);
		// no caso é verdade que acesso.getId() > 0, pq o test injetou e salvou no banco.
		// OUTRO EXEMPLO: assertEquals(ROLE_ADMIN_ASSERT, acesso.getDESCRICAO);


		//este Carregamento com Repository
		Acesso acesso2 = acessoRepository.findById(acesso.getId()).get();
		assertEquals(acesso.getId(), acesso2.getId());

		//Teste Query do Repository
		acesso = new Acesso();
		acesso.setDescricao("ROLE_ADMIN_NOVO");
		acesso = acessoController.salvarAcesso(acesso).getBody();
		List<Acesso> acessos = acessoRepository.buscarAcessoDesc("QUERYS".trim().toUpperCase());
		assertEquals(1, acessos.size());

		acessoRepository.deleteById(acesso.getId());
		acessoRepository.flush();
	}*/


}
