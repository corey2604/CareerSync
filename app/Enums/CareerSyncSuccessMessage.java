package Enums;

public enum CareerSyncSuccessMessage {
    DETAILS_UPDATED("Your details have been successfully updated");

    private String successMsg;

    CareerSyncSuccessMessage(String successMsg) {
        this.successMsg=successMsg;
    }

    public String getSuccessMessage() {
        return this.successMsg;
    }
}
