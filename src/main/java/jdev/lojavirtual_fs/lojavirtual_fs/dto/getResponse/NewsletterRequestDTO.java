package jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public class NewsletterRequestDTO {

    @NotBlank(message = "Nome da newsletter é obrigatório")
    private String name;

    @NotBlank(message = "Assunto é obrigatório")
    private String subject;

    @NotNull(message = "Conteúdo é obrigatório")
    private EmailContentDTO content;

    private List<String> campaignIds;        // IDs das campanhas/listas
    private List<EmailRecipientDTO> recipients; // Recipientes específicos

    // Campos opcionais para configurações avançadas
    private Map<String, String> from;        // Remetente (name, email)
    private Boolean trackOpens;              // Rastrear aberturas (padrão true)
    private Boolean trackClicks;             // Rastrear cliques (padrão true)
    private String replyTo;                  // E-mail para resposta

    // Construtor padrão
    public NewsletterRequestDTO() {}

    // Getters e Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public EmailContentDTO getContent() { return content; }
    public void setContent(EmailContentDTO content) { this.content = content; }

    public List<String> getCampaignIds() { return campaignIds; }
    public void setCampaignIds(List<String> campaignIds) { this.campaignIds = campaignIds; }

    public List<EmailRecipientDTO> getRecipients() { return recipients; }
    public void setRecipients(List<EmailRecipientDTO> recipients) { this.recipients = recipients; }

    public Map<String, String> getFrom() { return from; }
    public void setFrom(Map<String, String> from) { this.from = from; }

    public Boolean getTrackOpens() { return trackOpens; }
    public void setTrackOpens(Boolean trackOpens) { this.trackOpens = trackOpens; }

    public Boolean getTrackClicks() { return trackClicks; }
    public void setTrackClicks(Boolean trackClicks) { this.trackClicks = trackClicks; }

    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final NewsletterRequestDTO dto = new NewsletterRequestDTO();

        public Builder name(String name) { dto.name = name; return this; }
        public Builder subject(String subject) { dto.subject = subject; return this; }
        public Builder content(EmailContentDTO content) { dto.content = content; return this; }
        public Builder campaignIds(List<String> campaignIds) { dto.campaignIds = campaignIds; return this; }
        public Builder recipients(List<EmailRecipientDTO> recipients) { dto.recipients = recipients; return this; }
        public Builder from(String name, String email) {
            dto.from = Map.of("name", name, "email", email);
            return this;
        }
        public Builder trackOpens(Boolean trackOpens) { dto.trackOpens = trackOpens; return this; }
        public Builder trackClicks(Boolean trackClicks) { dto.trackClicks = trackClicks; return this; }

        public NewsletterRequestDTO build() { return dto; }
    }

    @Override
    public String toString() {
        return "NewsletterRequestDTO{name='" + name + "', subject='" + subject + "'}";
    }
}
