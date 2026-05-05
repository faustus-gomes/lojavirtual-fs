package jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse;

public class AccountStatsDTO {

    private int totalContacts;        // Total de contatos
    private int activeContacts;       // Contatos ativos
    private int inactiveContacts;     // Contatos inativos
    private int emailsSentThisMonth;  // E-mails enviados no mês
    private int emailsRemaining;      // E-mails restantes
    private double openRate;          // Taxa média de abertura (%)
    private double clickRate;         // Taxa média de cliques (%)
    private double bounceRate;        // Taxa de rejeição (%)
    private int campaignsSent;        // Campanhas enviadas
    private int campaignsActive;      // Campanhas ativas

    public int getTotalContacts() {
        return totalContacts;
    }

    public void setTotalContacts(int totalContacts) {
        this.totalContacts = totalContacts;
    }

    public int getActiveContacts() {
        return activeContacts;
    }

    public void setActiveContacts(int activeContacts) {
        this.activeContacts = activeContacts;
    }

    public int getInactiveContacts() {
        return inactiveContacts;
    }

    public void setInactiveContacts(int inactiveContacts) {
        this.inactiveContacts = inactiveContacts;
    }

    public int getEmailsSentThisMonth() {
        return emailsSentThisMonth;
    }

    public void setEmailsSentThisMonth(int emailsSentThisMonth) {
        this.emailsSentThisMonth = emailsSentThisMonth;
    }

    public int getEmailsRemaining() {
        return emailsRemaining;
    }

    public void setEmailsRemaining(int emailsRemaining) {
        this.emailsRemaining = emailsRemaining;
    }

    public double getOpenRate() {
        return openRate;
    }

    public void setOpenRate(double openRate) {
        this.openRate = openRate;
    }

    public double getClickRate() {
        return clickRate;
    }

    public void setClickRate(double clickRate) {
        this.clickRate = clickRate;
    }

    public double getBounceRate() {
        return bounceRate;
    }

    public void setBounceRate(double bounceRate) {
        this.bounceRate = bounceRate;
    }

    public int getCampaignsSent() {
        return campaignsSent;
    }

    public void setCampaignsSent(int campaignsSent) {
        this.campaignsSent = campaignsSent;
    }

    public int getCampaignsActive() {
        return campaignsActive;
    }

    public void setCampaignsActive(int campaignsActive) {
        this.campaignsActive = campaignsActive;
    }

    @Override
    public String toString() {
        return "AccountStatsDTO{totalContacts=" + totalContacts + ", emailsRemaining=" + emailsRemaining + "}";
    }
}
