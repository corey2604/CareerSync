package enums;

public enum CareerSyncSuccessMessage {
    DETAILS_UPDATED("Your details have been successfully updated"),
    JOB_SPECIFICATION_SAVED("Your job recommendation has been saved successfully");

    private String successMsg;

    CareerSyncSuccessMessage(String successMsg) {
        this.successMsg=successMsg;
    }

    public String getSuccessMessage() {
        return this.successMsg;
    }
}
