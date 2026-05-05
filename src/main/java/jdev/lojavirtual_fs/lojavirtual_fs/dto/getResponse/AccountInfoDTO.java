package jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
public class AccountInfoDTO {

    @JsonProperty("accountId")
    private String accountId;

    private String email;
    private String firstName;
    private String lastName;
    private String timezone;
    private String language;

    @JsonProperty("isActive")
    private boolean active;

    @JsonProperty("createdOn")
    private LocalDateTime createdOn;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        return "AccountInfoDTO{email='" + email + "', accountId='" + accountId + "'}";
    }
}
