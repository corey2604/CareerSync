package utilities;

import awsWrappers.AmazonDynamoDbClientWrapper;
import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import models.JobDescription;
import models.UserAccountDetails;
import models.UserKsas;

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

    public List<JobDescription> getJobRecommendations(String username) {
        UserKsas userKsas = getKsasForUser(username);
        ScanResult allJobDescriptions = getAllJobDescriptions();
        return getMatchingJobDescriptions(userKsas, allJobDescriptions);
    }

    public void getPotentialCandidates(String recruiter, String referenceCode) {
        JobDescription jobDescription = DynamoAccessor.getInstance().getJobDescription(recruiter, referenceCode);
        ScanResult allCandidates = getAllCandidates();
        getMatchingCandidates(jobDescription, allCandidates);
    }

    public UserKsas getKsasForUser(String username) {
        Table userKsaTable = DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_USER_KSAS.getName());
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("username = :username")
                .withValueMap(new ValueMap()
                        .withString(":username", username)
                );

        ItemCollection<QueryOutcome> items = userKsaTable.query(spec);

        Iterator<Item> iterator = items.iterator();
        Item item;
        List<UserKsas> userKsas = new ArrayList<>();
        while (iterator.hasNext()) {
            item = iterator.next();
            userKsas.add(new UserKsas(item));
        }
        return userKsas.get(0);
    }

    public ScanResult getAllJobDescriptions() {
        AmazonDynamoDB client = AmazonDynamoDbClientWrapper.getInstance();

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(DynamoTables.CAREER_SYNC_JOB_DESCRIPTIONS.getName());

        return client.scan(scanRequest);
    }

    public ScanResult getAllCandidates() {
        AmazonDynamoDB client = AmazonDynamoDbClientWrapper.getInstance();

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(DynamoTables.CAREER_SYNC_USER_KSAS.getName());

        return client.scan(scanRequest);
    }


    public void readAllJobDescriptionsFromRecruiter(String recruiterName) {
        AmazonDynamoDB client = AmazonDynamoDbClientWrapper.getInstance();

        Map<String, AttributeValue> expressionAttributeValues = Collections.singletonMap(":recruiterName", new AttributeValue(recruiterName));
        ScanRequest scanRequest = new ScanRequest()
                .withTableName(DynamoTables.CAREER_SYNC_JOB_DESCRIPTIONS.getName())
                .withFilterExpression("recruiter = :recruiterName")
                .withExpressionAttributeValues(expressionAttributeValues);

        ScanResult result = client.scan(scanRequest);
        for (Map<String, AttributeValue> item : result.getItems()) {
            System.out.println(item);
        }
    }

    public List<JobDescription> getMatchingJobDescriptions(UserKsas userKsas, ScanResult allJobDescriptions) {
        List<String> allKsas = userKsas.getAllKsas();
        List<JobDescription> matchingJobDescriptions = new ArrayList<>();
        for (Map<String, AttributeValue> item : allJobDescriptions.getItems()) {
            JobDescription jobDescription = new JobDescription(item);
            List<String> allJobDescriptionRelatedKsas = jobDescription.getAllJobRelatedKsas();
            long ksaCount = allKsas.stream().filter(ksa -> allJobDescriptionRelatedKsas.contains(ksa)).count();
            double percentMatch = (ksaCount <= allJobDescriptionRelatedKsas.size()) ? (ksaCount * 100) / allJobDescriptionRelatedKsas.size() : 100;
            System.out.println("Percentage Match: " + percentMatch);

            if (percentMatch > 75) {
                matchingJobDescriptions.add(jobDescription);
            }
        }
        return matchingJobDescriptions;
    }

    public List<UserAccountDetails> getMatchingCandidates(JobDescription jobDescription, ScanResult allCandidates) {
        List<String> allJobDescriptionRelatedKsas = jobDescription.getAllJobRelatedKsas();
        List<UserAccountDetails> matchingUsers = new ArrayList<>();
        for (Map<String, AttributeValue> item : allCandidates.getItems()) {
            UserKsas userKsas = new UserKsas(item);
            List<String> allUserKsas = userKsas.getAllKsas();
            long ksaCount = allUserKsas.stream().filter(ksa -> allJobDescriptionRelatedKsas.contains(ksa)).count();
            double percentMatch = (ksaCount <= allJobDescriptionRelatedKsas.size()) ? (ksaCount * 100) / allJobDescriptionRelatedKsas.size() : 100;;
            System.out.println("Percentage Match: " + percentMatch);

            if (percentMatch > 75) {
                matchingUsers.add(DynamoAccessor.getInstance().getUserAccountDetails(userKsas.getUsername()));
            }
        }
        return matchingUsers;
    }

}
