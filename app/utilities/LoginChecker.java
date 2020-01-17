package utilities;

import awsWrappers.AwsCognitoIdentityProviderWrapper;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import play.mvc.Http;

public class LoginChecker {
    public static boolean isLoggedin(Http.Request request) {
        return request.cookies().getCookie("username").isPresent();
    }

    public static String getUsername(String accessToken) {
        GetUserRequest request = new GetUserRequest();
        request.withAccessToken(accessToken);

        AWSCognitoIdentityProvider cognitoCreate = AwsCognitoIdentityProviderWrapper.getInstance();

        try {
            return cognitoCreate.getUser(request).getUsername();
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
