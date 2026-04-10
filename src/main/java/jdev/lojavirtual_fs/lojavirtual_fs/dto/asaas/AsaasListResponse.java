package jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas;

import lombok.Data;

import java.util.List;

@Data
public class AsaasListResponse<T> {
    private List<T> data;
    private int totalCount;
    private int offset;
    private int limit;
}
