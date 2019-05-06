package recito.request;

public class SignInRequest {
    private String login;
    private String passwordClient;

    public SignInRequest(String login, String passwordClient) {
        this.login = login;
        this.passwordClient = passwordClient;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordClient() {
        return passwordClient;
    }

    public void setPasswordClient(String passwordClient) {
        this.passwordClient = passwordClient;
    }
}
