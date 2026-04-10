package jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsaasClienteRequest {
    private String name;              // Nome do cliente
    private String email;             // Email do cliente
    private String cpfCnpj;          // CPF ou CNPJ
    private String mobilePhone;       // Telefone celular
    private String phone;             // Telefone fixo (opcional)
    private String postalCode;        // CEP
    private String address;           // Endereço
    private String addressNumber;     // Número
    private String complement;        // Complemento
    private String province;          // Bairro
    private String city;              // Cidade
    private String state;             // Estado (UF)
    private String country;           // País (padrão: "BR")
    private String personType;        // "FISICA" ou "JURIDICA"
    private String externalReference; // Seu ID local
    private String observations;      // Observações
}
