package jdev.lojavirtual_fs.lojavirtual_fs.repository;

import jdev.lojavirtual_fs.lojavirtual_fs.model.MarcaProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface MarcaProdutoRepository extends JpaRepository<MarcaProduto, Long> {
    /*@Query("select a from MarcaProduto a where upper(trim(a.nomDesc)) like %?1%")
    List<MarcaProduto> buscarMarcaDesc(String desc);*/

}
