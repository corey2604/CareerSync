package utilities;

public class ValidationHelper {
    private static ValidationHelper validationHelper = null;

    private ValidationHelper() {
        //Private Constructor
    }

    //Factory method
    public static ValidationHelper getInstance() {
        if (validationHelper == null) {
            validationHelper = new ValidationHelper();
        }
        return validationHelper;
    }

    public boolean passwordIsValid(String password) {
        if (password.length() < 8) {
            return false;
        }//Ensures that the password is not entirely in the same case
        if (password.equals(password.toLowerCase())) {
            return false;
        }
        //Ensures that at least one digit is present
        if (!password.matches("(.)*(\\d)(.)*")) {
            return false;
        }
        //Ensures that at least one character is not alphanumeric
        if (password.matches("[A-Za-z0-9 ]*")) {
            return false;
        }
        return true;
    }

}
