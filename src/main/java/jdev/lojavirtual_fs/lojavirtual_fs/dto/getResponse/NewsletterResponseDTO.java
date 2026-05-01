package jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewsletterResponseDTO {

    @JsonProperty("newsletterId")
    private String newsletterId;

    private String name;
    private String subject;
    private String status;

    @JsonProperty("createdOn")
    private String createdOn;

    // Construtor padrão
    public NewsletterResponseDTO() {}

    // Construtor com parâmetros
    public NewsletterResponseDTO(String newsletterId, String name, String subject, String status, String createdOn) {
        this.newsletterId = newsletterId;
        this.name = name;
        this.subject = subject;
        this.status = status;
        this.createdOn = createdOn;
    }

    // Getters e Setters
    public String getNewsletterId() { return newsletterId; }
    public void setNewsletterId(String newsletterId) { this.newsletterId = newsletterId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedOn() { return createdOn; }
    public void setCreatedOn(String createdOn) { this.createdOn = createdOn; }

    @Override
    public String toString() {
        return "NewsletterResponseDTO{newsletterId='" + newsletterId + "', name='" + name + "', status='" + status + "'}";
    }
}
