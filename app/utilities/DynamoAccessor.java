package utilities;

import Enums.DynamoTables;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        Table jobDescriptionsTable = DynamoDbTableProvider.getTable(DynamoTables.CAREER_SYNC_JOB_DESCRIPTIONS.getName());
        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("recruiter = :recruiter")
                .withFilterExpression("referenceCode = :referenceCode")
                .withValueMap(new ValueMap()
                        .withString(":recruiter", recruiter)
                        .withString(":referenceCode", referenceCode)
                );

        ItemCollection<QueryOutcome> items = jobDescriptionsTable.query(spec);

        Iterator<Item> iterator = items.iterator();
        List<JobDescription> jobDescriptions = new ArrayList<JobDescription>();
        Item item;
        while (iterator.hasNext()) {
            item = iterator.next();
            jobDescriptions.add(new JobDescription(item));
            System.out.println(item.toJSONPretty());
        }
        return jobDescriptions.get(0);
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
}
