package controllers;

import awsWrappers.AwsCognitoIdentityProviderWrapper;
import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.typesafe.config.Config;
import models.UserSignUpRequest;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utilities.DynamoTables;

import javax.inject.Inject;

public class RegisterController extends Controller {

    private Config config;
    private FormFactory formFactory;

    @Inject
    public RegisterController(Config config, FormFactory formFactory) {
        this.config = config;
        this.formFactory = formFactory;
    }

    public Result register(Http.Request request) {
//        if (LoginChecker.isLoggedin(request)) {
//            return redirect(routes.HomeController.index());
//        }
        return ok(views.html.signUp.render());
    }

    public Result registerSubmit() {
        Form<UserSignUpRequest> userSignUpForm = formFactory.form(UserSignUpRequest.class).bindFromRequest();
        UserType user = awsSignUp(userSignUpForm);
        return redirect(routes.HomeController.index()).withCookies(
                Http.Cookie.builder("username", user.getUsername()).build(),
                Http.Cookie.builder("userType", userSignUpForm.value().get().getUserType()).build());
    }

    public UserType awsSignUp(Form<UserSignUpRequest> userSignUpForm) {
        UserSignUpRequest signUpRequest = userSignUpForm.get();
        AWSCognitoIdentityProvider cognitoClient = AwsCognitoIdentityProviderWrapper.getInstance();
        AdminCreateUserRequest cognitoRequest = new AdminCreateUserRequest()
                .withUserPoolId(config.getString("userPoolId"))
                .withUsername(signUpRequest.getUsername())
                .withUserAttributes(
                        new AttributeType()
                                .withName("email")
                                .withValue(signUpRequest.getEmail()),
                        new AttributeType()
                                .withName("name")
                                .withValue(signUpRequest.getFirstName()),
                        new AttributeType()
                                .withName("family_name")
                                .withValue(signUpRequest.getLastName()),
                        new AttributeType()
                                .withName("email_verified")
                                .withValue("true"))
                .withTemporaryPassword("TEMPoRARY_PASSWoRD1")
                .withMessageAction("SUPPRESS")
                .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .withForceAliasCreation(Boolean.FALSE);

        Item careerSyncUser = new Item()
                .withPrimaryKey("username", signUpRequest.getUsername())
                .with("firstName", signUpRequest.getFirstName())
                .with("surname", signUpRequest.getLastName())
                .with("email", signUpRequest.getEmail())
                .with("phoneNumber", signUpRequest.getPhoneNumber())
                .with("userType", signUpRequest.getUserType());
        DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_USERS.getName()).putItem(careerSyncUser);


        AdminCreateUserResult createUserResult = cognitoClient.adminCreateUser(cognitoRequest);
        UserType cognitoUser = createUserResult.getUser();

        return cognitoUser;

    }
}
