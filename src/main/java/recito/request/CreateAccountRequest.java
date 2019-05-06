package recito.request;

public class CreateAccountRequest {
    private String loginClient;
    private String passwordClient;
    private String emailClient;

    public CreateAccountRequest(String idClient, String passwordClient, String emailClient) {
        this.loginClient = idClient;
        this.passwordClient = passwordClient;
        this.emailClient = emailClient;
    }

    public String getLoginClient() {
        return loginClient;
    }

    public void setLoginClient(String loginClient) {
        this.loginClient = loginClient;
    }

    public String getPasswordClient() {
        return passwordClient;
    }

    public void setPasswordClient(String passwordClient) {
        this.passwordClient = passwordClient;
    }

    public String getEmailClient() {
        return emailClient;
    }

    public void setEmailClient(String emailClient) {
        this.emailClient = emailClient;
    }
}
