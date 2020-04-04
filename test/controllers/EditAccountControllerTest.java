package controllers;

import awsWrappers.AwsCognitoIdentityProviderWrapper;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordResult;
import models.UserAccountDetails;
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
import utilities.DynamoAccessor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;

@RunWith(MockitoJUnitRunner.class)
public class EditAccountControllerTest {

    @Mock
    private FormFactory mockFormFactory;

    @Mock
    private Form mockForm;

    @Mock
    private DynamoAccessor mockDynamoAccessor;

    @Mock
    private UserAccountDetails mockUserAccountDetails;

    @Mock
    private AdminSetUserPasswordResult mockAdminSetUserPasswordResult;

    @Mock
    private AWSCognitoIdentityProvider mockAwsCognitoIdentityProvider;

    @Test
    public void testEditAccount() {
        //given
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
        UserAccountDetails mockUserAccountDetails = spy(UserAccountDetails.class);
        mockUserAccountDetails.setPassword("Test12345@");
        doReturn(mockForm).when(mockFormFactory).form(any());
        doReturn(mockForm).when(mockForm).bindFromRequest();
        doReturn(mockUserAccountDetails).when(mockForm).get();
        DynamoAccessor.setDynamoAccessor(mockDynamoAccessor);
        AwsCognitoIdentityProviderWrapper.setInstance(mockAwsCognitoIdentityProvider);
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

    @After
    public void tearDown() {
        reset(mockFormFactory,
                mockForm,
                mockAdminSetUserPasswordResult,
                mockAwsCognitoIdentityProvider,
                mockUserAccountDetails);
        DynamoAccessor.setDynamoAccessor(null);
        AwsCognitoIdentityProviderWrapper.setInstance(null);
    }
}
