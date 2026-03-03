package jdev.lojavirtual_fs.lojavirtual_fs.dto;

import java.io.Serializable;

public class ProductsEnvioEtiquetaDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String quantity;
    private String unitary_value;
    private String weight;
    private String width;
    private String height;
    private String length;
    private String unitary_price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnitary_value() {
        return unitary_value;
    }

    public void setUnitary_value(String unitary_value) {
        this.unitary_value = unitary_value;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getUnitary_price() {
        return unitary_price;
    }

    public void setUnitary_price(String unitary_price) {
        this.unitary_price = unitary_price;
    }
}
