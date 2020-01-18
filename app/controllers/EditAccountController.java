package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class EditAccountController extends Controller {

    public Result editAccount(Http.Request request) {
        return ok(views.html.editAccount.render());
    }
}
