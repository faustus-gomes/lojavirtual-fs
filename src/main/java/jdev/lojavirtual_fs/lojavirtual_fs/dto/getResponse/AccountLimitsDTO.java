package jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse;

public class AccountLimitsDTO {

    private int contacts;        // Limite de contatos
    private int monthlyEmails;   // E-mails mensais permitidos
    private int users;           // Número de usuários
    private int campaigns;       // Limite de campanhas
    private int automationRules; // Limite de automações
    private int landingPages;    // Limite de landing pages
    private int apiCalls;        // Limite de chamadas API

    public int getContacts() {
        return contacts;
    }

    public void setContacts(int contacts) {
        this.contacts = contacts;
    }

    public int getMonthlyEmails() {
        return monthlyEmails;
    }

    public void setMonthlyEmails(int monthlyEmails) {
        this.monthlyEmails = monthlyEmails;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public int getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(int campaigns) {
        this.campaigns = campaigns;
    }

    public int getAutomationRules() {
        return automationRules;
    }

    public void setAutomationRules(int automationRules) {
        this.automationRules = automationRules;
    }

    public int getLandingPages() {
        return landingPages;
    }

    public void setLandingPages(int landingPages) {
        this.landingPages = landingPages;
    }

    public int getApiCalls() {
        return apiCalls;
    }

    public void setApiCalls(int apiCalls) {
        this.apiCalls = apiCalls;
    }

    @Override
    public String toString() {
        return "AccountLimitsDTO{contacts=" + contacts + ", monthlyEmails=" + monthlyEmails + "}";
    }
}
