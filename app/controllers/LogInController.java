package controllers;

import enums.DynamoTables;
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
        return ok(views.html.logIn.render(false));
    }

    public Result logInSubmit() {
        Form<UserSignInRequest> userSignInForm = formFactory.form(UserSignInRequest.class).bindFromRequest();
        try {
            String username = awsSignIn(userSignInForm);
            String userType = DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_USERS.getName())
                    .getItem("username", username)
                    .get("userType")
                    .toString();
            return redirect(routes.HomeController.index()).withCookies(
                    Http.Cookie.builder("username", username).build(),
                    Http.Cookie.builder("userType", userType).build());
        } catch (Exception e) {
            return badRequest(views.html.logIn.render(true));
        }
    }

    public String awsSignIn(Form<UserSignInRequest> signInRequestForm) {
        UserSignInRequest signInRequest = signInRequestForm.get();
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
        AuthenticationResultType authenticationResult = result.getAuthenticationResult();
        return LoginChecker.getInstance().getUsername(authenticationResult.getAccessToken());
    }
}
