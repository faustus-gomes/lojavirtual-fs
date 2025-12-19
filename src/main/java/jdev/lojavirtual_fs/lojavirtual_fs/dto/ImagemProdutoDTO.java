package jdev.lojavirtual_fs.lojavirtual_fs.dto;

import java.io.Serializable;

public class ImagemProdutoDTO implements Serializable {

    private Long id;
    private String imagemOriginal;
    private String imagemMiniatura;
    private Long produto;
    private long empresa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImagemOriginal() {
        return imagemOriginal;
    }

    public void setImagemOriginal(String imagemOriginal) {
        this.imagemOriginal = imagemOriginal;
    }

    public String getImagemMiniatura() {
        return imagemMiniatura;
    }

    public void setImagemMiniatura(String imagemMiniatura) {
        this.imagemMiniatura = imagemMiniatura;
    }

    public Long getProduto() {
        return produto;
    }

    public void setProduto(Long produto) {
        this.produto = produto;
    }

    public long getEmpresa() {
        return empresa;
    }

    public void setEmpresa(long empresa) {
        this.empresa = empresa;
    }
}
