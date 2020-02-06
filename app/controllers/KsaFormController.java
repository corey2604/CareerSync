package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

public class KsaFormController extends Controller {

    public Result loadForm(Http.Request request) {
        return ok(views.html.candidate.ksaForm.render());
    }
}
