package jdev.lojavirtual_fs.lojavirtual_fs.service;

import jdev.lojavirtual_fs.lojavirtual_fs.model.Usuario;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component //para o Java 17 ou superior,  pode tirar o component, pois já está embutido
@Service
public class TarefaAutomatizadaService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServiceSendEmail serviceSendEmail;

    @Scheduled(initialDelay = 2000, fixedDelay = 86400000)  /*Roda a cada 24 horas para este teste, é melhor*/
    //@Scheduled(cron = "0 0 11 * * *", zone = "America/Sao_Paulo") /*Rodará as 11 horas da manhã no horário de SP*/
    public void notificarUserTrocaSenha() throws InterruptedException {

        List<Usuario> usuarios = usuarioRepository.usuarioSenhaVencida();

        for (Usuario usuario : usuarios) {

            StringBuilder msg = new StringBuilder();
            msg.append("Olá, ").append(usuario.getPessoa().getNome()).append("<br/>");
            msg.append("Está na horá de trocar a senha, já se passaram 10 dias de validade.").append("<br/>");
            msg.append("Troque sua senha, Loja Virtual - FaustusGomeesDev");

            serviceSendEmail.enviarEmailHtml("Troca de Senha", msg.toString(), usuario.getLogin());

            Thread.sleep(3000);
        }

    }

}
