package jdev.lojavirtual_fs.lojavirtual_fs.repository;

import jdev.lojavirtual_fs.lojavirtual_fs.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query(nativeQuery = true, value = "select count(1) > 0 " +
            "from produto " +
            "where upper(trim(nome)) = upper(trim(?1));")
    public boolean existeProduto(String nomeCategoria);

    @Query("select a from Produto a where upper(trim(a.nome)) like %?1%")
    List<Produto> buscarProdutoNome(String nomeDesc);

    @Query("select a from Produto a " +
            "where upper(trim(a.nome)) like %?1% " +
            "and a.empresa.id = ?2")
    List<Produto> buscarProdutoNome(String nomeDesc, Long idEmpresa);

}
