package jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SendEmailSimpleDTO {

    @NotBlank(message = "E-mail destino é obrigatório")
    @Email(message = "E-mail deve ser válido")
    private String to;

    @NotBlank(message = "Assunto é obrigatório")
    private String subject;

    @NotBlank(message = "Conteúdo HTML é obrigatório")
    private String htmlContent;

    private String fromName;  // Nome do remetente (opcional)

    // Construtor padrão
    public SendEmailSimpleDTO() {}

    // Getters e Setters
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getHtmlContent() { return htmlContent; }
    public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }

    public String getFromName() { return fromName; }
    public void setFromName(String fromName) { this.fromName = fromName; }

    @Override
    public String toString() {
        return "SendEmailSimpleDTO{to='" + to + "', subject='" + subject + "'}";
    }
}
