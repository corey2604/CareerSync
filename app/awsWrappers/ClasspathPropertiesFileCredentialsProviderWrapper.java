package awsWrappers;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;

import javax.inject.Singleton;

@Singleton
public class ClasspathPropertiesFileCredentialsProviderWrapper {
    private static ClasspathPropertiesFileCredentialsProvider classpathPropertiesFileCredentialsProvider = null;

    private ClasspathPropertiesFileCredentialsProviderWrapper() {
        //Private constructor
    }

    //Factory method
    public static ClasspathPropertiesFileCredentialsProvider getInstance() {
        if (classpathPropertiesFileCredentialsProvider == null) {
            classpathPropertiesFileCredentialsProvider =
                    new ClasspathPropertiesFileCredentialsProvider("/application.conf");
        }
        return classpathPropertiesFileCredentialsProvider;
    }
}
