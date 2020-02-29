package controllers;

import org.junit.Test;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static play.mvc.Http.Status.OK;

public class JobApplicationControllerTest {

    @Test
    public void testUploadJobApplication() {
        //given
        FormFactory mockFormFactory = mock(FormFactory.class);
        Http.RequestImpl request = Helpers.fakeRequest().build();

        //when
        Result result = new JobApplicationController(mockFormFactory).uploadJobApplication(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }
}
