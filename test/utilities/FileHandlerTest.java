package utilities;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FileHandlerTest {

    @Test
    public void testReturnsTrueIfUserHasUploadedCv() {
        assertTrue(FileHandler.getInstance().doesUserHaveUploadedCV("candidateWithCV"));
    }

    @Test
    public void testReturnsFalseIfUserHasNoUploadedCv() {
        assertFalse(FileHandler.getInstance().doesUserHaveUploadedCV("candidateWithoutCV"));
    }

    @Test
    public void testReturnsFalseForUploadedCvIfUserDoesNotExist() {
        assertFalse(FileHandler.getInstance().doesUserHaveUploadedCV("username that doesn't exist"));
    }
}
