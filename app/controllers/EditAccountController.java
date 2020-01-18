package controllers;

import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.document.Item;
import models.UserAccountDetails;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class EditAccountController extends Controller {

    public Result editAccount(Http.Request request) {
        Item userAccountDetails = DynamoDbTableProvider.getTable("CareerSync-Users").getItem("username", request.cookie("username").value());
        UserAccountDetails fakeUserDetails =
                new UserAccountDetails(userAccountDetails.get("username").toString(),
                        userAccountDetails.get("firstName").toString(),
                        userAccountDetails.get("surname").toString(),
                        userAccountDetails.get("email").toString(),
                        userAccountDetails.get("phoneNumber").toString());
        return ok(views.html.editAccount.render(fakeUserDetails));
    }
}
