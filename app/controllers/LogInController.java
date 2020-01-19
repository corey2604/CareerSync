package controllers;

import awsWrappers.AwsCognitoIdentityProviderWrapper;
import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.typesafe.config.Config;
import models.UserSignInRequest;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utilities.DynamoTables;
import utilities.LoginChecker;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class LogInController extends Controller {
    private Config config;
    private FormFactory formFactory;

    @Inject
    public LogInController(Config config, FormFactory formFactory) {
        this.config = config;
        this.formFactory = formFactory;
    }

    public Result logIn(Http.Request request) {
//        if (LoginChecker.isLoggedin(request)) {
//            return redirect(routes.HomeController.index());
//        }
        return ok(views.html.logIn.render());
    }

    public Result logInSubmit() {
        Form<UserSignInRequest> userSignInForm = formFactory.form(UserSignInRequest.class).bindFromRequest();
        String username = awsSignIn(userSignInForm);
        String userType = DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_USERS.getName())
                .getItem("username", username)
                .get("userType")
                .toString();
        return redirect(routes.HomeController.index()).withCookies(
                Http.Cookie.builder("username", username).build(),
                Http.Cookie.builder("userType", userType).build());
    }

    public String awsSignIn(Form<UserSignInRequest> signInRequestForm) {
        UserSignInRequest signInRequest = signInRequestForm.get();
        AuthenticationResultType authenticationResult;
        AWSCognitoIdentityProvider cognitoClient = AwsCognitoIdentityProviderWrapper.getInstance();

        final Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", signInRequest.getUsername());
        authParams.put("PASSWORD", signInRequest.getPassword());

        final AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest();
        authRequest.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withClientId(config.getString("clientId"))
                .withUserPoolId(config.getString("userPoolId"))
                .withAuthParameters(authParams);

        AdminInitiateAuthResult result = cognitoClient.adminInitiateAuth(authRequest);
        authenticationResult = result.getAuthenticationResult();
        //If the challenge is required new Password validates if it has the new password variable.
        if ("NEW_PASSWORD_REQUIRED".equals(result.getChallengeName())) {
            //we still need the username
            final Map<String, String> challengeResponses = new HashMap<>();
            challengeResponses.put("USERNAME", signInRequest.getUsername());
            challengeResponses.put("PASSWORD", signInRequest.getPassword());
            //add the new password to the params map
            challengeResponses.put("NEW_PASSWORD", signInRequest.getPassword());
            //populate the challenge response
            final AdminRespondToAuthChallengeRequest request =
                    new AdminRespondToAuthChallengeRequest();
            request.withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                    .withChallengeResponses(challengeResponses)
                    .withClientId(config.getString("clientId"))
                    .withUserPoolId(config.getString("userPoolId"))
                    .withSession(result.getSession());

            AdminRespondToAuthChallengeResult resultChallenge =
                    cognitoClient.adminRespondToAuthChallenge(request);
            authenticationResult = resultChallenge.getAuthenticationResult();
        }

        return LoginChecker.getUsername(authenticationResult.getAccessToken());
    }
}
