package utilities;

import org.junit.Test;
import play.mvc.Http;
import play.test.Helpers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoginCheckerTest {

    @Test
    public void testReturnsTrueIfUserIsLoggedIn() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeUser").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();

        //when
        boolean isLoggedIn = LoginChecker.getInstance().isLoggedin(request);

        //then
        assertTrue(isLoggedIn);
    }

    @Test
    public void testReturnsFalseIfUserIsNotLoggedIn() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .build();

        //when
        boolean isLoggedIn = LoginChecker.getInstance().isLoggedin(request);

        //then
        assertFalse(isLoggedIn);
    }
}
