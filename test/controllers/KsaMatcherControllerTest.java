package controllers;

import models.JobDescription;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import utilities.KsaMatcher;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.OK;

@RunWith(MockitoJUnitRunner.class)
public class KsaMatcherControllerTest {

    private static final String USER_WITH_RECOMMENDATIONS = "userWithRecommendations";
    private static final String USER_WITHOUT_RECOMMENDATIONS = "userWithNoRecommendations";

    @Mock
    private KsaMatcher mockKsaMatcher;

    @Mock
    private JobDescription mockJobDescription;

    @Before
    public void setUp() {
        when(mockKsaMatcher.getJobRecommendations(USER_WITH_RECOMMENDATIONS)).thenReturn(Collections.singletonList(mockJobDescription));
    }

    @Test
    public void testViewJobRecommendations() {
        //given
        KsaMatcher.setInstance(mockKsaMatcher);
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", USER_WITH_RECOMMENDATIONS).build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();

        //when
        Result result = new KsaMatcherController().viewJobRecommendations(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
        assertEquals(1, mockKsaMatcher.getJobRecommendations(USER_WITH_RECOMMENDATIONS).size());
    }

    @Test
    public void testViewJobRecommendationsForUserWithNoRecommendations() {
        //given
        KsaMatcher.setInstance(mockKsaMatcher);
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", USER_WITHOUT_RECOMMENDATIONS).build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();

        //when
        Result result = new KsaMatcherController().viewJobRecommendations(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
        assertEquals(0, mockKsaMatcher.getJobRecommendations(USER_WITHOUT_RECOMMENDATIONS).size());
    }

    @After
    public void tearDown() {
        reset(mockKsaMatcher,
                mockJobDescription);
        KsaMatcher.setInstance(null);
    }
}
