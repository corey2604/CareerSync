package utilities;

import Enums.DynamoTables;
import awsWrappers.AmazonDynamoDbClientWrapper;
import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import models.JobDescription;
import models.KsaForm;
import models.UserAccountDetails;
import models.UserKsas;

import java.util.*;
import java.util.stream.Collectors;

public class DynamoAccessor {
    private static DynamoAccessor dynamoAccessor = null;

    private DynamoAccessor() {
        //private constructor
    }

    public static DynamoAccessor getInstance() {
        if (dynamoAccessor == null) {
            dynamoAccessor = new DynamoAccessor();
        }
        return dynamoAccessor;
    }

    public static void setDynamoAccessor(DynamoAccessor setDynamoAccessor) {
        dynamoAccessor = setDynamoAccessor;
    }

    public JobDescription getJobDescription(String recruiter, String referenceCode) {
        GetItemRequest getItemRequest = new GetItemRequest().withTableName(DynamoTables.CAREER_SYNC_JOB_DESCRIPTIONS.getName())
                .addKeyEntry("recruiter", new AttributeValue().withS(recruiter))
                .addKeyEntry("referenceCode", new AttributeValue().withS(referenceCode));

        return new JobDescription(AmazonDynamoDbClientWrapper.getInstance().getItem(getItemRequest).getItem());
    }

    public UserAccountDetails getUserAccountDetails(String username) {
        Item userAccountDetails = DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_USERS.getName()).getItem("username", username);
        return new UserAccountDetails(userAccountDetails.get("username").toString(),
                userAccountDetails.get("firstName").toString(),
                userAccountDetails.get("surname").toString(),
                userAccountDetails.get("email").toString(),
                userAccountDetails.get("phoneNumber").toString());
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

    public boolean doesUserHaveKsas(String username) {
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
        return userKsas.size() > 0;
    }

    public List<String> getAllUsernames() {
        AmazonDynamoDB client = AmazonDynamoDbClientWrapper.getInstance();

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(DynamoTables.CAREER_SYNC_USERS.getName())
                .withProjectionExpression("username");

        ScanResult result = client.scan(scanRequest);
        List<String> usernames = new ArrayList<>();
        for (Map<String, AttributeValue> item : result.getItems()) {
            usernames.add(item.get("username").getS());
        }
        return usernames;
    }

    public void putKsasInTable(String username, KsaForm ksaForm) {
        Item jobDescriptionItem = new Item()
                .withPrimaryKey("username", username)
                .with("qualificationLevel", ksaForm.getQualificationLevel())
                .with("qualificationArea", ksaForm.getQualificationArea());
        addSkillListIfPresent(jobDescriptionItem, "communicationSkills", ksaForm.getCommunicationSkills());
        addSkillListIfPresent(jobDescriptionItem, "peopleSkills", ksaForm.getPeopleSkills());
        addSkillListIfPresent(jobDescriptionItem, "financialKnowledgeAndSkills", ksaForm.getFinancialKnowledgeAndSkills());
        addSkillListIfPresent(jobDescriptionItem, "thinkingAndAnalysis", ksaForm.getThinkingAndAnalysis());
        addSkillListIfPresent(jobDescriptionItem, "creativeOrInnovative", ksaForm.getCreativeOrInnovative());
        addSkillListIfPresent(jobDescriptionItem, "administrativeOrOrganisational", ksaForm.getAdministrativeOrOrganisational());
        DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_USER_KSAS.getName()).putItem(jobDescriptionItem);
    }

    public void deleteJobDescriptionFromTable(JobDescription jobDescription) {
        Table jobDescriptionsTable = DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_JOB_DESCRIPTIONS.getName());
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey("recruiter", jobDescription.getRecruiter(), "referenceCode", jobDescription.getReferenceCode())
                .withReturnValues(ReturnValue.ALL_OLD);
        jobDescriptionsTable.deleteItem(deleteItemSpec);
    }

    private void addSkillListIfPresent(Item jobDescriptionItem, String fieldName, List<String> skillList) {
        Optional<List<String>> optSkillList = Optional.ofNullable(skillList);
        if (optSkillList.isPresent()) {
            jobDescriptionItem.withList(fieldName, optSkillList.get().stream().filter(item -> item != null).collect(Collectors.toList()));
        } else {
            jobDescriptionItem.withList(fieldName, Collections.EMPTY_LIST);
        }
    }
}
