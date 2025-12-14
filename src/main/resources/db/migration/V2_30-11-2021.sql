select constraint_name
from information_schema.constraint_column_usage
 where table_name = 'usuario_acesso'
 and column_name = 'acesso_id'
 and constraint_name <> 'unique_acesso_user';

alter table usuario_acesso drop CONSTRAINT "ukfhwpg5wu1u5p306q8gycxn9ky";

select constraint_name
from information_schema.constraint_column_usage
 where table_name = 'nota_fiscal_venda'
 and column_name = 'venda_compra_loja_virt_id'
 and constraint_name <> 'nota_fiscal_venda_pkey';

alter table nota_fiscal_venda drop CONSTRAINT "uk3sg7y5xs15vowbpi2mcql08kg";


select constraint_name
from information_schema.constraint_column_usage
 where table_name = 'vd_cp_loja_virt'
 and column_name = 'nota_fiscal_venda_id'
 and constraint_name <> 'vd_cp_loja_virt_pkey';

alter table vd_cp_loja_virt drop CONSTRAINT "ukhkxjejv08kldx994j4serhrbu";


--ALTER TABLE public.produto DROP CONSTRAINT nota_item_produto_id_fk;
--ALTER TABLE public.produto DROP COLUMN nota_item_produto_id;
