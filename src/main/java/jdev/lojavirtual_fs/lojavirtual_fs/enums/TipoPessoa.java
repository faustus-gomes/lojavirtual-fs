package jdev.lojavirtual_fs.lojavirtual_fs.enums;

public enum TipoPessoa {
    JURIDICA("Jurídica"),
    JURIDICA_FORNECEDOR("Jurídica e Fornecedor"),
    FISICA("Física");

    private String descricao;

    private TipoPessoa(String descri) {
        this.descricao = descri;
    }

    public String getDescricao() {
        return descricao;
    }
}
