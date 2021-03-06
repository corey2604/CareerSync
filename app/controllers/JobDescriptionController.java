package controllers;

import enums.CareerSyncSuccessMessage;
import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
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
import enums.DynamoTables;
import utilities.KsaMatcher;

import javax.inject.Inject;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class JobDescriptionController extends Controller {

    private FormFactory formFactory;
    private DynamoAccessor dynamoAccessor = DynamoAccessor.getInstance();

    @Inject
    public JobDescriptionController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result uploadJobDescription(Http.Request request) {
        return ok(views.html.recruiter.uploadJobDescription.render(views.html.ksaFormContent.render()));
    }

    public Result viewJobDescription(Http.Request request, String recruiter, String referenceCode) {
        JobDescription jobDescription = dynamoAccessor.getJobDescription(recruiter, referenceCode);
        return ok(views.html.recruiter.viewJobDescription.render(views.html.viewJobSpecificationBody.render(jobDescription),
                views.html.viewEmployeeSpecificationBody.render(jobDescription),
                request.cookie("userType").value(),
                DynamoAccessor.getInstance().doesUserHaveKsas(request.cookie("username").value())));
    }

    public Result getUploadedJobDescriptions(Http.Request request) {
        Table jobDescriptionsTable = DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_JOB_DESCRIPTIONS.getName());

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("recruiter = :recruiter")
                .withValueMap(new ValueMap()
                        .withString(":recruiter", request.cookie("username").value()));

        ItemCollection<QueryOutcome> items = jobDescriptionsTable.query(spec);

        Iterator<Item> iterator = items.iterator();
        List<JobDescription> jobDescriptions = new ArrayList<JobDescription>();
        while (iterator.hasNext()) {
            JobDescription jobDescription = new JobDescription(iterator.next());
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date closingDate;
            try {
                closingDate = formatter.parse(jobDescription.getClosingDate());
            } catch (ParseException e) {
                return badRequest();
            }
            if (new Date().after(closingDate)) {
                deleteJobDescription(request, jobDescription.getReferenceCode());
            } else {
                jobDescriptions.add(jobDescription);
            }
        }
        return ok(views.html.recruiter.uploadedJobDescriptions.render(jobDescriptions));
    }

    public Result submitJobDescription(Http.Request request) {
        JobDescription jobDescription = formFactory.form(JobDescription.class).bindFromRequest().get();
        jobDescription.setRecruiter(request.cookie("username").value());
        putNewJobDescriptionInTable(jobDescription);
        return redirect(routes.HomeController.index());
    }

    public Result submitEditedJobDescription(Http.Request request) {
        JobDescription jobDescription = formFactory.form(JobDescription.class).bindFromRequest().get();
        jobDescription.setRecruiter(request.cookie("username").value());
        updateJobDescriptionInTable(jobDescription);
        return redirect(routes.HomeController.index());
    }

    public Result editJobDescription(String recruiter, String referenceCode) {
        JobDescription jobDescription = dynamoAccessor.getJobDescription(recruiter, referenceCode);
        return ok(views.html.recruiter.editJobApplication.render(jobDescription, views.html.populatedKsaFormContent.render(jobDescription.getUserKsasFromJobDescription())));
    }

    public Result deleteJobDescription(Http.Request request, String referenceCode) {
        Table jobDescriptionsTable = DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_JOB_DESCRIPTIONS.getName());
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey("recruiter", request.cookie("username").value(), "referenceCode", referenceCode)
                .withReturnValues(ReturnValue.ALL_OLD);
        jobDescriptionsTable.deleteItem(deleteItemSpec);
        return getUploadedJobDescriptions(request);
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

    public Result viewUserDetails(Http.Request request, String usernameToFindDetails) {
        UserAccountDetails userAccountDetails = DynamoAccessor.getInstance().getUserAccountDetails(usernameToFindDetails);
        Boolean userHasKsas = DynamoAccessor.getInstance().doesUserHaveKsas(request.cookie("username").value());
        return ok(views.html.candidate.userContactInformation.render(userAccountDetails, request.cookie("userType").value(), userHasKsas));
    }

    public Result saveJobDescription(Http.Request request, String recruiter, String referenceCode) {
        String username = request.cookie("username").value();
        DynamoAccessor.getInstance().saveJobDescriptionForUser(username, recruiter, referenceCode);
        List<JobDescription> matchingJobDescriptions = KsaMatcher.getInstance().getJobRecommendations(username);
        return ok(views.html.candidate.jobReccomendations.render(username, matchingJobDescriptions, Optional.of(CareerSyncSuccessMessage.JOB_SPECIFICATION_SAVED)));
    }

    public Result getSavedJobDescriptionsForUser(Http.Request request) {
        List<JobDescription> savedJobDescriptions = DynamoAccessor.getInstance().getSavedJobSpecifications(request.cookie("username").value());
        return ok(views.html.candidate.savedJobRecommendations.render(savedJobDescriptions));
    }

    public Result removeSavedJobDescription(Http.Request request, String recruiter, String referenceCode) {
        String username = request.cookie("username").value();
        DynamoAccessor.getInstance().removeSavedJobDescription(username, recruiter, referenceCode);
        List<JobDescription> savedJobDescriptions = DynamoAccessor.getInstance().getSavedJobSpecifications(username);
        return ok(views.html.candidate.savedJobRecommendations.render(savedJobDescriptions));
    }

    private void putNewJobDescriptionInTable(JobDescription jobDescription) {
        DynamoAccessor.getInstance().putJobDescriptionInTable(jobDescription, false);
    }

    private void updateJobDescriptionInTable(JobDescription jobDescription) {
        DynamoAccessor.getInstance().putJobDescriptionInTable(jobDescription, true);
    }
}
