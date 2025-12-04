package jdev.lojavirtual_fs.lojavirtual_fs.service;

import jakarta.mail.MessagingException;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaFisica;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaJuridica;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Usuario;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.PessoaFisicaRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.PessoaRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

@Service
public class PessoaUserService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaFisicaRepository pessoaFisicaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ServiceSendEmail serviceSendEmail;
    public PessoaJuridica salvarPessoaJuridica(PessoaJuridica juridica) {
        // PRIMEIRO: Configura os relacionamentos dos endereços ANTES de salvar
        for (int i = 0; i < juridica.getEnderecos().size(); i++) {
            juridica.getEnderecos().get(i).setPessoa(juridica);
            juridica.getEnderecos().get(i).setEmpresa(juridica);
        }
        // DEPOIS: Salva a pessoa jurídica com os relacionamentos já configurados
        juridica = pessoaRepository.save(juridica);

        Usuario usuarioPj = usuarioRepository.findByUserPessoa(juridica.getId(), juridica.getEmail());

        if (usuarioPj == null) {
            // remover a constraint que cria
            String constraint = usuarioRepository.consultaConstraintAcesso();
            System.out.println("Constraint: " + constraint);
            if (constraint != null){
                jdbcTemplate.execute("begin;" +
                        "alter table usuario_acesso drop constraint " +
                        constraint + "; commit;"
                );
                System.out.println("Constraint removida: " + constraint);
            }

                usuarioPj = new Usuario();
                usuarioPj.setDataAtualSenha(Calendar.getInstance().getTime());
                usuarioPj.setEmpresa(juridica);
                usuarioPj.setPessoa(juridica);
                usuarioPj.setLogin(juridica.getEmail());

                String senha = "" + Calendar.getInstance().getTimeInMillis();
                String senhaCript = new BCryptPasswordEncoder().encode(senha);

                usuarioPj.setSenha(senhaCript);
                usuarioPj = usuarioRepository.save(usuarioPj);

                usuarioRepository.insereAcessoUser(usuarioPj.getId());
                usuarioRepository.insereAcessoUserPj(usuarioPj.getId(), "ROLE_ADMIN");


                /*Fazer envio de e-mail do login e senha*/
                // Envio de e-mail com HTML correto
                StringBuilder mensagemHtml = new StringBuilder();
                mensagemHtml.append("<!DOCTYPE html>");
                mensagemHtml.append("<html>");
                mensagemHtml.append("<head><meta charset='UTF-8'></head>");
                mensagemHtml.append("<body>");
                mensagemHtml.append("<h2>Dados de Acesso - Loja Virtual</h2>");
                mensagemHtml.append("<p><b>Segue abaixo seus dados de acesso para a loja virtual:</b></p>");
                mensagemHtml.append("<p><b>Login:</b> ").append(juridica.getEmail()).append("</p>");
                mensagemHtml.append("<p><b>Senha:</b> ").append(senha).append("</p>");
                mensagemHtml.append("<p>Obrigado!</p>");
                mensagemHtml.append("</body>");
                mensagemHtml.append("</html>");
                try {
                    serviceSendEmail.enviarEmailHtml("Acesso Gerado para loja Virtual", mensagemHtml.toString(), juridica.getEmail());
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }

        return juridica;
    }

    public PessoaFisica salvarPessoaFisica(PessoaFisica pessoaFisica) {
        // PRIMEIRO: Configura os relacionamentos dos endereços ANTES de salvar
        for (int i = 0; i < pessoaFisica.getEnderecos().size(); i++) {
            pessoaFisica.getEnderecos().get(i).setPessoa(pessoaFisica);
            //pessoaFisica.getEnderecos().get(i).setEmpresa(pessoaFisica);
        }
        // DEPOIS: Salva a pessoa jurídica com os relacionamentos já configurados
        pessoaFisica = pessoaFisicaRepository.save(pessoaFisica);

        Usuario usuarioPj = usuarioRepository.findByUserPessoa(pessoaFisica.getId(), pessoaFisica.getEmail());

        if (usuarioPj == null) {
            // remover a constraint que cria
            String constraint = usuarioRepository.consultaConstraintAcesso();
            System.out.println("Constraint: " + constraint);
            if (constraint != null){
                jdbcTemplate.execute("begin;" +
                        "alter table usuario_acesso drop constraint " +
                        constraint + "; commit;"
                );
                System.out.println("Constraint removida: " + constraint);
            }

            usuarioPj = new Usuario();
            usuarioPj.setDataAtualSenha(Calendar.getInstance().getTime());
            usuarioPj.setEmpresa(pessoaFisica.getEmpresa());
            usuarioPj.setPessoa(pessoaFisica);
            usuarioPj.setLogin(pessoaFisica.getEmail());

            String senha = "" + Calendar.getInstance().getTimeInMillis();
            String senhaCript = new BCryptPasswordEncoder().encode(senha);

            usuarioPj.setSenha(senhaCript);
            usuarioPj = usuarioRepository.save(usuarioPj);

            usuarioRepository.insereAcessoUser(usuarioPj.getId());

            /*Fazer envio de e-mail do login e senha*/
            // Envio de e-mail com HTML correto
            StringBuilder mensagemHtml = new StringBuilder();
            mensagemHtml.append("<!DOCTYPE html>");
            mensagemHtml.append("<html>");
            mensagemHtml.append("<head><meta charset='UTF-8'></head>");
            mensagemHtml.append("<body>");
            mensagemHtml.append("<h2>Dados de Acesso - Loja Virtual</h2>");
            mensagemHtml.append("<p><b>Segue abaixo seus dados de acesso para a loja virtual:</b></p>");
            mensagemHtml.append("<p><b>Login:</b> ").append(pessoaFisica.getEmail()).append("</p>");
            mensagemHtml.append("<p><b>Senha:</b> ").append(senha).append("</p>");
            mensagemHtml.append("<p>Obrigado!</p>");
            mensagemHtml.append("</body>");
            mensagemHtml.append("</html>");
            try {
                serviceSendEmail.enviarEmailHtml("Acesso Gerado para loja Virtual", mensagemHtml.toString(), pessoaFisica.getEmail());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return pessoaFisica;
    }
}
