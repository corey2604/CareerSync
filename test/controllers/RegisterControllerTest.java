package controllers;

import com.typesafe.config.Config;
import models.UserAccountDetails;
import org.junit.Test;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.OK;

public class RegisterControllerTest {

    @Test
    public void testRegister() {
        //given
        FormFactory mockFormFactory = mock(FormFactory.class);
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();
        Config config = mock(Config.class);

        //when
        Result result = new RegisterController(config, mockFormFactory).register(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }
}
