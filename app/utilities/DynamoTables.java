package utilities;

public enum DynamoTables {
    CAREER_SYNC_USERS("CareerSync-Users"),
    CAREER_SYNC_JOB_DESCRIPTIONS("CareerSync-JobDescriptions");

    private String name;

    DynamoTables(String name) {
        this.name=name;
    }

    public String getName() {
        return this.name;
    }
}
