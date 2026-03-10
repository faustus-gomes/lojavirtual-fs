package jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas;

import lombok.Data;

import java.util.List;

@Data
public class AsaasPageResponseDTO <T>{
    private List<T> data;
    private int totalCount;
    private boolean hasMore;
    private int limit;
    private int offset;
}
