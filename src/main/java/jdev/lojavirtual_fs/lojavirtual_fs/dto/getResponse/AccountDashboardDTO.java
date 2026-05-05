package jdev.lojavirtual_fs.lojavirtual_fs.dto.getResponse;

public class AccountDashboardDTO {

    private AccountInfoDTO accountInfo;
    private AccountLimitsDTO limits;
    private AccountStatsDTO stats;
    private String status;  // "active", "trial", "expired"
    private String planName; // Nome do plano

    public AccountInfoDTO getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfoDTO accountInfo) {
        this.accountInfo = accountInfo;
    }

    public AccountLimitsDTO getLimits() {
        return limits;
    }

    public void setLimits(AccountLimitsDTO limits) {
        this.limits = limits;
    }

    public AccountStatsDTO getStats() {
        return stats;
    }

    public void setStats(AccountStatsDTO stats) {
        this.stats = stats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }
}
