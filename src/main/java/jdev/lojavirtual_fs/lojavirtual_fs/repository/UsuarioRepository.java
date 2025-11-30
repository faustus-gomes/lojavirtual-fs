package jdev.lojavirtual_fs.lojavirtual_fs.repository;

import jakarta.transaction.Transactional;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Usuario;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
    @Query(value = "select u from Usuario u where u.login = ?1")
    Usuario findUserByLogin(String login);

    @Query(value = "select u.* from usuario u " +
            "where u.data_atual_senha <= CURRENT_DATE - INTERVAL '10' DAY", nativeQuery = true)
    List<Usuario> usuarioSenhaVencida();
    @Query(value = "select u from Usuario u where u.pessoa.id = ?1 or u.login = ?2")
    Usuario findByUserPessoa(Long id, String email);

    @Query(value = "select constraint_name\n" +
            "from information_schema.constraint_column_usage\n" +
            " where table_name = 'usuario_acesso'\n" +
            " and column_name = 'acesso_id'\n" +
            " and constraint_name <> 'unique_acesso_user';",
            nativeQuery = true)
    String consultaConstraintAcesso();

    @Transactional
    @Modifying //futuramente excluir este por ser estático
    @Query(nativeQuery = true, value = "insert into usuario_acesso(usuario_id, acesso_id) " +
            "values(?1, (select id from acesso where descricao = 'ROLE_USER'))")
    void insereAcessoUserPj(Long iduser);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "insert into usuario_acesso(usuario_id, acesso_id) " +
            "values(?1, (select id from acesso where descricao = ?2))")
    void insereAcessoUserPj(Long iduser, String acesso);
}
