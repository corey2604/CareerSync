package utilities;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ValidationHelperTest {

    @Test
    public void testWithValidPassword() {
        //when
        String validPassword = "Test12345@";

        //then
        assertTrue(ValidationHelper.getInstance().passwordIsValid(validPassword));
    }

    @Test
    public void testPasswordWithNotEnoughCharacters() {
        //when
        String invalidPassword = "T12@";

        //then
        assertFalse(ValidationHelper.getInstance().passwordIsValid(invalidPassword));
    }

    @Test
    public void testPasswordWithNoNumbers() {
        //when
        String invalidPassword = "TestPassword@";

        //then
        assertFalse(ValidationHelper.getInstance().passwordIsValid(invalidPassword));
    }

    @Test
    public void testPasswordWithOnlyAlphanumericCharacters() {
        //when
        String invalidPassword = "TestPassword12345";

        //then
        assertFalse(ValidationHelper.getInstance().passwordIsValid(invalidPassword));
    }

    @Test
    public void testPasswordWithOnlyLowercaseCharacters() {
        //when
        String invalidPassword = "testpassword123@";

        //then
        assertFalse(ValidationHelper.getInstance().passwordIsValid(invalidPassword));
    }

    @Test
    public void testPasswordWithOnlyUppercaseCharacters() {
        //when
        String invalidPassword = "TESTPASSWORD@";

        //then
        assertFalse(ValidationHelper.getInstance().passwordIsValid(invalidPassword));
    }
}
