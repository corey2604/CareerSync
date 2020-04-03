package controllers;

import com.typesafe.config.Config;
import models.UserAccountDetails;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.OK;

@RunWith(MockitoJUnitRunner.class)
public class RegisterControllerTest {

    @Mock
    private Config mockConfig;

    @Mock
    private FormFactory mockFormFactory;

    @Test
    public void testRegister() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
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

    @After
    public void tearDown() {
        reset(mockFormFactory);
    }
}
