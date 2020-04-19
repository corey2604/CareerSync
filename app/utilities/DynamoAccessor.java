package utilities;

import enums.DynamoTables;
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

import java.text.SimpleDateFormat;
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

    public List<JobDescription> getSavedJobSpecifications(String username) {
        AmazonDynamoDB client = AmazonDynamoDbClientWrapper.getInstance();

        Map<String, AttributeValue> expressionAttributeValues =
                new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":username", new AttributeValue().withS(username));

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(DynamoTables.CAREER_SYNC_SAVED_JOB_DESCRIPTIONS.getName())
                .withFilterExpression("username = :username")
                .withExpressionAttributeValues(expressionAttributeValues);

        ScanResult result = client.scan(scanRequest);

        List<JobDescription> savedJobDescriptionsForUser = new ArrayList<>();
        for (Map<String, AttributeValue> item : result.getItems()) {
            savedJobDescriptionsForUser.add(getJobDescription(item.get("recruiter").getS(), item.get("referenceCode").getS()));
        }

        return savedJobDescriptionsForUser;
    }

    public void putKsasInTable(String username, KsaForm ksaForm) {
        Item jobDescriptionItem = new Item()
                .withPrimaryKey("username", username)
                .with("qualificationLevel", ksaForm.getQualificationLevel())
                .with("qualificationArea", ksaForm.getQualificationArea().isPresent() ? ksaForm.getQualificationArea().get() : "N/A");
        addSkillListIfPresent(jobDescriptionItem, "communicationSkills", ksaForm.getCommunicationSkills());
        addSkillListIfPresent(jobDescriptionItem, "peopleSkills", ksaForm.getPeopleSkills());
        addSkillListIfPresent(jobDescriptionItem, "financialKnowledgeAndSkills", ksaForm.getFinancialKnowledgeAndSkills());
        addSkillListIfPresent(jobDescriptionItem, "thinkingAndAnalysis", ksaForm.getThinkingAndAnalysis());
        addSkillListIfPresent(jobDescriptionItem, "creativeOrInnovative", ksaForm.getCreativeOrInnovative());
        addSkillListIfPresent(jobDescriptionItem, "administrativeOrOrganisational", ksaForm.getAdministrativeOrOrganisational());
        DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_USER_KSAS.getName()).putItem(jobDescriptionItem);
    }

    public void saveJobDescriptionForUser(String username, String recruiter, String referenceCode) {
        ScanResult result = scanForSavedJobDescription(username, recruiter, referenceCode);

        if (result.getItems().size() == 0) {
            Item jobDescriptionItem = new Item()
                    .withPrimaryKey("id", UUID.randomUUID().toString())
                    .with("username", username)
                    .with("recruiter", recruiter)
                    .with("referenceCode", referenceCode);
            DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_SAVED_JOB_DESCRIPTIONS.getName()).putItem(jobDescriptionItem);
        }
    }

    public void removeSavedJobDescription(String username, String recruiter, String referenceCode) {
        Table savedJobDescriptionsTable = DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_SAVED_JOB_DESCRIPTIONS.getName());

        ScanResult result = scanForSavedJobDescription(username, recruiter, referenceCode);

        for (Map<String, AttributeValue> item : result.getItems()) {
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey("id", item.get("id").getS())
                    .withReturnValues(ReturnValue.ALL_OLD);
            savedJobDescriptionsTable.deleteItem(deleteItemSpec);
        }
    }

    public boolean hasUserSavedJobDescription(String username, String recruiter, String referenceCode) {
        ScanResult result = scanForSavedJobDescription(username, recruiter, referenceCode);
        return result.getItems().size() > 0;
    }

    private ScanResult scanForSavedJobDescription(String username, String recruiter, String referenceCode) {
        AmazonDynamoDB client = AmazonDynamoDbClientWrapper.getInstance();

        Map<String, AttributeValue> expressionAttributeValues =
                new HashMap<String, AttributeValue>();
        expressionAttributeValues.put(":username", new AttributeValue().withS(username));
        expressionAttributeValues.put(":recruiter", new AttributeValue().withS(recruiter));
        expressionAttributeValues.put(":referenceCode", new AttributeValue().withS(referenceCode));

        ScanRequest scanRequest = new ScanRequest()
                .withTableName(DynamoTables.CAREER_SYNC_SAVED_JOB_DESCRIPTIONS.getName())
                .withFilterExpression("username = :username AND recruiter = :recruiter AND referenceCode = :referenceCode")
                .withExpressionAttributeValues(expressionAttributeValues);

        return client.scan(scanRequest);
    }

    public void putJobDescriptionInTable(JobDescription jobDescription, boolean isUpdate) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);

        Optional<List<String>> communicaticationSkills = Optional.ofNullable(jobDescription.getCommunicationSkills());
        Optional<List<String>> peopleSkills = Optional.ofNullable(jobDescription.getPeopleSkills());
        Optional<List<String>> financialKnowledgeAndSkills = Optional.ofNullable(jobDescription.getFinancialKnowledgeAndSkills());
        Optional<List<String>> thinkingAndAnalysis = Optional.ofNullable(jobDescription.getThinkingAndAnalysis());
        Optional<List<String>> creativeOrInnovative = Optional.ofNullable(jobDescription.getCreativeOrInnovative());
        Optional<List<String>> administrativeOrOrganisational = Optional.ofNullable(jobDescription.getAdministrativeOrOrganisational());

        Item jobDescriptionItem = new Item()
                .withPrimaryKey("referenceCode", jobDescription.getReferenceCode())
                .with("recruiter", jobDescription.getRecruiter())
                .with("jobTitle", jobDescription.getJobTitle())
                .with("location", jobDescription.getLocation())
                .with("companyOrOrganisation", jobDescription.getCompanyOrOrganisation())
                .with("hours", jobDescription.getHours())
                .with("salary", jobDescription.getSalary())
                .with("mainPurposeOfJob", jobDescription.getMainPurposeOfJob())
                .with("mainResponsibilities", jobDescription.getMainResponsibilities())
                .with("closingDate", jobDescription.getClosingDate())
                .with("qualificationLevel", jobDescription.getQualificationLevel())
                .with("qualificationArea", jobDescription.getQualificationArea().isPresent() ? jobDescription.getQualificationArea().get() : "N/A")
                .withList("communicationSkills", convertSkillsToList(communicaticationSkills))
                .withList("peopleSkills", convertSkillsToList(peopleSkills))
                .withList("financialKnowledgeAndSkills", convertSkillsToList(financialKnowledgeAndSkills))
                .withList("thinkingAndAnalysis", convertSkillsToList(thinkingAndAnalysis))
                .withList("creativeOrInnovative", convertSkillsToList(creativeOrInnovative))
                .withList("administrativeOrOrganisational", convertSkillsToList(administrativeOrOrganisational))
                .with("createdAt", (isUpdate) ? jobDescription.getCreatedAt() : strDate)
                .with("lastUpdatedAt", strDate)
                .with("percentageMatchThreshold", String.valueOf(jobDescription.getPercentageMatchThreshold()));

        if (jobDescription.getDuration().isPresent()) {
            jobDescriptionItem.with("duration", jobDescription.getDuration().get());
        }

        if (jobDescription.getDepartment().isPresent()) {
            jobDescriptionItem.with("department", jobDescription.getDepartment().get());
        }

        if (jobDescription.getSection().isPresent()) {
            jobDescriptionItem.with("section", jobDescription.getSection().get());
        }

        if (jobDescription.getGrade().isPresent()) {
            jobDescriptionItem.with("grade", jobDescription.getGrade().get());
        }

        if (jobDescription.getReportsTo().isPresent()) {
            jobDescriptionItem.with("reportsTo", jobDescription.getReportsTo().get());
        }

        if (jobDescription.getResponsibleTo().isPresent()) {
            jobDescriptionItem.with("responsibleTo", jobDescription.getResponsibleTo().get());
        }

        if (jobDescription.getGeneral().isPresent()) {
            jobDescriptionItem.with("general", jobDescription.getGeneral().get());
        }

        DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_JOB_DESCRIPTIONS.getName()).putItem(jobDescriptionItem);
    }

    private List<String> convertSkillsToList(Optional<List<String>> skills) {
        return skills.map(strings -> strings.stream().filter(Objects::nonNull).collect(Collectors.toList())).orElse(Collections.EMPTY_LIST);
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
