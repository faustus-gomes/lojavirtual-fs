package jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
public class LeadResponseDTO {

    @JsonProperty("contactId")
    private String contactId;

    private String email;
    private String name;
    private String status;

    @JsonProperty("campaignId")
    private String campaignId;

    public LeadResponseDTO() {
    }

    public LeadResponseDTO(String contactId, String email, String name, String status, String campaignId) {
        this.contactId = contactId;
        this.email = email;
        this.name = name;
        this.status = status;
        this.campaignId = campaignId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    @Override
    public String toString() {
        return "LeadResponseDTO{contactId='" + contactId + "', email='" + email + "', status='" + status + "'}";
    }
}
