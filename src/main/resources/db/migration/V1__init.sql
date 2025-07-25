--
-- PostgreSQL database dump
--

-- Dumped from database version 17.0 (Debian 17.0-1.pgdg120+1)
-- Dumped by pg_dump version 17.0

-- Started on 2025-06-03 10:00:56 -03

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 258 (class 1255 OID 20476)
-- Name: validachavepessoa(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.validachavepessoa() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
	declare existe integer;
begin
	existe = (select count(1) from pessoa_fisica where id = NEW.pessoa_id);
	if (existe <= 0) then
		existe = (select count(1) from pessoa_juridica where id = NEW.pessoa_id);
	if (existe <= 0) then
		raise exception 'Não foi encontrado o ID ou PK da pessoa para realizar a associação';
	 end if;
	end if;
	return NEW;
end;
$$;


ALTER FUNCTION public.validachavepessoa() OWNER TO postgres;

--
-- TOC entry 259 (class 1255 OID 20481)
-- Name: validachavepessoa2(); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION public.validachavepessoa2() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
	declare existe integer;
begin
	existe = (select count(1) from pessoa_fisica where id = NEW.pessoa_forn_id);
	if (existe <= 0) then
		existe = (select count(1) from pessoa_juridica where id = NEW.pessoa_forn_id);
	if (existe <= 0) then
		raise exception 'Não foi encontrado o ID ou PK da pessoa para realizar a associação';
	 end if;
	end if;
	return NEW;
end;
$$;


ALTER FUNCTION public.validachavepessoa2() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 221 (class 1259 OID 20162)
-- Name: acesso; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.acesso (
    id bigint NOT NULL,
    descricao character varying(255) NOT NULL
);


ALTER TABLE public.acesso OWNER TO postgres;

--
-- TOC entry 256 (class 1259 OID 20450)
-- Name: avaliacao_produto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.avaliacao_produto (
    id bigint NOT NULL,
    pessoa_id bigint NOT NULL,
    produto_id bigint NOT NULL,
    nota integer NOT NULL,
    descricao character varying(255) NOT NULL
);


ALTER TABLE public.avaliacao_produto OWNER TO postgres;

--
-- TOC entry 219 (class 1259 OID 20156)
-- Name: categoria_produto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.categoria_produto (
    id bigint NOT NULL,
    nome_desc character varying(255) NOT NULL
);


ALTER TABLE public.categoria_produto OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 20278)
-- Name: conta_pagar; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.conta_pagar (
    id bigint NOT NULL,
    dt_pagamento date,
    valor_desconto numeric(38,2),
    valor_total numeric(38,2),
    pessoa_id bigint NOT NULL,
    pessoa_forn_id bigint NOT NULL,
    descricao character varying(255) NOT NULL,
    dt_vencimento date NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT conta_pagar_status_check CHECK (((status)::text = ANY ((ARRAY['COBRANCA'::character varying, 'VENCIDA'::character varying, 'ABERTA'::character varying, 'QUITADA'::character varying, 'NEGOCIADA'::character varying])::text[])))
);


ALTER TABLE public.conta_pagar OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 20264)
-- Name: conta_receber; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.conta_receber (
    id bigint NOT NULL,
    dt_pagamento date,
    valor_desconto numeric(38,2),
    pessoa_id bigint NOT NULL,
    descricao character varying(255) NOT NULL,
    dt_vencimento date NOT NULL,
    valor_total numeric(38,2) NOT NULL,
    status character varying(255) NOT NULL,
    CONSTRAINT conta_receber_status_check CHECK (((status)::text = ANY ((ARRAY['COBRANCA'::character varying, 'VENCIDA'::character varying, 'ABERTA'::character varying, 'QUITADA'::character varying])::text[])))
);


ALTER TABLE public.conta_receber OWNER TO postgres;

--
-- TOC entry 238 (class 1259 OID 20289)
-- Name: cup_desc; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.cup_desc (
    id bigint NOT NULL,
    valor_porcent_desc numeric(38,2),
    valor_real_desc numeric(38,2),
    cod_desc character varying(255) NOT NULL,
    data_validade_cupom date NOT NULL
);


ALTER TABLE public.cup_desc OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 20212)
-- Name: endereco; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.endereco (
    id bigint NOT NULL,
    bairro character varying(255) NOT NULL,
    cep character varying(255) NOT NULL,
    cidade character varying(255) NOT NULL,
    complemento character varying(255),
    numero character varying(255) NOT NULL,
    rua_logra character varying(255) NOT NULL,
    uf character varying(255) NOT NULL,
    pessoa_id bigint NOT NULL,
    tipo_endereco smallint NOT NULL,
    CONSTRAINT endereco_tipo_endereco_check CHECK (((tipo_endereco >= 0) AND (tipo_endereco <= 1)))
);


ALTER TABLE public.endereco OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 20272)
-- Name: forma_pagamento; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.forma_pagamento (
    id bigint NOT NULL,
    descricao character varying(255) NOT NULL
);


ALTER TABLE public.forma_pagamento OWNER TO postgres;

--
-- TOC entry 242 (class 1259 OID 20303)
-- Name: imagem_produto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.imagem_produto (
    id bigint NOT NULL,
    produto_id bigint NOT NULL,
    imagem_miniatura text NOT NULL,
    imagem_original text NOT NULL
);


ALTER TABLE public.imagem_produto OWNER TO postgres;

--
-- TOC entry 254 (class 1259 OID 20434)
-- Name: item_venda_loja; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.item_venda_loja (
    id bigint NOT NULL,
    produto_id bigint NOT NULL,
    venda_compraloja_virtual_id bigint NOT NULL,
    quantidade double precision NOT NULL
);


ALTER TABLE public.item_venda_loja OWNER TO postgres;

--
-- TOC entry 217 (class 1259 OID 20149)
-- Name: marca_produto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.marca_produto (
    id bigint NOT NULL,
    nome_desc character varying(255) NOT NULL
);


ALTER TABLE public.marca_produto OWNER TO postgres;

--
-- TOC entry 244 (class 1259 OID 20316)
-- Name: nota_fiscal_compra; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nota_fiscal_compra (
    id bigint NOT NULL,
    descricao_obs character varying(255),
    valor_desconto numeric(38,2),
    conta_pagar_id bigint NOT NULL,
    pessoa_id bigint NOT NULL,
    data_compra date NOT NULL,
    nunero_nota character varying(255) NOT NULL,
    serie_nota character varying(255) NOT NULL,
    valor_icms numeric(38,2) NOT NULL,
    valor_total numeric(38,2) NOT NULL
);


ALTER TABLE public.nota_fiscal_compra OWNER TO postgres;

--
-- TOC entry 250 (class 1259 OID 20380)
-- Name: nota_fiscal_venda; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nota_fiscal_venda (
    id bigint NOT NULL,
    venda_compra_loja_virt_id bigint NOT NULL,
    numero character varying(255) NOT NULL,
    pdf text NOT NULL,
    serie character varying(255) NOT NULL,
    tipo character varying(255) NOT NULL,
    xml text NOT NULL
);


ALTER TABLE public.nota_fiscal_venda OWNER TO postgres;

--
-- TOC entry 246 (class 1259 OID 20356)
-- Name: nota_item_produto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.nota_item_produto (
    id bigint NOT NULL,
    produto_id bigint NOT NULL,
    nota_fiscal_compra_id bigint NOT NULL,
    quantidade double precision NOT NULL
);


ALTER TABLE public.nota_item_produto OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 20198)
-- Name: pessoa_fisica; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pessoa_fisica (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    nome character varying(255) NOT NULL,
    telefone character varying(255) NOT NULL,
    cpf character varying(255) NOT NULL,
    data_nascimento date
);


ALTER TABLE public.pessoa_fisica OWNER TO postgres;

--
-- TOC entry 225 (class 1259 OID 20205)
-- Name: pessoa_juridica; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.pessoa_juridica (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    nome character varying(255) NOT NULL,
    telefone character varying(255) NOT NULL,
    categoria character varying(255),
    cnpj character varying(255) NOT NULL,
    insc_estadual character varying(255) NOT NULL,
    insc_municipal character varying(255),
    nome_fantasia character varying(255) NOT NULL,
    razao_social character varying(255) NOT NULL
);


ALTER TABLE public.pessoa_juridica OWNER TO postgres;

--
-- TOC entry 240 (class 1259 OID 20295)
-- Name: produto; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.produto (
    id bigint NOT NULL,
    alerta_qtde_estoque boolean,
    link_youtube character varying(255),
    qtde_alerta_estoque integer,
    qtde_clique integer,
    altura double precision NOT NULL,
    ativo boolean NOT NULL,
    descricao text NOT NULL,
    largura double precision NOT NULL,
    nome character varying(255) NOT NULL,
    peso double precision NOT NULL,
    profundidade double precision NOT NULL,
    qtde_estoque integer NOT NULL,
    tipo_unidade character varying(255) NOT NULL,
    valor_venda numeric(38,2) NOT NULL
);


ALTER TABLE public.produto OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 20167)
-- Name: seq_acesso; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_acesso
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_acesso OWNER TO postgres;

--
-- TOC entry 257 (class 1259 OID 20455)
-- Name: seq_avaliacao_produto; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_avaliacao_produto
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_avaliacao_produto OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 20161)
-- Name: seq_categoria_produto; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_categoria_produto
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_categoria_produto OWNER TO postgres;

--
-- TOC entry 236 (class 1259 OID 20287)
-- Name: seq_conta_pagar; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_conta_pagar
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_conta_pagar OWNER TO postgres;

--
-- TOC entry 231 (class 1259 OID 20254)
-- Name: seq_conta_rdeceber; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_conta_rdeceber
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_conta_rdeceber OWNER TO postgres;

--
-- TOC entry 237 (class 1259 OID 20288)
-- Name: seq_conta_receber; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_conta_receber
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_conta_receber OWNER TO postgres;

--
-- TOC entry 239 (class 1259 OID 20294)
-- Name: seq_cup_desc; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_cup_desc
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_cup_desc OWNER TO postgres;

--
-- TOC entry 227 (class 1259 OID 20219)
-- Name: seq_endereco; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_endereco
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_endereco OWNER TO postgres;

--
-- TOC entry 234 (class 1259 OID 20277)
-- Name: seq_forma_pagamento; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_forma_pagamento
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_forma_pagamento OWNER TO postgres;

--
-- TOC entry 243 (class 1259 OID 20310)
-- Name: seq_imagem_produto; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_imagem_produto
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_imagem_produto OWNER TO postgres;

--
-- TOC entry 255 (class 1259 OID 20439)
-- Name: seq_item_venda_loja; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_item_venda_loja
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_item_venda_loja OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 20154)
-- Name: seq_marca_produto; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_marca_produto
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_marca_produto OWNER TO postgres;

--
-- TOC entry 245 (class 1259 OID 20323)
-- Name: seq_nota_fiscal_compra; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_nota_fiscal_compra
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_nota_fiscal_compra OWNER TO postgres;

--
-- TOC entry 251 (class 1259 OID 20387)
-- Name: seq_nota_fiscal_venda; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_nota_fiscal_venda
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_nota_fiscal_venda OWNER TO postgres;

--
-- TOC entry 247 (class 1259 OID 20361)
-- Name: seq_nota_item_produto; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_nota_item_produto
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_nota_item_produto OWNER TO postgres;

--
-- TOC entry 223 (class 1259 OID 20187)
-- Name: seq_pessoa; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_pessoa
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_pessoa OWNER TO postgres;

--
-- TOC entry 241 (class 1259 OID 20302)
-- Name: seq_produto; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_produto
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_produto OWNER TO postgres;

--
-- TOC entry 249 (class 1259 OID 20379)
-- Name: seq_status_rastreio; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_status_rastreio
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_status_rastreio OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 20235)
-- Name: seq_usuario; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_usuario
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_usuario OWNER TO postgres;

--
-- TOC entry 253 (class 1259 OID 20393)
-- Name: seq_vd_cp_loja_virt; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.seq_vd_cp_loja_virt
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.seq_vd_cp_loja_virt OWNER TO postgres;

--
-- TOC entry 248 (class 1259 OID 20372)
-- Name: status_rastreio; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.status_rastreio (
    id bigint NOT NULL,
    centro_distribuicao character varying(255),
    cidade character varying(255),
    estado character varying(255),
    status character varying(255),
    venda_compra_loja_virt_id bigint NOT NULL
);


ALTER TABLE public.status_rastreio OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 20221)
-- Name: usuario; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuario (
    id bigint NOT NULL,
    data_atual_senha date NOT NULL,
    login character varying(255) NOT NULL,
    senha character varying(255) NOT NULL,
    pessoa_id bigint NOT NULL
);


ALTER TABLE public.usuario OWNER TO postgres;

--
-- TOC entry 229 (class 1259 OID 20228)
-- Name: usuario_acesso; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.usuario_acesso (
    usuario_id bigint NOT NULL,
    acesso_id bigint NOT NULL
);


ALTER TABLE public.usuario_acesso OWNER TO postgres;

--
-- TOC entry 252 (class 1259 OID 20388)
-- Name: vd_cp_loja_virt; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vd_cp_loja_virt (
    id bigint NOT NULL,
    pessoa_id bigint NOT NULL,
    endereco_cobranca_id bigint NOT NULL,
    endereco_entrega_id bigint NOT NULL,
    valor_desconto numeric(38,2),
    forma_pagamento_id bigint NOT NULL,
    nota_fiscal_venda_id bigint NOT NULL,
    data_entrega date NOT NULL,
    data_venda date NOT NULL,
    dia_entrega integer NOT NULL,
    valor_fret numeric(38,2) NOT NULL,
    cupom_desc_id bigint,
    valor_total numeric(38,2) NOT NULL
);


ALTER TABLE public.vd_cp_loja_virt OWNER TO postgres;

--
-- TOC entry 3537 (class 0 OID 20162)
-- Dependencies: 221
-- Data for Name: acesso; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.acesso (id, descricao) FROM stdin;
\.


--
-- TOC entry 3572 (class 0 OID 20450)
-- Dependencies: 256
-- Data for Name: avaliacao_produto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.avaliacao_produto (id, pessoa_id, produto_id, nota, descricao) FROM stdin;
1	1	1	10	Teste Avaliação Trigger
\.


--
-- TOC entry 3535 (class 0 OID 20156)
-- Dependencies: 219
-- Data for Name: categoria_produto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.categoria_produto (id, nome_desc) FROM stdin;
\.


--
-- TOC entry 3551 (class 0 OID 20278)
-- Dependencies: 235
-- Data for Name: conta_pagar; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.conta_pagar (id, dt_pagamento, valor_desconto, valor_total, pessoa_id, pessoa_forn_id, descricao, dt_vencimento, status) FROM stdin;
\.


--
-- TOC entry 3548 (class 0 OID 20264)
-- Dependencies: 232
-- Data for Name: conta_receber; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.conta_receber (id, dt_pagamento, valor_desconto, pessoa_id, descricao, dt_vencimento, valor_total, status) FROM stdin;
\.


--
-- TOC entry 3554 (class 0 OID 20289)
-- Dependencies: 238
-- Data for Name: cup_desc; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.cup_desc (id, valor_porcent_desc, valor_real_desc, cod_desc, data_validade_cupom) FROM stdin;
\.


--
-- TOC entry 3542 (class 0 OID 20212)
-- Dependencies: 226
-- Data for Name: endereco; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.endereco (id, bairro, cep, cidade, complemento, numero, rua_logra, uf, pessoa_id, tipo_endereco) FROM stdin;
\.


--
-- TOC entry 3549 (class 0 OID 20272)
-- Dependencies: 233
-- Data for Name: forma_pagamento; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.forma_pagamento (id, descricao) FROM stdin;
\.


--
-- TOC entry 3558 (class 0 OID 20303)
-- Dependencies: 242
-- Data for Name: imagem_produto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.imagem_produto (id, produto_id, imagem_miniatura, imagem_original) FROM stdin;
\.


--
-- TOC entry 3570 (class 0 OID 20434)
-- Dependencies: 254
-- Data for Name: item_venda_loja; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.item_venda_loja (id, produto_id, venda_compraloja_virtual_id, quantidade) FROM stdin;
\.


--
-- TOC entry 3533 (class 0 OID 20149)
-- Dependencies: 217
-- Data for Name: marca_produto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.marca_produto (id, nome_desc) FROM stdin;
\.


--
-- TOC entry 3560 (class 0 OID 20316)
-- Dependencies: 244
-- Data for Name: nota_fiscal_compra; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.nota_fiscal_compra (id, descricao_obs, valor_desconto, conta_pagar_id, pessoa_id, data_compra, nunero_nota, serie_nota, valor_icms, valor_total) FROM stdin;
\.


--
-- TOC entry 3566 (class 0 OID 20380)
-- Dependencies: 250
-- Data for Name: nota_fiscal_venda; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.nota_fiscal_venda (id, venda_compra_loja_virt_id, numero, pdf, serie, tipo, xml) FROM stdin;
\.


--
-- TOC entry 3562 (class 0 OID 20356)
-- Dependencies: 246
-- Data for Name: nota_item_produto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.nota_item_produto (id, produto_id, nota_fiscal_compra_id, quantidade) FROM stdin;
\.


--
-- TOC entry 3540 (class 0 OID 20198)
-- Dependencies: 224
-- Data for Name: pessoa_fisica; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pessoa_fisica (id, email, nome, telefone, cpf, data_nascimento) FROM stdin;
1	faustus.gomes@gmail.com	Fausto P Gomes	019 99814-5284	22288978676	1977-05-06
\.


--
-- TOC entry 3541 (class 0 OID 20205)
-- Dependencies: 225
-- Data for Name: pessoa_juridica; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.pessoa_juridica (id, email, nome, telefone, categoria, cnpj, insc_estadual, insc_municipal, nome_fantasia, razao_social) FROM stdin;
\.


--
-- TOC entry 3556 (class 0 OID 20295)
-- Dependencies: 240
-- Data for Name: produto; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.produto (id, alerta_qtde_estoque, link_youtube, qtde_alerta_estoque, qtde_clique, altura, ativo, descricao, largura, nome, peso, profundidade, qtde_estoque, tipo_unidade, valor_venda) FROM stdin;
1	t	tstststs	0	50	10	t	Produto Teste	50.2	Nome Produto Teste	50	80.8	1	UN	50.00
\.


--
-- TOC entry 3564 (class 0 OID 20372)
-- Dependencies: 248
-- Data for Name: status_rastreio; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.status_rastreio (id, centro_distribuicao, cidade, estado, status, venda_compra_loja_virt_id) FROM stdin;
\.


--
-- TOC entry 3544 (class 0 OID 20221)
-- Dependencies: 228
-- Data for Name: usuario; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.usuario (id, data_atual_senha, login, senha, pessoa_id) FROM stdin;
\.


--
-- TOC entry 3545 (class 0 OID 20228)
-- Dependencies: 229
-- Data for Name: usuario_acesso; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.usuario_acesso (usuario_id, acesso_id) FROM stdin;
\.


--
-- TOC entry 3568 (class 0 OID 20388)
-- Dependencies: 252
-- Data for Name: vd_cp_loja_virt; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.vd_cp_loja_virt (id, pessoa_id, endereco_cobranca_id, endereco_entrega_id, valor_desconto, forma_pagamento_id, nota_fiscal_venda_id, data_entrega, data_venda, dia_entrega, valor_fret, cupom_desc_id, valor_total) FROM stdin;
\.


--
-- TOC entry 3579 (class 0 OID 0)
-- Dependencies: 222
-- Name: seq_acesso; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_acesso', 1, false);


--
-- TOC entry 3580 (class 0 OID 0)
-- Dependencies: 257
-- Name: seq_avaliacao_produto; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_avaliacao_produto', 1, false);


--
-- TOC entry 3581 (class 0 OID 0)
-- Dependencies: 220
-- Name: seq_categoria_produto; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_categoria_produto', 1, false);


--
-- TOC entry 3582 (class 0 OID 0)
-- Dependencies: 236
-- Name: seq_conta_pagar; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_conta_pagar', 1, false);


--
-- TOC entry 3583 (class 0 OID 0)
-- Dependencies: 231
-- Name: seq_conta_rdeceber; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_conta_rdeceber', 1, false);


--
-- TOC entry 3584 (class 0 OID 0)
-- Dependencies: 237
-- Name: seq_conta_receber; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_conta_receber', 1, false);


--
-- TOC entry 3585 (class 0 OID 0)
-- Dependencies: 239
-- Name: seq_cup_desc; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_cup_desc', 1, false);


--
-- TOC entry 3586 (class 0 OID 0)
-- Dependencies: 227
-- Name: seq_endereco; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_endereco', 1, false);


--
-- TOC entry 3587 (class 0 OID 0)
-- Dependencies: 234
-- Name: seq_forma_pagamento; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_forma_pagamento', 1, false);


--
-- TOC entry 3588 (class 0 OID 0)
-- Dependencies: 243
-- Name: seq_imagem_produto; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_imagem_produto', 1, false);


--
-- TOC entry 3589 (class 0 OID 0)
-- Dependencies: 255
-- Name: seq_item_venda_loja; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_item_venda_loja', 1, false);


--
-- TOC entry 3590 (class 0 OID 0)
-- Dependencies: 218
-- Name: seq_marca_produto; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_marca_produto', 1, false);


--
-- TOC entry 3591 (class 0 OID 0)
-- Dependencies: 245
-- Name: seq_nota_fiscal_compra; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_nota_fiscal_compra', 1, false);


--
-- TOC entry 3592 (class 0 OID 0)
-- Dependencies: 251
-- Name: seq_nota_fiscal_venda; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_nota_fiscal_venda', 1, false);


--
-- TOC entry 3593 (class 0 OID 0)
-- Dependencies: 247
-- Name: seq_nota_item_produto; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_nota_item_produto', 1, false);


--
-- TOC entry 3594 (class 0 OID 0)
-- Dependencies: 223
-- Name: seq_pessoa; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_pessoa', 1, false);


--
-- TOC entry 3595 (class 0 OID 0)
-- Dependencies: 241
-- Name: seq_produto; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_produto', 1, false);


--
-- TOC entry 3596 (class 0 OID 0)
-- Dependencies: 249
-- Name: seq_status_rastreio; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_status_rastreio', 1, false);


--
-- TOC entry 3597 (class 0 OID 0)
-- Dependencies: 230
-- Name: seq_usuario; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_usuario', 1, false);


--
-- TOC entry 3598 (class 0 OID 0)
-- Dependencies: 253
-- Name: seq_vd_cp_loja_virt; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.seq_vd_cp_loja_virt', 1, false);


--
-- TOC entry 3319 (class 2606 OID 20166)
-- Name: acesso acesso_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.acesso
    ADD CONSTRAINT acesso_pkey PRIMARY KEY (id);


--
-- TOC entry 3355 (class 2606 OID 20454)
-- Name: avaliacao_produto avaliacao_produto_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.avaliacao_produto
    ADD CONSTRAINT avaliacao_produto_pkey PRIMARY KEY (id);


--
-- TOC entry 3317 (class 2606 OID 20160)
-- Name: categoria_produto categoria_produto_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.categoria_produto
    ADD CONSTRAINT categoria_produto_pkey PRIMARY KEY (id);


--
-- TOC entry 3335 (class 2606 OID 20285)
-- Name: conta_pagar conta_pagar_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.conta_pagar
    ADD CONSTRAINT conta_pagar_pkey PRIMARY KEY (id);


--
-- TOC entry 3331 (class 2606 OID 20271)
-- Name: conta_receber conta_receber_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.conta_receber
    ADD CONSTRAINT conta_receber_pkey PRIMARY KEY (id);


--
-- TOC entry 3337 (class 2606 OID 20293)
-- Name: cup_desc cup_desc_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.cup_desc
    ADD CONSTRAINT cup_desc_pkey PRIMARY KEY (id);


--
-- TOC entry 3325 (class 2606 OID 20218)
-- Name: endereco endereco_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.endereco
    ADD CONSTRAINT endereco_pkey PRIMARY KEY (id);


--
-- TOC entry 3333 (class 2606 OID 20276)
-- Name: forma_pagamento forma_pagamento_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.forma_pagamento
    ADD CONSTRAINT forma_pagamento_pkey PRIMARY KEY (id);


--
-- TOC entry 3341 (class 2606 OID 20309)
-- Name: imagem_produto imagem_produto_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.imagem_produto
    ADD CONSTRAINT imagem_produto_pkey PRIMARY KEY (id);


--
-- TOC entry 3353 (class 2606 OID 20438)
-- Name: item_venda_loja item_venda_loja_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item_venda_loja
    ADD CONSTRAINT item_venda_loja_pkey PRIMARY KEY (id);


--
-- TOC entry 3315 (class 2606 OID 20153)
-- Name: marca_produto marca_produto_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.marca_produto
    ADD CONSTRAINT marca_produto_pkey PRIMARY KEY (id);


--
-- TOC entry 3343 (class 2606 OID 20322)
-- Name: nota_fiscal_compra nota_fiscal_compra_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nota_fiscal_compra
    ADD CONSTRAINT nota_fiscal_compra_pkey PRIMARY KEY (id);


--
-- TOC entry 3349 (class 2606 OID 20386)
-- Name: nota_fiscal_venda nota_fiscal_venda_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nota_fiscal_venda
    ADD CONSTRAINT nota_fiscal_venda_pkey PRIMARY KEY (id);


--
-- TOC entry 3345 (class 2606 OID 20360)
-- Name: nota_item_produto nota_item_produto_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nota_item_produto
    ADD CONSTRAINT nota_item_produto_pkey PRIMARY KEY (id);


--
-- TOC entry 3321 (class 2606 OID 20204)
-- Name: pessoa_fisica pessoa_fisica_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pessoa_fisica
    ADD CONSTRAINT pessoa_fisica_pkey PRIMARY KEY (id);


--
-- TOC entry 3323 (class 2606 OID 20211)
-- Name: pessoa_juridica pessoa_juridica_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.pessoa_juridica
    ADD CONSTRAINT pessoa_juridica_pkey PRIMARY KEY (id);


--
-- TOC entry 3339 (class 2606 OID 20301)
-- Name: produto produto_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.produto
    ADD CONSTRAINT produto_pkey PRIMARY KEY (id);


--
-- TOC entry 3347 (class 2606 OID 20378)
-- Name: status_rastreio status_rastreio_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.status_rastreio
    ADD CONSTRAINT status_rastreio_pkey PRIMARY KEY (id);


--
-- TOC entry 3329 (class 2606 OID 20232)
-- Name: usuario_acesso unique_acesso_user; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario_acesso
    ADD CONSTRAINT unique_acesso_user UNIQUE (usuario_id, acesso_id);


--
-- TOC entry 3327 (class 2606 OID 20227)
-- Name: usuario usuario_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT usuario_pkey PRIMARY KEY (id);


--
-- TOC entry 3351 (class 2606 OID 20392)
-- Name: vd_cp_loja_virt vd_cp_loja_virt_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vd_cp_loja_virt
    ADD CONSTRAINT vd_cp_loja_virt_pkey PRIMARY KEY (id);


--
-- TOC entry 3376 (class 2620 OID 20484)
-- Name: conta_receber validachavepessoa; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoa BEFORE UPDATE ON public.conta_receber FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3372 (class 2620 OID 20486)
-- Name: endereco validachavepessoa; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoa BEFORE UPDATE ON public.endereco FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3382 (class 2620 OID 20488)
-- Name: nota_fiscal_compra validachavepessoa; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoa BEFORE UPDATE ON public.nota_fiscal_compra FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3374 (class 2620 OID 20490)
-- Name: usuario validachavepessoa; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoa BEFORE UPDATE ON public.usuario FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3384 (class 2620 OID 20492)
-- Name: vd_cp_loja_virt validachavepessoa; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoa BEFORE UPDATE ON public.vd_cp_loja_virt FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3377 (class 2620 OID 20485)
-- Name: conta_receber validachavepessoa2; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoa2 BEFORE INSERT ON public.conta_receber FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa2();


--
-- TOC entry 3373 (class 2620 OID 20487)
-- Name: endereco validachavepessoa2; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoa2 BEFORE INSERT ON public.endereco FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa2();


--
-- TOC entry 3383 (class 2620 OID 20489)
-- Name: nota_fiscal_compra validachavepessoa2; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoa2 BEFORE INSERT ON public.nota_fiscal_compra FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa2();


--
-- TOC entry 3375 (class 2620 OID 20491)
-- Name: usuario validachavepessoa2; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoa2 BEFORE INSERT ON public.usuario FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa2();


--
-- TOC entry 3385 (class 2620 OID 20493)
-- Name: vd_cp_loja_virt validachavepessoa2; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoa2 BEFORE INSERT ON public.vd_cp_loja_virt FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa2();


--
-- TOC entry 3386 (class 2620 OID 20477)
-- Name: avaliacao_produto validachavepessoaavaliacaoproduto; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoaavaliacaoproduto BEFORE UPDATE ON public.avaliacao_produto FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3387 (class 2620 OID 20478)
-- Name: avaliacao_produto validachavepessoaavaliacaoproduto2; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoaavaliacaoproduto2 BEFORE INSERT ON public.avaliacao_produto FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3378 (class 2620 OID 20479)
-- Name: conta_pagar validachavepessoacontapagar; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoacontapagar BEFORE UPDATE ON public.conta_pagar FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3379 (class 2620 OID 20480)
-- Name: conta_pagar validachavepessoacontapagar2; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoacontapagar2 BEFORE INSERT ON public.conta_pagar FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa();


--
-- TOC entry 3380 (class 2620 OID 20482)
-- Name: conta_pagar validachavepessoacontapagarpessoa_forn_id; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoacontapagarpessoa_forn_id BEFORE UPDATE ON public.conta_pagar FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa2();


--
-- TOC entry 3381 (class 2620 OID 20483)
-- Name: conta_pagar validachavepessoacontapagarpessoa_forn_id2; Type: TRIGGER; Schema: public; Owner: postgres
--

CREATE TRIGGER validachavepessoacontapagarpessoa_forn_id2 BEFORE INSERT ON public.conta_pagar FOR EACH ROW EXECUTE FUNCTION public.validachavepessoa2();


--
-- TOC entry 3356 (class 2606 OID 20236)
-- Name: usuario_acesso acesso_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario_acesso
    ADD CONSTRAINT acesso_fk FOREIGN KEY (acesso_id) REFERENCES public.acesso(id);


--
-- TOC entry 3359 (class 2606 OID 20324)
-- Name: nota_fiscal_compra conta_pagar_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nota_fiscal_compra
    ADD CONSTRAINT conta_pagar_fk FOREIGN KEY (conta_pagar_id) REFERENCES public.conta_pagar(id);


--
-- TOC entry 3364 (class 2606 OID 20470)
-- Name: vd_cp_loja_virt cupom_desc_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vd_cp_loja_virt
    ADD CONSTRAINT cupom_desc_fk FOREIGN KEY (cupom_desc_id) REFERENCES public.cup_desc(id);


--
-- TOC entry 3365 (class 2606 OID 20394)
-- Name: vd_cp_loja_virt endereco_cobranca_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vd_cp_loja_virt
    ADD CONSTRAINT endereco_cobranca_fk FOREIGN KEY (endereco_cobranca_id) REFERENCES public.endereco(id);


--
-- TOC entry 3366 (class 2606 OID 20399)
-- Name: vd_cp_loja_virt endereco_entrega_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vd_cp_loja_virt
    ADD CONSTRAINT endereco_entrega_fk FOREIGN KEY (endereco_entrega_id) REFERENCES public.endereco(id);


--
-- TOC entry 3367 (class 2606 OID 20405)
-- Name: vd_cp_loja_virt forma_pagamento_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vd_cp_loja_virt
    ADD CONSTRAINT forma_pagamento_fk FOREIGN KEY (forma_pagamento_id) REFERENCES public.forma_pagamento(id);


--
-- TOC entry 3360 (class 2606 OID 20367)
-- Name: nota_item_produto nota_fiscal_compra_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nota_item_produto
    ADD CONSTRAINT nota_fiscal_compra_fk FOREIGN KEY (nota_fiscal_compra_id) REFERENCES public.nota_fiscal_compra(id);


--
-- TOC entry 3368 (class 2606 OID 20412)
-- Name: vd_cp_loja_virt nota_fiscal_venda_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vd_cp_loja_virt
    ADD CONSTRAINT nota_fiscal_venda_fk FOREIGN KEY (nota_fiscal_venda_id) REFERENCES public.nota_fiscal_venda(id);


--
-- TOC entry 3358 (class 2606 OID 20311)
-- Name: imagem_produto produto_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.imagem_produto
    ADD CONSTRAINT produto_fk FOREIGN KEY (produto_id) REFERENCES public.produto(id);


--
-- TOC entry 3361 (class 2606 OID 20362)
-- Name: nota_item_produto produto_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nota_item_produto
    ADD CONSTRAINT produto_fk FOREIGN KEY (produto_id) REFERENCES public.produto(id);


--
-- TOC entry 3369 (class 2606 OID 20440)
-- Name: item_venda_loja produto_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item_venda_loja
    ADD CONSTRAINT produto_fk FOREIGN KEY (produto_id) REFERENCES public.produto(id);


--
-- TOC entry 3371 (class 2606 OID 20456)
-- Name: avaliacao_produto produto_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.avaliacao_produto
    ADD CONSTRAINT produto_fk FOREIGN KEY (produto_id) REFERENCES public.produto(id);


--
-- TOC entry 3357 (class 2606 OID 20241)
-- Name: usuario_acesso usuario_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.usuario_acesso
    ADD CONSTRAINT usuario_fk FOREIGN KEY (usuario_id) REFERENCES public.usuario(id);


--
-- TOC entry 3363 (class 2606 OID 20419)
-- Name: nota_fiscal_venda venda_compra_loja_virt__fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.nota_fiscal_venda
    ADD CONSTRAINT venda_compra_loja_virt__fk FOREIGN KEY (venda_compra_loja_virt_id) REFERENCES public.vd_cp_loja_virt(id);


--
-- TOC entry 3362 (class 2606 OID 20429)
-- Name: status_rastreio venda_compra_loja_virt_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.status_rastreio
    ADD CONSTRAINT venda_compra_loja_virt_fk FOREIGN KEY (venda_compra_loja_virt_id) REFERENCES public.vd_cp_loja_virt(id);


--
-- TOC entry 3370 (class 2606 OID 20445)
-- Name: item_venda_loja venda_compralojavirtual_fk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.item_venda_loja
    ADD CONSTRAINT venda_compralojavirtual_fk FOREIGN KEY (venda_compraloja_virtual_id) REFERENCES public.vd_cp_loja_virt(id);


-- Completed on 2025-06-03 10:00:57 -03

--
-- PostgreSQL database dump complete
--

