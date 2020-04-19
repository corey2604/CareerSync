package utilities;

import awsWrappers.AwsCognitoIdentityProviderWrapper;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import play.mvc.Http;

public class LoginChecker {
    private static LoginChecker loginChecker = null;

    private LoginChecker() {
        //Private Constructor
    }

    //Factory method
    public static LoginChecker getInstance() {
        if (loginChecker == null) {
            loginChecker = new LoginChecker();
        }
        return loginChecker;
    }

    public boolean isLoggedin(Http.Request request) {
        return request.cookies().getCookie("username").isPresent();
    }

    public String getUsername(String accessToken) {
        GetUserRequest request = new GetUserRequest();
        request.withAccessToken(accessToken);

        AWSCognitoIdentityProvider cognitoCreate = AwsCognitoIdentityProviderWrapper.getInstance();

        try {
            return cognitoCreate.getUser(request).getUsername();
        } catch (Exception e) {
            throw new RuntimeException("User not found.");
        }
    }
}
