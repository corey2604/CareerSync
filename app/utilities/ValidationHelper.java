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
        }
        if (password.equals(password.toLowerCase())) {
            return false;
        }
        if (!password.matches("(.)*(\\d)(.)*")) {
            return false;
        }
        //Checks at least one char is not alpha numeric)
        if (password.matches("[A-Za-z0-9 ]*")) {
            return false;
        }
        return true;
    }

}
