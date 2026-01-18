package jdev.lojavirtual_fs.lojavirtual_fs.repository;

import jdev.lojavirtual_fs.lojavirtual_fs.model.StatusRastreio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface StatusRastreioRepository extends JpaRepository<StatusRastreio, Long> {

    @Query("select s " +
            "from StatusRastreio s " +
            "where s.vendaCompraLojaVirtual.id = ?1 " +
            "order by s.id")
    public List<StatusRastreio> listaRastreioVenda(Long idVenda);
}
