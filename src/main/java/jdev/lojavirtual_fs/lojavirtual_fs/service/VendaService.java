package jdev.lojavirtual_fs.lojavirtual_fs.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jdev.lojavirtual_fs.lojavirtual_fs.dto.FiltroVendaDTO;
import jdev.lojavirtual_fs.lojavirtual_fs.model.ItemVendaLoja;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Pessoa;
import jdev.lojavirtual_fs.lojavirtual_fs.model.Produto;
import jdev.lojavirtual_fs.lojavirtual_fs.model.VendaCompraLojaVirtual;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
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

    @PersistenceContext
    private EntityManager entityManager;

    public List<VendaCompraLojaVirtual> buscarVendasComFiltros(FiltroVendaDTO filtro) {
        // Passo 1: Buscar IDs das vendas que atendem aos filtros
        List<Long> idsVendas = buscarIdsVendasPorFiltro(filtro);

        if (idsVendas.isEmpty()) {
            return Collections.emptyList();
        }

        // Passo 2: Buscar vendas completas com FETCH usando os IDs
        return buscarVendasComFetchPorIds(idsVendas);
    }

    private List<Long> buscarIdsVendasPorFiltro(FiltroVendaDTO filtro) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<VendaCompraLojaVirtual> venda = cq.from(VendaCompraLojaVirtual.class);

        List<Predicate> predicates = new ArrayList<>();

        // Filtro básico: não excluído
        predicates.add(cb.isFalse(venda.get("excluido")));

        // Aplicar filtros dinamicamente
        aplicarFiltros(filtro, cb, cq, venda, predicates);

        cq.select(venda.get("id")).distinct(true).where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(cq).getResultList();
    }

    private void aplicarFiltros(FiltroVendaDTO filtro, CriteriaBuilder cb,
                                CriteriaQuery<?> cq, Root<VendaCompraLojaVirtual> venda,
                                List<Predicate> predicates) {

        // Filtro por ID do produto
        if (filtro.getIdProduto() != null) {
            Subquery<Long> subquery = cq.subquery(Long.class);
            Root<ItemVendaLoja> item = subquery.from(ItemVendaLoja.class);
            subquery.select(item.get("vendaCompraLojaVirtual").get("id"))
                    .where(cb.equal(item.get("produto").get("id"), filtro.getIdProduto()));
            predicates.add(venda.get("id").in(subquery));
        }

        // Filtro por nome do produto
        if (filtro.getNomeProduto() != null) {
            Subquery<Long> subquery = cq.subquery(Long.class);
            Root<ItemVendaLoja> item = subquery.from(ItemVendaLoja.class);
            Join<ItemVendaLoja, Produto> produto = item.join("produto");
            subquery.select(item.get("vendaCompraLojaVirtual").get("id"))
                    .where(cb.like(cb.lower(produto.get("nome")),
                            "%" + filtro.getNomeProduto().toLowerCase() + "%"));
            predicates.add(venda.get("id").in(subquery));
        }

        // Filtro por nome do cliente
        if (filtro.getNomeCliente() != null) {
            Join<VendaCompraLojaVirtual, Pessoa> pessoa = venda.join("pessoa");
            predicates.add(cb.like(cb.lower(pessoa.get("nome")),
                    "%" + filtro.getNomeCliente().toLowerCase() + "%"));
        }

        // Filtro por email do cliente
        if (filtro.getEmailCliente() != null) {
            Join<VendaCompraLojaVirtual, Pessoa> pessoa = venda.join("pessoa");
            predicates.add(cb.like(cb.lower(pessoa.get("email")),
                    "%" + filtro.getEmailCliente().toLowerCase() + "%"));
        }

        // Filtro por data início
        if (filtro.getDataInicio() != null) {
            predicates.add(cb.greaterThanOrEqualTo(venda.get("dataVenda"), filtro.getDataInicio()));
        }

        // Filtro por data fim
        if (filtro.getDataFim() != null) {
            predicates.add(cb.lessThanOrEqualTo(venda.get("dataVenda"), filtro.getDataFim()));
        }
    }

    private List<VendaCompraLojaVirtual> buscarVendasComFetchPorIds(List<Long> ids) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<VendaCompraLojaVirtual> cq = cb.createQuery(VendaCompraLojaVirtual.class);
        Root<VendaCompraLojaVirtual> venda = cq.from(VendaCompraLojaVirtual.class);

        // FETCH de todos os relacionamentos
        venda.fetch("pessoa", JoinType.LEFT);
        venda.fetch("enderecoEntrega", JoinType.LEFT);
        venda.fetch("enderecoCobranca", JoinType.LEFT);
        venda.fetch("itemVendaLojas", JoinType.LEFT);

        // FETCH aninhado: produto dentro dos itens
        Fetch<VendaCompraLojaVirtual, ItemVendaLoja> itemFetch = venda.fetch("itemVendaLojas", JoinType.LEFT);
        itemFetch.fetch("produto", JoinType.LEFT);

        cq.select(venda)
                .where(venda.get("id").in(ids))
                .distinct(true);

        return entityManager.createQuery(cq).getResultList();
    }

}
