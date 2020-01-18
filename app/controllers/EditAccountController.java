package controllers;

import models.UserAccountDetails;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class EditAccountController extends Controller {

    public Result editAccount(Http.Request request) {
        UserAccountDetails fakeUserDetails = new UserAccountDetails("test1", "test2", "test3", "test4", "test5");
        return ok(views.html.editAccount.render(fakeUserDetails));
    }
}
