package controllers;

import awsWrappers.AwsCognitoIdentityProviderWrapper;
import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.typesafe.config.Config;
import models.UserSignUpRequest;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import Enums.CareerSyncErrorMessages;
import utilities.DynamoAccessor;
import Enums.DynamoTables;
import utilities.ValidationHelper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        return ok(views.html.signUp.render(Collections.emptyList(), Optional.empty()));
    }

    public Result registerSubmit() {
        UserSignUpRequest userSignUpRequest = formFactory.form(UserSignUpRequest.class).get();
        List<CareerSyncErrorMessages> errorMessages = new ArrayList<>();
        DynamoAccessor.getInstance().getAllUsernames();
        if (DynamoAccessor.getInstance().getAllUsernames().contains(userSignUpRequest.getUsername())){
            errorMessages.add(CareerSyncErrorMessages.USERNAME_TAKEN);
        }
        if (!userSignUpRequest.getPhoneNumber().matches("\\d+")) {
            errorMessages.add(CareerSyncErrorMessages.INVALID_PHONE_NUMBER);
        }
        if (!ValidationHelper.getInstance().passwordIsValid(userSignUpRequest.getPassword())) {
            errorMessages.add(CareerSyncErrorMessages.PASSWORD_DOES_NOT_CONFORM);
        }
        if (errorMessages.size() == 0){
            try {
                UserType user = awsSignUp(userSignUpRequest);
                return redirect(routes.HomeController.index()).withCookies(
                        Http.Cookie.builder("username", user.getUsername()).build(),
                        Http.Cookie.builder("userType", userSignUpRequest.getUserType()).build());
            } catch (Exception e) {
                errorMessages.add(CareerSyncErrorMessages.GENERAL_SUBMISSION_ERROR);
            }
        }
        return badRequest(views.html.signUp.render(errorMessages, Optional.of(userSignUpRequest)));
    }

    private UserType awsSignUp(UserSignUpRequest signUpRequest) {
        AWSCognitoIdentityProvider cognitoClient = AwsCognitoIdentityProviderWrapper.getInstance();
        String userPoolId = config.getString("userPoolId");
        AdminCreateUserRequest cognitoRequest = new AdminCreateUserRequest()
                .withUserPoolId(userPoolId)
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

        AdminSetUserPasswordRequest setUserPasswordRequest = new AdminSetUserPasswordRequest()
                .withPassword(signUpRequest.getPassword())
                .withUsername(signUpRequest.getUsername())
                .withPermanent(true)
                .withUserPoolId(userPoolId);

        cognitoClient.adminSetUserPassword(setUserPasswordRequest);

        return cognitoUser;

    }
}
