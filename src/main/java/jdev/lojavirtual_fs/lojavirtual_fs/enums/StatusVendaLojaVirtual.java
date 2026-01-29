package jdev.lojavirtual_fs.lojavirtual_fs.enums;

public enum StatusVendaLojaVirtual {

    FINALIZADA("Finalizada"),
    CANCELADA("Cancelada"),
    ABANDONOU_CARRINHO("Abandou Carrinho");

    private  String desccricao = "";

    StatusVendaLojaVirtual(String valor) {
        this.desccricao= valor;
    }

    public String getDesccricao() {
        return desccricao;
    }

    public void setDesccricao(String desccricao) {
        this.desccricao = desccricao;
    }

    @Override
    public String toString() {
        return this.desccricao;
    }
}
