package controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import utilities.LoginChecker;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LoginChecker.class)
public class HomeControllerTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void testIndexWithCandidateLoggedIn() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest().cookie(Http.Cookie.builder("userType", "candidate").build()).build();
        PowerMockito.mockStatic(LoginChecker.class);
        BDDMockito.given(LoginChecker.isLoggedin(any())).willReturn(true);

        //when
        Result result = new HomeController().index(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testIndexWithRecruiterLoggedIn() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest().cookie(Http.Cookie.builder("userType", "recruiter").build()).build();
        PowerMockito.mockStatic(LoginChecker.class);
        BDDMockito.given(LoginChecker.isLoggedin(any())).willReturn(true);

        //when
        Result result = new HomeController().index(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testIndexWhenNotLoggedIn() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest().cookie(Http.Cookie.builder("userType", "recruiter").build()).build();
        PowerMockito.mockStatic(LoginChecker.class);
        BDDMockito.given(LoginChecker.isLoggedin(any())).willReturn(false);

        //when
        Result result = new HomeController().index(request);

        //then
        assertEquals(SEE_OTHER, result.status());
    }

}
