package jdev.lojavirtual_fs.lojavirtual_fs.enums;

public enum TipoEndereco {
    COBRANCA("Cobrança"),
    ENTREGA("Entrega");

    private String descricao;
    private TipoEndereco(String descricao) {
        /**TO-DO Construtor*/
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public  String toString() {
        return this.descricao;
    }
}
