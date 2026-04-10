package jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsaasClienteResponse {
    private String id;
    private String name;
    private String email;
    private String cpfCnpj;
    private String mobilePhone;
    private String phone;
    private String postalCode;
    private String address;
    private String addressNumber;
    private String complement;
    private String province;
    private String city;
    private String state;
    private String country;
    private String personType;
    private String externalReference;
    private String observations;
    private LocalDateTime dateCreated;
}
