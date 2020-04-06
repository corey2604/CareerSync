package awsWrappers;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;

import javax.inject.Singleton;

@Singleton
public class AwsCognitoIdentityProviderWrapper {
    private static AWSCognitoIdentityProvider awsCognitoIdentityProvider = null;

    private AwsCognitoIdentityProviderWrapper() {
        //Private constructor
    }

    //Factory method
    public static AWSCognitoIdentityProvider getInstance() {
        if (awsCognitoIdentityProvider == null) {
            awsCognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.standard()
                    //.withCredentials(ClasspathPropertiesFileCredentialsProviderWrapper.getInstance())
                    .withRegion(Regions.EU_WEST_1)
                    .build();
        }
        return awsCognitoIdentityProvider;
    }

    public static void setInstance(AWSCognitoIdentityProvider awsCognitoIdentityProviderInstance) {
        awsCognitoIdentityProvider = awsCognitoIdentityProviderInstance;
    }
}
