package jdev.lojavirtual_fs.lojavirtual_fs.service;

import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaJuridica;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Usuario;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.PessoaRepository;
import jdev.lojavirtual_fs.lojavirtual_fs.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class PessoaUserService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    public PessoaJuridica salvarPessoaJuridica(PessoaJuridica juridica) {
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

                usuarioRepository.insereAcessoUserPj(usuarioPj.getId());

        }

        return juridica;
    }
}
