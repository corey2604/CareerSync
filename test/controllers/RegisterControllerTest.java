package controllers;

import com.typesafe.config.Config;
import models.UserAccountDetails;
import models.UserSignUpRequest;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.*;

@RunWith(MockitoJUnitRunner.class)
public class RegisterControllerTest {

    @Mock
    private Config mockConfig;

    @Mock
    private FormFactory mockFormFactory;

    @Mock
    private Form mockForm;

    @Mock
    private UserSignUpRequest mockSignUpRequest;

    @Test
    public void testRegister() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .build();
        doReturn(System.getenv("AWS_CLIENT_ID")).when(mockConfig).getString("clientId");
        doReturn(System.getenv("AWS_USER_POOL_ID")).when(mockConfig).getString("userPoolId");

        //when
        Result result = new RegisterController(mockConfig, mockFormFactory).register(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testRegisterSubmitResturnsBadRequestWithExistingUsername() {
        //given
        doReturn("candidateTest").when(mockSignUpRequest).getUsername();
        doReturn("0123456789").when(mockSignUpRequest).getPhoneNumber();
        doReturn("Test12345@").when(mockSignUpRequest).getPassword();
        doReturn(mockForm).when(mockFormFactory).form(UserSignUpRequest.class);
        doReturn(mockSignUpRequest).when(mockForm).get();

        //when
        Result result = new RegisterController(mockConfig, mockFormFactory).registerSubmit();

        //then
        assertEquals(BAD_REQUEST, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testRegisterSubmitResturnsBadRequestWithInvalidPhoneNumber() {
        //given
        doReturn(UUID.randomUUID().toString()).when(mockSignUpRequest).getUsername();
        doReturn("01asdasdasd456789").when(mockSignUpRequest).getPhoneNumber();
        doReturn("Test12345@").when(mockSignUpRequest).getPassword();
        doReturn(mockForm).when(mockFormFactory).form(UserSignUpRequest.class);
        doReturn(mockSignUpRequest).when(mockForm).get();

        //when
        Result result = new RegisterController(mockConfig, mockFormFactory).registerSubmit();

        //then
        assertEquals(BAD_REQUEST, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testRegisterSubmitResturnsBadRequestWithInvalidPassword() {
        //given
        doReturn(UUID.randomUUID().toString()).when(mockSignUpRequest).getUsername();
        doReturn("0123456789").when(mockSignUpRequest).getPhoneNumber();
        doReturn("Test").when(mockSignUpRequest).getPassword();
        doReturn(mockForm).when(mockFormFactory).form(UserSignUpRequest.class);
        doReturn(mockSignUpRequest).when(mockForm).get();

        //when
        Result result = new RegisterController(mockConfig, mockFormFactory).registerSubmit();

        //then
        assertEquals(BAD_REQUEST, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testRegisterSubmitSuccessfulWithValidDetails() {
        //given
        String username = UUID.randomUUID().toString();
        doReturn(username).when(mockSignUpRequest).getUsername();
        doReturn("0123456789").when(mockSignUpRequest).getPhoneNumber();
        doReturn("Test12345@").when(mockSignUpRequest).getPassword();
        doReturn("test@email.com").when(mockSignUpRequest).getEmail();
        doReturn("UnitTest").when(mockSignUpRequest).getFirstName();
        doReturn("User").when(mockSignUpRequest).getLastName();
        doReturn("eu-west-1_FehhvQScE").when(mockConfig).getString("userPoolId");
        doReturn(mockForm).when(mockFormFactory).form(UserSignUpRequest.class);
        doReturn(mockSignUpRequest).when(mockForm).get();

        //when
        Result result = new RegisterController(mockConfig, mockFormFactory).registerSubmit();

        //then
        assertEquals(SEE_OTHER, result.status());
        assertEquals("/", result.redirectLocation().get());
        assertEquals(username, result.cookies().getCookie("username").get().value());
    }

    @After
    public void tearDown() {
        reset(mockConfig, mockFormFactory, mockForm, mockSignUpRequest);
    }
}
