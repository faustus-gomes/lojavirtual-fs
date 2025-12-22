package jdev.lojavirtual_fs.lojavirtual_fs.service;

public class VendaService {


/* Esta rotina, quando não dá certo no repository, adaptamos aqui
========================================================================
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

    Conversão:
    ==============
    String value = "BEGIN; " +
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
            "COMMIT;";
*/

}
