package jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailContentDTO {

        private String html;   // Conteúdo HTML do e-mail
        private String text;   // Versão texto puro (opcional)
        private String subject; // Assunto do e-mail

        // Construtor padrão
        public EmailContentDTO() {}

        // Construtor com parâmetros
        public EmailContentDTO(String html, String text, String subject) {
            this.html = html;
            this.text = text;
            this.subject = subject;
        }

        // Getters e Setters
        public String getHtml() { return html; }
        public void setHtml(String html) { this.html = html; }

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

        // Builder pattern para facilitar a criação
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String html;
            private String text;
            private String subject;

            public Builder html(String html) { this.html = html; return this; }
            public Builder text(String text) { this.text = text; return this; }
            public Builder subject(String subject) { this.subject = subject; return this; }

            public EmailContentDTO build() {
                return new EmailContentDTO(html, text, subject);
            }
        }

    @Override
    public String toString() {
        return "EmailContentDTO{subject='" + subject + "', html=" + (html != null ? "present" : "null") + "}";
    }
}
