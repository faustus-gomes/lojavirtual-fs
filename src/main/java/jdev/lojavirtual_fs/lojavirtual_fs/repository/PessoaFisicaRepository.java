package jdev.lojavirtual_fs.lojavirtual_fs.repository;

import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaFisica;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaJuridica;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PessoaFisicaRepository extends CrudRepository<PessoaFisica, Long> {
    @Query(value = "select pf from PessoaFisica pf where upper(trim(pf.nome)) like %?1%")
    public List<PessoaFisica> pesquisaPorNomePF(String nome);

    @Query(value = "select pf from PessoaFisica pf where pf.cpf =?1")
    public List<PessoaFisica> pesquisaPorCPF(String cpf);

    // NOVOS MÉTODOS PARA O ASaaS
    @Query(value = "select pf from PessoaFisica pf where pf.email = ?1")
    public Optional<PessoaFisica> findByEmail(String email);

    @Query(value = "select pf from PessoaFisica pf where pf.asaasId = ?1")
    public Optional<PessoaFisica> findByAsaasId(String asaasId);

    @Query(value = "select pf from PessoaFisica pf where pf.email = ?1 or pf.cpf = ?2")
    public Optional<PessoaFisica> findByEmailOrCpf(String email, String cpf);

    // Método auxiliar para verificar existência por email
    default boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
}
