package utilities;

import Enums.DynamoTables;
import awsWrappers.AmazonDynamoDbClientWrapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import models.JobDescription;
import models.UserAccountDetails;
import models.UserKsas;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class KsaMatcher {
    private static KsaMatcher ksaMatcher = null;

    private KsaMatcher() {
        //private constructor
    }

    public static KsaMatcher getInstance() {
        if (ksaMatcher == null) {
            ksaMatcher = new KsaMatcher();
        }
        return ksaMatcher;
    }

    public static void setInstance(KsaMatcher ksaMatcherInstance) {
        ksaMatcher = ksaMatcherInstance;
    }

    public List<JobDescription> getJobRecommendations(String username) {
        UserKsas userKsas = DynamoAccessor.getInstance().getKsasForUser(username);
        ScanResult allJobDescriptions = getAllJobDescriptions();
        return getMatchingJobDescriptions(userKsas, allJobDescriptions);
    }

    public List<UserAccountDetails> getPotentialCandidates(String recruiter, String referenceCode) {
        JobDescription jobDescription = DynamoAccessor.getInstance().getJobDescription(recruiter, referenceCode);
        ScanResult allCandidates = getAllCandidates();
        return getMatchingCandidates(jobDescription, allCandidates);
    }

    private ScanResult getAllJobDescriptions() {
        AmazonDynamoDB client = AmazonDynamoDbClientWrapper.getInstance();

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(DynamoTables.CAREER_SYNC_JOB_DESCRIPTIONS.getName());

        return client.scan(scanRequest);
    }

    private ScanResult getAllCandidates() {
        AmazonDynamoDB client = AmazonDynamoDbClientWrapper.getInstance();

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(DynamoTables.CAREER_SYNC_USER_KSAS.getName());

        return client.scan(scanRequest);
    }

    private List<JobDescription> getMatchingJobDescriptions(UserKsas userKsas, ScanResult allJobDescriptions) {
        List<String> allKsas = userKsas.getAllKsas();
        List<JobDescription> matchingJobDescriptions = new ArrayList<>();
        for (Map<String, AttributeValue> item : allJobDescriptions.getItems()) {
            JobDescription jobDescription = new JobDescription(item);
            List<String> allJobDescriptionRelatedKsas = jobDescription.getAllJobRelatedKsas();
            long ksaCount = allKsas.stream().filter(ksa -> allJobDescriptionRelatedKsas.contains(ksa)).count();
            double percentMatch = (ksaCount <= allJobDescriptionRelatedKsas.size()) ? (ksaCount * 100) / allJobDescriptionRelatedKsas.size() : 100;

            if (percentMatch > jobDescription.getPercentageMatchThreshold()) {
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date closingDate = formatter.parse(jobDescription.getClosingDate());
                    if (new Date().after(closingDate)) {
                        DynamoAccessor.getInstance().deleteJobDescriptionFromTable(jobDescription);
                    } else {
                        matchingJobDescriptions.add(jobDescription);
                    }
                } catch (ParseException e) {
                }
            }
        }
        return matchingJobDescriptions;
    }

    private List<UserAccountDetails> getMatchingCandidates(JobDescription jobDescription, ScanResult allCandidates) {
        List<String> allJobDescriptionRelatedKsas = jobDescription.getAllJobRelatedKsas();
        if (allJobDescriptionRelatedKsas.size() > 0) {
            List<UserAccountDetails> matchingUsers = new ArrayList<>();
            for (Map<String, AttributeValue> item : allCandidates.getItems()) {
                UserKsas userKsas = new UserKsas(item);
                List<String> allUserKsas = userKsas.getAllKsas();
                long ksaCount = allUserKsas.stream().filter(ksa -> allJobDescriptionRelatedKsas.contains(ksa)).count();
                double percentMatch = (ksaCount <= allJobDescriptionRelatedKsas.size()) ? (ksaCount * 100) / allJobDescriptionRelatedKsas.size() : 100;
                System.out.println("Percentage Match: " + percentMatch);

                if (percentMatch >= jobDescription.getPercentageMatchThreshold()) {
                    matchingUsers.add(DynamoAccessor.getInstance().getUserAccountDetails(userKsas.getUsername()));
                }
            }
            return matchingUsers;
        }
        return Collections.EMPTY_LIST;
    }
}
