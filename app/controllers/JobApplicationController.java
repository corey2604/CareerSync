package controllers;

import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.document.Item;
import models.JobDescription;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utilities.DynamoTables;

import javax.inject.Inject;

public class JobApplicationController extends Controller {

    private FormFactory formFactory;

    @Inject
    public JobApplicationController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result uploadJobApplication(Http.Request request) {
        return ok(views.html.uploadJobApplication.render());
    }

    public Result getUploadedJobSpecifications(Http.Request request) {
        return ok(views.html.uploadedJobSpecifications.render());
    }

    public Result submitJobDescription() {
        JobDescription jobDescription = formFactory.form(JobDescription.class).bindFromRequest().get();
        Item jobDescriptionItem = new Item()
                .withPrimaryKey("referenceCode", jobDescription.getReferenceCode())
                .with("recruiter", "test")
                .with("jobTitle", jobDescription.getJobTitle())
                .with("duration", jobDescription.getDuration())
                .with("location", jobDescription.getLocation())
                .with("department", jobDescription.getDepartment())
                .with("section", jobDescription.getSection())
                .with("grade", jobDescription.getGrade())
                .with("reportsTo", jobDescription.getReportsTo())
                .with("responsibleTo", jobDescription.getResponsibleTo())
                .with("hours", jobDescription.getHours())
                .with("salary", jobDescription.getSalary())
                .with("mainPurposeOfJob", jobDescription.getMainPurposeOfJob())
                .with("mainResponsibilities", jobDescription.getMainResponsibilities())
                .with("general", jobDescription.getGeneral());
        DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_JOB_DESCRIPTIONS.getName()).putItem(jobDescriptionItem);
        return redirect(routes.HomeController.index());
    }
}
