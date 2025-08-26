-- Flyway migration script to fix validachavepessoa functions
-- Date: 22-08-2025
-- Description: Corrige as funções de trigger que referenciam campo inexistente

-- Corrigir a função validachavepessoa2() para INSERT
CREATE OR REPLACE FUNCTION validachavepessoa2()
RETURNS TRIGGER AS $$
DECLARE
    existe INTEGER;
BEGIN
    -- Usar pessoa_id em vez de pessoa_forn_id
    existe = (SELECT COUNT(1) FROM pessoa_fisica WHERE id = NEW.pessoa_id);

    -- Verificar se existe o registro em pessoa_fisica
    IF existe = 0 THEN
        RAISE EXCEPTION 'Pessoa com ID % não existe na tabela pessoa_fisica', NEW.pessoa_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Corrigir a função validachavepessoa() para UPDATE
CREATE OR REPLACE FUNCTION validachavepessoa()
RETURNS TRIGGER AS $$
DECLARE
    existe INTEGER;
BEGIN
    -- Usar pessoa_id em vez de pessoa_forn_id
    existe = (SELECT COUNT(1) FROM pessoa_fisica WHERE id = NEW.pessoa_id);

    -- Verificar se existe o registro em pessoa_fisica
    IF existe = 0 THEN
        RAISE EXCEPTION 'Pessoa com ID % não existe na tabela pessoa_fisica', NEW.pessoa_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Recriar as triggers para garantir que estão usando as funções corrigidas
DROP TRIGGER IF EXISTS validachavepessoa ON usuario;
DROP TRIGGER IF EXISTS validachavepessoa2 ON usuario;

CREATE TRIGGER validachavepessoa
    BEFORE UPDATE ON usuario
    FOR EACH ROW EXECUTE FUNCTION validachavepessoa();

CREATE TRIGGER validachavepessoa2
    BEFORE INSERT ON usuario
    FOR EACH ROW EXECUTE FUNCTION validachavepessoa2();

-- Comentário para documentação
COMMENT ON FUNCTION validachavepessoa() IS 'Valida se pessoa_id existe em pessoa_fisica durante UPDATE';
COMMENT ON FUNCTION validachavepessoa2() IS 'Valida se pessoa_id existe em pessoa_fisica durante INSERT';