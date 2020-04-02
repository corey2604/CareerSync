package awsWrappers;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.google.common.annotations.VisibleForTesting;

public class DynamoDbTableProvider {
    private static DynamoDB dynamoDB = null;


    private DynamoDbTableProvider() {
        //Private constructor
    }

    //Factory method
    public static Table getTable(String tableName) {
        System.out.println("Table Name within method: " + tableName);
        if (dynamoDB == null) {
            System.out.println("Dynamo DB was null");
            dynamoDB = new DynamoDB(AmazonDynamoDbClientWrapper.getInstance());
            System.out.println("DynamoDBTableProvider set.");
        }
        return dynamoDB.getTable(tableName);
    }

    @VisibleForTesting
    public static void setDynamoDb(DynamoDB setDynamoDb) {
        dynamoDB = setDynamoDb;
    }
}
