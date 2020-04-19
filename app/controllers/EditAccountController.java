package controllers;

import enums.CareerSyncSuccessMessage;
import awsWrappers.AmazonDynamoDbClientWrapper;
import awsWrappers.AwsCognitoIdentityProviderWrapper;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordRequest;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import models.UserAccountDetails;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import enums.CareerSyncErrorMessages;
import utilities.DynamoAccessor;
import enums.DynamoTables;
import utilities.ValidationHelper;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EditAccountController extends Controller {
    private FormFactory formFactory;

    @Inject
    public EditAccountController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result editAccount(Http.Request request) {
        return ok(views.html.editAccount.render(DynamoAccessor.getInstance().getUserAccountDetails(request.cookie("username").value()),
                Optional.empty(),
                Optional.empty(),
                request.cookie("userType").value(),
                DynamoAccessor.getInstance().doesUserHaveKsas(request.cookie("username").value())));
    }

    public Result updateUserAccountDetails(Http.Request request) {
        UserAccountDetails userAccountDetails = formFactory.form(UserAccountDetails.class).bindFromRequest().get();
        boolean passwordIsEmpty = userAccountDetails.getPassword().isEmpty();
        if (!passwordIsEmpty && !ValidationHelper.getInstance().passwordIsValid(userAccountDetails.getPassword())) {
            return badRequest(views.html.editAccount.render(userAccountDetails,
                    Optional.of(CareerSyncErrorMessages.PASSWORD_DOES_NOT_CONFORM),
                    Optional.empty(),
                    request.cookie("userType").value(),
                    DynamoAccessor.getInstance().doesUserHaveKsas(request.cookie("username").value())));
        } else if (!passwordIsEmpty) {
            AWSCognitoIdentityProvider cognitoClient = AwsCognitoIdentityProviderWrapper.getInstance();
            AdminSetUserPasswordRequest setUserPasswordRequest = new AdminSetUserPasswordRequest()
                    .withPassword(userAccountDetails.getPassword())
                    .withUsername(userAccountDetails.getUsername())
                    .withPermanent(true)
                    .withUserPoolId("eu-west-1_FehhvQScE");

            cognitoClient.adminSetUserPassword(setUserPasswordRequest);
        }

        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setTableName(DynamoTables.CAREER_SYNC_USERS.getName());

        Map<String, AttributeValue> keysMap = new HashMap<String, AttributeValue>();
        keysMap.put("username", new AttributeValue(userAccountDetails.getUsername()));
        updateRequest.setKey(keysMap);

        /* Create a Map of attributes to be updated */
        Map<String, AttributeValueUpdate> map = new HashMap<>();
        map.put("firstName", new AttributeValueUpdate(new AttributeValue(userAccountDetails.getFirstName()),"PUT"));
        map.put("surname", new AttributeValueUpdate(new AttributeValue(userAccountDetails.getSurname()),"PUT"));
        map.put("email", new AttributeValueUpdate(new AttributeValue(userAccountDetails.getEmailAddress()),"PUT"));
        map.put("phoneNumber", new AttributeValueUpdate(new AttributeValue(userAccountDetails.getPhoneNumber()),"PUT"));
        updateRequest.setAttributeUpdates(map);

        try {
            AmazonDynamoDbClientWrapper.getInstance().updateItem(updateRequest);
        } catch (AmazonServiceException e) {
            System.out.println(e.getErrorMessage());
        }
        return ok(views.html.editAccount.render(userAccountDetails,
                Optional.empty(),
                Optional.of(CareerSyncSuccessMessage.DETAILS_UPDATED),
                request.cookie("userType").value(),
                DynamoAccessor.getInstance().doesUserHaveKsas(request.cookie("username").value())));
    }
}
