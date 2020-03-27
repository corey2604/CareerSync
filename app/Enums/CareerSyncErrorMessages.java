package Enums;

public enum CareerSyncErrorMessages {
    USERNAME_TAKEN("Username is already taken. Please select another."),
    PASSWORD_DOES_NOT_CONFORM("Please enter a password that consists of at least 8 characters. It must contain one uppercase character, one number and one symbol character."),
    INVALID_PHONE_NUMBER("Please enter a valid phone number. Only numerical digits will be accepted."),
    GENERAL_SUBMISSION_ERROR("There was an error processing your details. Please review the data you have entered.");

    private String errorMsg;

    CareerSyncErrorMessages(String errorMsg) {
        this.errorMsg=errorMsg;
    }

    public String getErrorMessage() {
        return this.errorMsg;
    }
}
