package jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public class LeadRequestDTO {

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail deve ser válido")
    private String email;

    private String name;

    @NotBlank(message = "ID da campanha é obrigatório")
    @JsonProperty("campaignId")
    private String campaignId;

    public LeadRequestDTO() {
    }

    public LeadRequestDTO(String email, String name, String campaignId) {
        this.email = email;
        this.name = name;
        this.campaignId = campaignId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    @Override
    public String toString() {
        return "LeadRequestDTO{email='" + email + "', name='" + name + "', campaignId='" + campaignId + "'}";
    }
}
