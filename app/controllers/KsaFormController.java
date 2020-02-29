package controllers;

import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.document.Item;
import models.KsaForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utilities.DynamoTables;

import javax.inject.Inject;
import java.util.stream.Collectors;

public class KsaFormController extends Controller {
    private FormFactory formFactory;

    @Inject
    public KsaFormController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result loadForm(Http.Request request) {
        return ok(views.html.candidate.ksaForm.render(views.html.ksaFormContent.render()));
    }

    public Result submitForm(Http.Request request) {
        KsaForm ksaForm = formFactory.form(KsaForm.class).bindFromRequest(request).get();
        putKsasInTable(request.cookie("username").value(), ksaForm);
        return redirect(routes.HomeController.index());
    }

    private void putKsasInTable(String username, KsaForm ksaForm) {
        Item jobDescriptionItem = new Item()
                .withPrimaryKey("username", username)
                .with("qualificationLevel", ksaForm.getQualificationLevel())
                .with("qualificationArea", ksaForm.getQualificationArea())
                .withList("communicationSkills", ksaForm.getCommunicationSkills().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("peopleSkills", ksaForm.getPeopleSkills().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("financialKnowledgeAndSkills", ksaForm.getFinancialKnowledgeAndSkills().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("thinkingAndAnalysis", ksaForm.getThinkingAndAnalysis().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("creativeOrInnovative", ksaForm.getCreativeOrInnovative().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("administrativeOrOrganisational", ksaForm.getAdministrativeOrOrganisational().stream().filter(item -> item != null).collect(Collectors.toList()));
        DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_USER_KSAS.getName()).putItem(jobDescriptionItem);
    }
}
