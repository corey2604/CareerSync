package controllers;

import com.typesafe.config.Config;
import org.junit.Test;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static play.mvc.Http.Status.OK;

public class LogInControllerTest {

    @Test
    public void testLogIn() {
        //given
        FormFactory mockFormFactory = mock(FormFactory.class);
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();
        Config config = mock(Config.class);

        //when
        Result result = new LogInController(config, mockFormFactory).logIn(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }
}
