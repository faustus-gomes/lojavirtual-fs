package jdev.lojavirtual_fs.lojavirtual_fs.repository;

import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaFisica;
import jdev.lojavirtual_fs.lojavirtual_fs.model.PessoaJuridica;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PessoaRepository extends CrudRepository<PessoaJuridica, Long> {

    @Query(value = "select pj from PessoaJuridica pj where upper(trim(pj.nome)) like %?1%")
    public List<PessoaJuridica> pesquisaPorNomePJ(String nome);

    @Query(value = "select pj from PessoaJuridica pj where pj.cnpj = ?1")
    public List<PessoaJuridica> existeCnpjCadastrado(String cnpj);

    @Query(value = "select pj from PessoaJuridica pj where pj.inscEstadual = ?1")
    public List<PessoaJuridica> existeInsEstadualCadastrado(String insEstadual);

    @Query(value = "select pf from PessoaFisica pf where pf.cpf = ?1")
    public List<PessoaFisica> existeCpfCadastrado(String cpf);

    // Método auxiliar para verificar existência
    /*default boolean existsByCnpj(String cnpj) {
        return !existeCnpjCadastrado(cnpj).isEmpty();
    }

    default boolean existsByInscEstadual(String inscEstadual) {
        return !existeInsEstadualCadastrado(inscEstadual).isEmpty();
    }*/
    // NOVOS MÉTODOS PARA O ASaaS
    @Query(value = "select pj from PessoaJuridica pj where pj.email = ?1")
    public Optional<PessoaJuridica> findByEmail(String email);

    @Query(value = "select pj from PessoaJuridica pj where pj.asaasId = ?1")
    public Optional<PessoaJuridica> findByAsaasId(String asaasId);

    @Query(value = "select pj from PessoaJuridica pj where pj.email = ?1 or pj.cnpj = ?2")
    public Optional<PessoaJuridica> findByEmailOrCnpj(String email, String cnpj);

    // Métodos auxiliares
    default boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    default Optional<PessoaJuridica> buscarPorEmailOuCnpj(String email, String cnpj) {
        // Primeiro tenta por email
        Optional<PessoaJuridica> result = findByEmail(email);
        if (result.isPresent()) {
            return result;
        }
        // Depois tenta por CNPJ
        List<PessoaJuridica> porCnpj = existeCnpjCadastrado(cnpj);
        if (!porCnpj.isEmpty()) {
            return Optional.of(porCnpj.get(0));
        }
        return Optional.empty();
    }

}
