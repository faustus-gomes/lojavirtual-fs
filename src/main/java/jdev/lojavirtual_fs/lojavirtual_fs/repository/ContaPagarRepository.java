package jdev.lojavirtual_fs.lojavirtual_fs.repository;

import jdev.lojavirtual_fs.lojavirtual_fs.model.ContaPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ContaPagarRepository extends JpaRepository<ContaPagar,Long> {

    @Query("select a from ContaPagar a " +
            "where upper(trim(a.descricao)) like %?1%")
    List<ContaPagar> buscaContaDesc(String des);

    @Query("select a from ContaPagar a " +
            "where a.pessoa.id = ?1")
    List<ContaPagar> buscaContaPessoa(Long idPessoa);

    @Query("select a from ContaPagar a " +
            "where a.pessoa_fornecedor.id = ?1")
    List<ContaPagar> buscaContaFornecedor(Long idFornecedor);

    @Query("select a from ContaPagar a " +
            "where a.empresa.id = ?1")
    List<ContaPagar> buscaContaEmpresa(Long idEmpresa);
}
