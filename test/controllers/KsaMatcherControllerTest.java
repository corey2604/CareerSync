package controllers;

import models.JobDescription;
import org.junit.Test;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import utilities.KsaMatcher;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.OK;

public class KsaMatcherControllerTest {

    @Test
    public void testViewJobRecommendations() {
        //given
        KsaMatcher mockKsaMatcher = mock(KsaMatcher.class);
        KsaMatcher.setInstance(mockKsaMatcher);
        JobDescription mockJobDescription = mock(JobDescription.class);
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();
        when(mockKsaMatcher.getJobRecommendations(any())).thenReturn(Collections.singletonList(mockJobDescription));

        //when
        Result result = new KsaMatcherController().viewJobRecommendations(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }
}
