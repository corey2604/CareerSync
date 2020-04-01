package controllers;

import awsWrappers.AwsCognitoIdentityProviderWrapper;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordResult;
import models.UserAccountDetails;
import org.junit.Test;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import utilities.DynamoAccessor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.BAD_REQUEST;
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

    @Test
    public void testUpdateUserAccountDetailsWithEmptyPassword() {
        //given
        FormFactory mockFormFactory = mock(FormFactory.class);
        Form mockForm = mock(Form.class);
        UserAccountDetails mockUserAccountDetails = spy(UserAccountDetails.class);
        mockUserAccountDetails.setPassword("");
        doReturn(mockForm).when(mockFormFactory).form(any());
        doReturn(mockForm).when(mockForm).bindFromRequest();
        doReturn(mockUserAccountDetails).when(mockForm).get();
        DynamoAccessor mockDynamoAccessor = mock(DynamoAccessor.class);
        DynamoAccessor.setDynamoAccessor(mockDynamoAccessor);
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();
        doReturn(mockUserAccountDetails).when(mockDynamoAccessor).getUserAccountDetails(any());

        //when
        Result result = new EditAccountController(mockFormFactory).updateUserAccountDetails(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testUpdateUserAccountDetailsWithInValidPassword() {
        //given
        FormFactory mockFormFactory = mock(FormFactory.class);
        Form mockForm = mock(Form.class);
        UserAccountDetails mockUserAccountDetails = spy(UserAccountDetails.class);
        mockUserAccountDetails.setPassword("test");
        doReturn(mockForm).when(mockFormFactory).form(any());
        doReturn(mockForm).when(mockForm).bindFromRequest();
        doReturn(mockUserAccountDetails).when(mockForm).get();
        DynamoAccessor mockDynamoAccessor = mock(DynamoAccessor.class);
        DynamoAccessor.setDynamoAccessor(mockDynamoAccessor);
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();
        doReturn(mockUserAccountDetails).when(mockDynamoAccessor).getUserAccountDetails(any());

        //when
        Result result = new EditAccountController(mockFormFactory).updateUserAccountDetails(request);

        //then
        assertEquals(BAD_REQUEST, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testUpdateUserAccountDetailsWithValidPassword() {
        //given
        FormFactory mockFormFactory = mock(FormFactory.class);
        Form mockForm = mock(Form.class);
        UserAccountDetails mockUserAccountDetails = spy(UserAccountDetails.class);
        mockUserAccountDetails.setPassword("Test12345@");
        doReturn(mockForm).when(mockFormFactory).form(any());
        doReturn(mockForm).when(mockForm).bindFromRequest();
        doReturn(mockUserAccountDetails).when(mockForm).get();
        DynamoAccessor mockDynamoAccessor = mock(DynamoAccessor.class);
        DynamoAccessor.setDynamoAccessor(mockDynamoAccessor);
        AWSCognitoIdentityProvider mockAwsCognitoIdentityProvider = mock(AWSCognitoIdentityProvider.class);
        AwsCognitoIdentityProviderWrapper.setInstance(mockAwsCognitoIdentityProvider);
        AdminSetUserPasswordResult mockAdminSetUserPasswordResult = mock(AdminSetUserPasswordResult.class);
        doReturn(mockAdminSetUserPasswordResult).when(mockAwsCognitoIdentityProvider).adminSetUserPassword(any());
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();
        doReturn(mockUserAccountDetails).when(mockDynamoAccessor).getUserAccountDetails(any());

        //when
        Result result = new EditAccountController(mockFormFactory).updateUserAccountDetails(request);

        //then
        verify(mockAwsCognitoIdentityProvider, times(1)).adminSetUserPassword(any());

        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }
}
