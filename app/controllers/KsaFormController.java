package controllers;

import models.KsaForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;

public class KsaFormController extends Controller {
    private FormFactory formFactory;

    @Inject
    public KsaFormController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result loadForm(Http.Request request) {
        KsaForm jobDescription = formFactory.form(KsaForm.class).bindFromRequest().get();
        return ok(views.html.candidate.ksaForm.render());
    }
}
