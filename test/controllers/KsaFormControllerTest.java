package controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import utilities.DynamoAccessor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.OK;

@RunWith(MockitoJUnitRunner.class)
public class KsaFormControllerTest {

    @Mock
    private DynamoAccessor mockDynamoAccessor;

    @Mock
    private FormFactory mockFormFactory;

    @Before
    public void setUp() {
        DynamoAccessor.setDynamoAccessor(mockDynamoAccessor);
        doReturn(true).when(mockDynamoAccessor).doesUserHaveKsas(any());
    }

    @Test
    public void testLoadForm() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();

        //when
        Result result = new KsaFormController(mockFormFactory).loadForm(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @After
    public void tearDown() {
        reset(mockDynamoAccessor, mockFormFactory);
    }
}
