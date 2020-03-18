package controllers;

import awsWrappers.AmazonDynamoDbClientWrapper;
import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.google.common.annotations.VisibleForTesting;
import models.UserAccountDetails;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utilities.DynamoAccessor;
import utilities.DynamoTables;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class EditAccountController extends Controller {
    private FormFactory formFactory;

    @Inject
    public EditAccountController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result editAccount(Http.Request request) {
        return ok(views.html.editAccount.render(DynamoAccessor.getInstance().getUserAccountDetails(request.cookie("username").value())));
    }

    public Result updateUserAccountDetails() {
        UserAccountDetails userAccountDetails = formFactory.form(UserAccountDetails.class).bindFromRequest().get();
        UpdateItemRequest updateRequest = new UpdateItemRequest();
        updateRequest.setTableName("CareerSync-Users");

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
        return redirect(routes.EditAccountController.editAccount());
    }
}
