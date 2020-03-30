package controllers;

import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.document.Item;
import models.KsaForm;
import models.UserKsas;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utilities.DynamoAccessor;
import Enums.DynamoTables;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class KsaFormController extends Controller {
    private FormFactory formFactory;

    @Inject
    public KsaFormController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result loadForm(Http.Request request) {
        boolean userHasKsas = DynamoAccessor.getInstance().doesUserHaveKsas(request.cookie("username").value());
        return ok(views.html.candidate.ksaForm.render(views.html.ksaFormContent.render(), userHasKsas));
    }

    public Result editKsas(Http.Request request) {
        UserKsas userKsas = DynamoAccessor.getInstance().getKsasForUser(request.cookie("username").value());
        return ok(views.html.candidate.ksaForm.render(views.html.populatedKsaFormContent.render(userKsas), true));
    }

    public Result submitForm(Http.Request request) {
        KsaForm ksaForm = formFactory.form(KsaForm.class).bindFromRequest(request).get();
        DynamoAccessor.getInstance().putKsasInTable(request.cookie("username").value(), ksaForm);
        return redirect(routes.HomeController.index());
    }

}
