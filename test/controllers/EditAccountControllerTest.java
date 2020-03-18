package controllers;

import models.UserAccountDetails;
import org.junit.Test;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import utilities.DynamoAccessor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.OK;

public class EditAccountControllerTest {

    @Test
    public void testEditAccount() {
        //given
        FormFactory mockFormFactory = mock(FormFactory.class);
        DynamoAccessor mockDynamoAccessor = mock(DynamoAccessor.class);
        UserAccountDetails mockUserAccountDetails = mock(UserAccountDetails.class);
        DynamoAccessor.setDynamoAccessor(mockDynamoAccessor);
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();
        doReturn(mockUserAccountDetails).when(mockDynamoAccessor).getUserAccountDetails(any());

        //when
        Result result = new EditAccountController(mockFormFactory).editAccount(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }
}
