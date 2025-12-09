package jdev.lojavirtual_fs.lojavirtual_fs.repository;

import jdev.lojavirtual_fs.lojavirtual_fs.model.Acesso;
import jdev.lojavirtual_fs.lojavirtual_fs.model.CategoriaProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Long> {

    @Query(nativeQuery = true, value = "select count(1) > 0 " +
            "from categoria_produto " +
            "where upper(trim(nome_desc)) = upper(trim(?1));")
    public boolean existeCategoria(String nomeCategoria);

    @Query("select a from CategoriaProduto a where upper(trim(a.nomeDesc)) like %?1%")
    List<CategoriaProduto> buscarCategoriaDesc(String nomeDesc);

}
