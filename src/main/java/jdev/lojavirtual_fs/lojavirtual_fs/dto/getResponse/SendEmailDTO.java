package jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SendEmailDTO {
    @NotBlank(message = "E-mail destino é obrigatório")
    @Email(message = "E-mail deve ser válido")
    private String to;

    @NotBlank(message = "Assunto é obrigatório")
    private String subject;

    @NotBlank(message = "Conteúdo HTML é obrigatório")
    private String htmlContent;

    // Getters e Setters
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getHtmlContent() { return htmlContent; }
    public void setHtmlContent(String htmlContent) { this.htmlContent = htmlContent; }
}
