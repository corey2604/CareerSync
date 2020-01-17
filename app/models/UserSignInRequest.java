package models;

public class UserSignInRequest {
    private String username;
    private String password;

    public UserSignInRequest(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserSignInRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
