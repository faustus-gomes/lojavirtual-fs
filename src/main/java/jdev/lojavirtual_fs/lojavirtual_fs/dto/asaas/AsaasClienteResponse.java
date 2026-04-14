package jdev.lojavirtual_fs.lojavirtual_fs.dto.asaas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsaasClienteResponse {
    private String id;
    private String name;
    private String email;

    @JsonProperty("cpfCnpj")
    private String cpfCnpj;

    @JsonProperty("mobilePhone")
    private String mobilePhone;

    private String phone;

    @JsonProperty("postalCode")
    private String postalCode;

    private String address;

    @JsonProperty("addressNumber")
    private String addressNumber;
    private String complement;
    private String province;
    private String city;
    private String state;
    private String country;

    @JsonProperty("personType")
    private String personType;

    @JsonProperty("externalReference")
    private String externalReference;

    private String observations;

    @JsonProperty("dateCreated")
    private LocalDate dateCreated;
}
