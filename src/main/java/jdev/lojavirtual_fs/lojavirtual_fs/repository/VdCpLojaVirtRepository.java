package jdev.lojavirtual_fs.lojavirtual_fs.repository;

import jdev.lojavirtual_fs.lojavirtual_fs.model.VendaCompraLojaVirtual;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.FiltroVendaDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface VdCpLojaVirtRepository extends JpaRepository<VendaCompraLojaVirtual, Long> {

    //Na aplicação feita em Java 11, não deu certo afzer q Query por aqui, então foi feita no VendaService
    @Modifying(flushAutomatically = true)
    @Query(nativeQuery = true, value = "BEGIN; " +
            // 1. Primeiro remove referências em outras tabelas
            "DELETE FROM item_venda_loja WHERE venda_compraloja_virtual_id = ?1; " +
            "DELETE FROM status_rastreio WHERE venda_compra_loja_virt_id = ?1; " +
            // 2. Remove referência da nota fiscal (setando para null)
            "UPDATE nota_fiscal_venda SET venda_compra_loja_virt_id = NULL " +
                    "WHERE venda_compra_loja_virt_id = ?1; " +
            // 3. Agora pode deletar a venda principal
            "DELETE FROM vd_cp_loja_virt WHERE id = ?1; " +
            // 4. Limpa notas fiscais sem venda (opcional)
            "DELETE FROM nota_fiscal_venda WHERE venda_compra_loja_virt_id IS NULL; " +
            "COMMIT;")
    void excluiTotalVendaBanco(Long idVenda);

    @Modifying(flushAutomatically = true)
    @Query(nativeQuery = true, value =
            "UPDATE VD_CP_LOJA_VIRT SET EXCLUIDO = TRUE WHERE ID = :idVenda")
    void excluiTotalVenda(@Param("idVenda") Long idVenda);

    @Query(value = "select a from VendaCompraLojaVirtual a " +
            "where a.id = ?1 and a.excluido = false")
    VendaCompraLojaVirtual findByIdExclusao(Long id);

    @Modifying(flushAutomatically = true)
    @Query(nativeQuery = true, value =
            "UPDATE VD_CP_LOJA_VIRT SET EXCLUIDO = FALSE WHERE ID = :idVenda")
    void ativaRegistroVendas(Long idVenda);

    @Query(value = "SELECT DISTINCT v FROM VendaCompraLojaVirtual v " +
            "WHERE v.excluido = false " +
            "AND EXISTS (SELECT 1 FROM ItemVendaLoja i " +
            "            WHERE i.vendaCompraLojaVirtual.id = v.id " +  // ← Corrigido
            "            AND i.produto.id = ?1)")
    List<VendaCompraLojaVirtual> vendaPorProduto(Long idProduto);

    @Query(value = "SELECT DISTINCT v.id as id, v.valorTotal as valorTotal, " +
            "v.valorDesconto as valorDesconto, v.valorFret as valorFrete, " +
            "v.pessoa.id as pessoaId, v.pessoa.nome as pessoaNome, v.pessoa.email as pessoaEmail " +
            "FROM VendaCompraLojaVirtual v " +
            "WHERE v.excluido = false " +
            "AND EXISTS (SELECT 1 FROM v.itemVendaLojas i WHERE i.produto.id = ?1)")
    List<FiltroVendaDTO> findVendasPorProdutoSimplificado(Long idProduto);

    @Query(value = "SELECT DISTINCT v FROM VendaCompraLojaVirtual v " +
            "LEFT JOIN FETCH v.pessoa p " +
            "LEFT JOIN FETCH v.enderecoEntrega ee " +
            "LEFT JOIN FETCH v.enderecoCobranca ec " +
            "LEFT JOIN FETCH v.itemVendaLojas ivl " +
            "LEFT JOIN FETCH ivl.produto prod " +
            "WHERE v.excluido = false " +
            "AND EXISTS (SELECT 1 FROM ItemVendaLoja i2 " +
            "            WHERE i2.vendaCompraLojaVirtual = v " +
            "            AND i2.produto.id = ?1)")
    List<VendaCompraLojaVirtual> vendaPorProdutoOtimizado(Long idProduto);

    @Query(value = "SELECT DISTINCT v FROM VendaCompraLojaVirtual v " +
            "LEFT JOIN FETCH v.pessoa p " +
            "LEFT JOIN FETCH v.enderecoEntrega ee " +
            "LEFT JOIN FETCH v.enderecoCobranca ec " +
            "LEFT JOIN FETCH v.itemVendaLojas i " +
            "LEFT JOIN FETCH i.produto prod " +
            "WHERE v.excluido = false " +
            "AND EXISTS (SELECT 1 FROM ItemVendaLoja i2 " +
            "            WHERE i2.vendaCompraLojaVirtual.id = v.id " +
            "            AND i2.produto.id = ?1)")
    List<VendaCompraLojaVirtual> vendaPorProdutoComFetch(Long idProduto);


    @Query(value = "SELECT DISTINCT v FROM VendaCompraLojaVirtual v " +
            "LEFT JOIN FETCH v.pessoa p " +
            "LEFT JOIN FETCH v.enderecoEntrega ee " +
            "LEFT JOIN FETCH v.enderecoCobranca ec " +
            "LEFT JOIN FETCH v.itemVendaLojas i " +
            "LEFT JOIN FETCH i.produto prod " +
            "WHERE v.excluido = false " +
            "AND (:idProduto IS NULL OR EXISTS (SELECT 1 FROM ItemVendaLoja i2 " +
            "            WHERE i2.vendaCompraLojaVirtual.id = v.id " +
            "            AND i2.produto.id = :idProduto)) " +
            "AND (:nomeProduto IS NULL OR prod.nome LIKE %:nomeProduto%) " +
            "AND (:nomeCliente IS NULL OR p.nome LIKE %:nomeCliente%) " +
            "AND (:emailCliente IS NULL OR p.email LIKE %:emailCliente%) " +
            "AND (:dataInicio IS NULL OR v.dataVenda >= :dataInicio) " +
            "AND (:dataFim IS NULL OR v.dataVenda <= :dataFim)")
    List<VendaCompraLojaVirtual> buscarVendasComFiltros(
            @Param("idProduto") Long idProduto,
            @Param("nomeProduto") String nomeProduto,
            @Param("nomeCliente") String nomeCliente,
            @Param("emailCliente") String emailCliente,
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim);
}
