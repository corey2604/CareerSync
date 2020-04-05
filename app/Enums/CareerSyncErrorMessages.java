package Enums;

public enum CareerSyncErrorMessages {
    USERNAME_TAKEN("Username is already taken. Please select another."),
    PASSWORD_DOES_NOT_CONFORM("Please enter a password that is at least 8 characters long. It must contain at least one of each of the following: an uppercase character, a lowercase character, a number and a special character."),
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
