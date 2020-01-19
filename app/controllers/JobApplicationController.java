package controllers;

import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import models.JobDescription;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utilities.DynamoTables;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JobApplicationController extends Controller {

    private FormFactory formFactory;
    private String userName;

    @Inject
    public JobApplicationController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result uploadJobApplication(Http.Request request) {
        userName = request.cookie("username").value();
        return ok(views.html.uploadJobApplication.render());
    }

    public Result getUploadedJobSpecifications(Http.Request request) {
        Table jobDescriptionsTable = DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_JOB_DESCRIPTIONS.getName());

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("recruiter = :recruiter")
                .withValueMap(new ValueMap()
                        .withString(":recruiter", request.cookie("username").value()));

        ItemCollection<QueryOutcome> items = jobDescriptionsTable.query(spec);

        Iterator<Item> iterator = items.iterator();
        List<JobDescription> jobDescriptions = new ArrayList<JobDescription>();
        Item item;
        while (iterator.hasNext()) {
            item = iterator.next();
            jobDescriptions.add(new JobDescription(item));
            System.out.println(item.toJSONPretty());
        }
        return ok(views.html.uploadedJobSpecifications.render(jobDescriptions));
    }

    public Result submitJobDescription() {
        JobDescription jobDescription = formFactory.form(JobDescription.class).bindFromRequest().get();
        Item jobDescriptionItem = new Item()
                .withPrimaryKey("referenceCode", jobDescription.getReferenceCode())
                .with("recruiter", userName)
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
