package controllers;

import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import models.JobDescription;
import models.UserAccountDetails;
import models.UserKsas;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utilities.DynamoAccessor;
import utilities.DynamoTables;
import utilities.KsaMatcher;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class JobApplicationController extends Controller {

    private FormFactory formFactory;
    private DynamoAccessor dynamoAccessor = DynamoAccessor.getInstance();

    @Inject
    public JobApplicationController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result uploadJobApplication(Http.Request request) {
        return ok(views.html.recruiter.uploadJobSpecification.render(views.html.ksaFormContent.render()));
    }

    public Result viewJobDescription(String recruiter, String referenceCode) {
        JobDescription jobDescription = dynamoAccessor.getJobDescription(recruiter, referenceCode);
        return ok(views.html.recruiter.viewJobSpecification.render(views.html.viewJobSpecificationBody.render(jobDescription),
                views.html.viewEmployeeSpecificationBody.render(jobDescription)));
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
        return ok(views.html.recruiter.uploadedJobSpecifications.render(jobDescriptions));
    }

    public Result submitJobDescription(Http.Request request) {
        JobDescription jobDescription = formFactory.form(JobDescription.class).bindFromRequest().get();
        jobDescription.setRecruiter(request.cookie("username").value());
        putJobDescriptionInTable(jobDescription);
        return redirect(routes.HomeController.index());
    }

    public Result editJobDescription(String recruiter, String referenceCode) {
        JobDescription jobDescription = dynamoAccessor.getJobDescription(recruiter, referenceCode);
        return ok(views.html.recruiter.editJobApplication.render(jobDescription, views.html.populatedKsaFormContent.render(jobDescription)));
    }

    public Result deleteJobDescription(Http.Request request, String referenceCode) {
        Table jobDescriptionsTable = DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_JOB_DESCRIPTIONS.getName());
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey("recruiter", request.cookie("username").value())
                .withConditionExpression(":referenceCode = :val")
                .withNameMap(new NameMap()
                        .with(":referenceCode", "referenceCode"))
                .withValueMap(new ValueMap()
                        .withString(":val", referenceCode))
                .withReturnValues(ReturnValue.ALL_OLD);
        jobDescriptionsTable.deleteItem(deleteItemSpec);
        return getUploadedJobSpecifications(request);
    }

    public Result getPotentialCandidates(String recruiter, String referenceCode) {
        JobDescription jobDescription = DynamoAccessor.getInstance().getJobDescription(recruiter, referenceCode);
        List<UserAccountDetails> matchingCandidates = KsaMatcher.getInstance().getPotentialCandidates(recruiter, referenceCode);
        return ok(views.html.recruiter.matchingCandidates.render(referenceCode, jobDescription.getJobTitle(), matchingCandidates));
    }

    public Result getCandidateKsaProfile(String firstName, String surname, String username) {
        String fullName = firstName + " " + surname;
        UserKsas userKsas = DynamoAccessor.getInstance().getKsasForUser(username);
        return ok(views.html.candidateKsaProfile.render(fullName, userKsas));
    }

    public Result viewUserDetails(String userName) {
        UserAccountDetails userAccountDetails = DynamoAccessor.getInstance().getUserAccountDetails(userName);
        return ok(views.html.candidate.userContactInformation.render(userAccountDetails));
    }

    private void putJobDescriptionInTable(JobDescription jobDescription) {
        Item jobDescriptionItem = new Item()
                .withPrimaryKey("referenceCode", jobDescription.getReferenceCode())
                .with("recruiter", jobDescription.getRecruiter())
                .with("jobTitle", jobDescription.getJobTitle())
                .with("duration", jobDescription.getDuration())
                .with("location", jobDescription.getLocation())
                .with("companyOrOrganisation", jobDescription.getCompanyOrOrganisation())
                .with("department", jobDescription.getDepartment())
                .with("section", jobDescription.getSection())
                .with("grade", jobDescription.getGrade())
                .with("reportsTo", jobDescription.getReportsTo())
                .with("responsibleTo", jobDescription.getResponsibleTo())
                .with("hours", jobDescription.getHours())
                .with("salary", jobDescription.getSalary())
                .with("mainPurposeOfJob", jobDescription.getMainPurposeOfJob())
                .with("mainResponsibilities", jobDescription.getMainResponsibilities())
                .with("general", jobDescription.getGeneral())
                .with("qualificationLevel", jobDescription.getQualificationLevel())
                .with("qualificationArea", jobDescription.getQualificationArea())
                .withList("communicationSkills", jobDescription.getCommunicationSkills().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("peopleSkills", jobDescription.getPeopleSkills().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("financialKnowledgeAndSkills", jobDescription.getFinancialKnowledgeAndSkills().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("thinkingAndAnalysis", jobDescription.getThinkingAndAnalysis().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("creativeOrInnovative", jobDescription.getCreativeOrInnovative().stream().filter(item -> item != null).collect(Collectors.toList()))
                .withList("administrativeOrOrganisational", jobDescription.getAdministrativeOrOrganisational().stream().filter(item -> item != null).collect(Collectors.toList()));
        DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_JOB_DESCRIPTIONS.getName()).putItem(jobDescriptionItem);
    }
}
