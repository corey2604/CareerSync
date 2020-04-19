package enums;

public enum DynamoTables {
    CAREER_SYNC_USERS("CareerSync-Users"),
    CAREER_SYNC_JOB_DESCRIPTIONS("CareerSync-JobDescriptions"),
    CAREER_SYNC_USER_KSAS("CareerSync-UserKsas"),
    CAREER_SYNC_SAVED_JOB_DESCRIPTIONS("CareerSync-SavedJobDescriptions");

    private String name;

    DynamoTables(String name) {
        this.name=name;
    }

    public String getName() {
        return this.name;
    }
}
