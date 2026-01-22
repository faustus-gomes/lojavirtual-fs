package jdev.lojavirtual_fs.lojavirtual_fs.repository;

import jdev.lojavirtual_fs.lojavirtual_fs.model.CupDesc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CupDescontoRepository extends JpaRepository<CupDesc, Long>{

    @Query(value = "select c from CupDesc c where c.empresa.id = ?1")
    public List<CupDesc> cupDescontoPorEmpresa(Long idEmpresa);
}
