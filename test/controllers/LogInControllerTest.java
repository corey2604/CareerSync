package controllers;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import models.UserAccountDetails;
import models.UserSignInRequest;
import org.junit.Test;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.*;

public class LogInControllerTest {

    @Test
    public void testLogIn() {
        //given
        FormFactory mockFormFactory = mock(FormFactory.class);
        Http.RequestImpl request = Helpers.fakeRequest()
                .build();
        Config config = mock(Config.class);

        //when
        Result result = new LogInController(config, mockFormFactory).logIn(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testLogInSubmitWithCorrectDetails() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .build();
        Config config = mock(Config.class);
        doReturn("41c3s16c84v5pakuejkjn8sslp").when(config).getString("clientId");
        doReturn("eu-west-1_FehhvQScE").when(config).getString("userPoolId");
        FormFactory mockFormFactory = mock(FormFactory.class);
        Form mockForm = mock(Form.class);
        UserSignInRequest signInRequest = spy(UserSignInRequest.class);
        doReturn(mockForm).when(mockFormFactory).form(any());
        doReturn(mockForm).when(mockForm).bindFromRequest();
        doReturn(signInRequest).when(mockForm).get();
        signInRequest.setUsername("validUsername");
        signInRequest.setPassword("Test12345@");

        //when
        Result result = new LogInController(config, mockFormFactory).logInSubmit();

        //then

        //Assert user is redirected to home page
        assertEquals(SEE_OTHER, result.status());
        assertEquals("/", result.redirectLocation().get());

        //Assert cookies are populated correctly
        assertEquals("validUsername", result.cookie("username").value());
        assertEquals("recruiter", result.cookie("userType").value());
    }

    @Test
    public void testLogInSubmitWithIncorrectDetails() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .build();
        Config config = mock(Config.class);
        FormFactory mockFormFactory = mock(FormFactory.class);
        Form mockForm = mock(Form.class);
        UserSignInRequest signInRequest = spy(UserSignInRequest.class);
        doReturn(mockForm).when(mockFormFactory).form(any());
        doReturn(mockForm).when(mockForm).bindFromRequest();
        doReturn(signInRequest).when(mockForm).get();
        signInRequest.setUsername("invalidUsername");
        signInRequest.setPassword("invalidPassword");

        //when
        Result result = new LogInController(config, mockFormFactory).logInSubmit();

        //then
        assertEquals(BAD_REQUEST, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());

        //Assert cookies aren't populated
        assertNull(request.cookie("username"));
        assertNull(request.cookie("userType"));
    }
}
