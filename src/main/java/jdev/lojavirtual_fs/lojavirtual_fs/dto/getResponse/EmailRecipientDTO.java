package jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse;

public class EmailRecipientDTO {

    private String contactId;  // ID do contato (se já existir)
    private String email;      // E-mail direto

    // Construtor padrão
    public EmailRecipientDTO() {}

    // Construtor com parâmetros
    public EmailRecipientDTO(String contactId, String email) {
        this.contactId = contactId;
        this.email = email;
    }

    // Getters e Setters
    public String getContactId() { return contactId; }
    public void setContactId(String contactId) { this.contactId = contactId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Factory methods para facilitar criação
    public static EmailRecipientDTO byContactId(String contactId) {
        return new EmailRecipientDTO(contactId, null);
    }

    public static EmailRecipientDTO byEmail(String email) {
        return new EmailRecipientDTO(null, email);
    }

    @Override
    public String toString() {
        return "EmailRecipientDTO{" +
                (contactId != null ? "contactId='" + contactId + "'" : "email='" + email + "'") +
                "}";
    }
}
