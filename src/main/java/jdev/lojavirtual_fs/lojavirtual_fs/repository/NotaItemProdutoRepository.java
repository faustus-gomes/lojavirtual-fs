package jdev.lojavirtual_fs.lojavirtual_fs.repository;

import jdev.lojavirtual_fs.lojavirtual_fs.model.NotaItemProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface NotaItemProdutoRepository extends JpaRepository<NotaItemProduto, Long> {


    @Query("select a from NotaItemProduto a " +
            "where a.produto.id = ?1 " +
            "and a.notaFiscalCompra.id = ?2")
    List<NotaItemProduto> buscaNotaItemProdutoNota(Long idProduto, Long idNotaFiscal);

    @Query("select a from NotaItemProduto a " +
            "where a.produto.id = ?1")
    List<NotaItemProduto> buscaNotaItemProdutoNota(Long idProduto);

    @Query("select a from NotaItemProduto a " +
           "where a.notaFiscalCompra.id = ?1")
    List<NotaItemProduto> buscaNotaItemPorNotaFiscal(Long idNotaFiscal);

    @Query("select a from NotaItemProduto a " +
            "where a.empresa.id = ?1")
    List<NotaItemProduto> buscaNotaItemPorEmpresa(Long idEmpresa);
}
