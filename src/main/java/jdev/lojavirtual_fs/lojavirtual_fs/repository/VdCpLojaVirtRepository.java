package jdev.lojavirtual_fs.lojavirtual_fs.repository;

import jdev.lojavirtual_fs.lojavirtual_fs.model.VendaCompraLojaVirtual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

}
