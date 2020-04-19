package controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import utilities.DynamoAccessor;
import utilities.FileHandler;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;

@RunWith(MockitoJUnitRunner.class)
public class HomeControllerTest extends WithApplication {

    @Mock
    private DynamoAccessor mockDynamoAccessor;

    @Mock
    private FileHandler mockFileHandler;

    @Before
    public void setUp() {
        DynamoAccessor.setDynamoAccessor(mockDynamoAccessor);
        doReturn(true).when(mockDynamoAccessor).doesUserHaveKsas(any());
    }

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void testIndexWithCandidateLoggedIn() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();

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
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "recruiter").build())
                .build();

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

        //when
        Result result = new HomeController().index(request);

        //then
        assertEquals(SEE_OTHER, result.status());
        assertEquals(result.redirectLocation().get(), "/logIn");
    }

    @Test
    public void testLogOut() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest().cookie(Http.Cookie.builder("username", "fakeUser").build()).build();

        //when
        Result result = new HomeController().logOut(request);

        //then
        assertEquals(SEE_OTHER, result.status());
        assertEquals(result.redirectLocation().get(), "/logIn");
        assertTrue(result.cookies().getCookie("username").get().value().isEmpty());
    }

    @Test
    public void testUploadFile() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();
        FileHandler.setFileHandler(mockFileHandler);
        doReturn(true).when(mockFileHandler).uploadFile("username");

        //when
        Result result = new HomeController().uploadFile(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testViewCv() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();
        FileHandler.setFileHandler(mockFileHandler);
        doNothing().when(mockFileHandler).getFileFromUsername("username");

        //when
        Result result = new HomeController().viewCv(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testViewCvForUser() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "recruiter").build())
                .build();
        FileHandler.setFileHandler(mockFileHandler);
        doNothing().when(mockFileHandler).getFileFromUsername("username");

        //when
        Result result = new HomeController().viewCvForUser(request, "username", "jobTitle");

        //then
        assertEquals(SEE_OTHER, result.status());
        assertEquals("/getPotentialCandidates?recruiter=fakeName&referenceCode=jobTitle", result.redirectLocation().get());
    }

    @After
    public void tearDown() {
        reset(mockDynamoAccessor, mockFileHandler);
        DynamoAccessor.setDynamoAccessor(null);
        FileHandler.setFileHandler(null);
    }

}
