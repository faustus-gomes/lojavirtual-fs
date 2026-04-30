package jdev.lojavirtual_fs.lojavirtual_fs.repository;

import jdev.lojavirtual_fs.lojavirtual_fs.model.CategoriaProduto;
import jdev.lojavirtual_fs.lojavirtual_fs.model.MarcaProduto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface MarcaProdutoRepository extends JpaRepository<MarcaProduto, Long> {
    @Query("select a from MarcaProduto a " +
            "where upper(trim(a.nomeDesc)) like %?1%")
    List<MarcaProduto> buscarMarcaDesc(String desc);

    @Query(nativeQuery = true,
            value = "select ceiling(cast(count(1) as float) / 10) as qtdpagina " +
                    "from marca_produto " +
                    "where empresa_id = ?1 ")
    public Integer qtdPagina(Long idEmpresa);
    @Query(value = "select a from MarcaProduto a where a.empresa.id = ?1 ")
    public List<MarcaProduto> findPorPage(Long idEmpresa, Pageable pageable);

}
