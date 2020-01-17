package awsWrappers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

public class AmazonDynamoDbClientWrapper {
    private static AmazonDynamoDB client = null;

    private AmazonDynamoDbClientWrapper() {
        //Private constructor
    }

    //Factory method
    public static AmazonDynamoDB getInstance() {
        if (client == null) {
            client = AmazonDynamoDBClientBuilder
                    .standard()
                    .withCredentials(ClasspathPropertiesFileCredentialsProviderWrapper.getInstance())
                    .withRegion(Regions.EU_WEST_1)
                    .build();;
        }
        return client;
    }
}
