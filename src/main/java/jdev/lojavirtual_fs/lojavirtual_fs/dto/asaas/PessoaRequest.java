package jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas;

import lombok.Data;

import java.util.Date;

@Data
public class PessoaRequest {
    private String nome;
    private String email;
    private String telefone;
    private String tipoPessoa;  // FISICA ou JURIDICA

    // Campos para Pessoa Física
    private String cpf;
    private Date dataNascimento;

    // Campos para Pessoa Jurídica
    private String cnpj;
    private String inscEstadual;

    //Empresa
    private Long empresaId;

}
