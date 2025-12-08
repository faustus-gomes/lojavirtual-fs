package jdev.lojavirtual_fs.lojavirtual_fs.dto;

import java.io.Serializable;

public class BillingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Boolean free;
    private Boolean database;

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public Boolean getDatabase() {
        return database;
    }

    public void setDatabase(Boolean database) {
        this.database = database;
    }
}
