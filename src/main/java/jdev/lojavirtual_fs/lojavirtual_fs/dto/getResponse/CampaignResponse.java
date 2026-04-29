package jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CampaignResponse {

    @JsonProperty("campaignId")
    private String id;
    private String name;
    private String description;
    @JsonProperty("isDefault")
    private boolean defaultCampaign;

    // Construtor padrão (necessário para o Jackson)
    public CampaignResponse() {}

    // Construtor com parâmetros
    public CampaignResponse(String id, String name, String description, boolean defaultCampaign) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.defaultCampaign = defaultCampaign;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDefaultCampaign() {
        return defaultCampaign;
    }

    public void setDefaultCampaign(boolean defaultCampaign) {
        this.defaultCampaign = defaultCampaign;
    }

    @Override
    public String toString() {
        return "CampaignResponse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", defaultCampaign=" + defaultCampaign +
                '}';
    }
}
