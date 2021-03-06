package controllers;

import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import models.UserAccountDetails;
import models.UserSignInRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.*;

@RunWith(MockitoJUnitRunner.class)
public class LogInControllerTest {

    @Mock
    private Config mockConfig;

    @Mock
    private FormFactory mockFormFactory;

    @Mock
    private Form mockForm;

    @Mock
    private DynamoDB mockDynamoDb;

    @Mock
    private Table mockTable;

    @Mock
    private Item mockItem;

    @Mock
    private Object mockObject;


    @Test
    public void testLogIn() {
        //given
        FormFactory mockFormFactory = mock(FormFactory.class);
        Http.RequestImpl request = Helpers.fakeRequest()
                .build();

        //when
        Result result = new LogInController(mockConfig, mockFormFactory).logIn(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testLogInSubmitWithCorrectDetails() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .build();
        UserSignInRequest signInRequest = spy(UserSignInRequest.class);
        DynamoDbTableProvider.setInstance(mockDynamoDb);
        doReturn("41c3s16c84v5pakuejkjn8sslp").when(mockConfig).getString("clientId");
        doReturn("eu-west-1_FehhvQScE").when(mockConfig).getString("userPoolId");
        doReturn(mockForm).when(mockFormFactory).form(any());
        doReturn(mockForm).when(mockForm).bindFromRequest();
        doReturn(signInRequest).when(mockForm).get();
        doReturn(mockTable).when(mockDynamoDb).getTable(any());
        doReturn(mockItem).when(mockTable).getItem(any(), any());
        doReturn(mockObject).when(mockItem).get("userType");
        doReturn("recruiter").when(mockObject).toString();
        signInRequest.setUsername("validUsername");
        signInRequest.setPassword("Test12345@");

        //when
        Result result = new LogInController(mockConfig, mockFormFactory).logInSubmit();

        //then

        //Assert user is redirected to home page
        assertEquals(SEE_OTHER, result.status());
        assertEquals("/", result.redirectLocation().get());

        //Assert cookies are populated correctly
        assertEquals("validUsername", result.cookie("username").value());
        assertEquals("recruiter", result.cookie("userType").value());
    }

    @Test
    public void testLogInSubmitWithIncorrectDetails() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .build();
        UserSignInRequest signInRequest = spy(UserSignInRequest.class);
        doReturn("41c3s16c84v5pakuejkjn8sslp").when(mockConfig).getString("clientId");
        doReturn("eu-west-1_FehhvQScE").when(mockConfig).getString("userPoolId");
        doReturn(mockForm).when(mockFormFactory).form(any());
        doReturn(mockForm).when(mockForm).bindFromRequest();
        doReturn(signInRequest).when(mockForm).get();
        signInRequest.setUsername("invalidUsername");
        signInRequest.setPassword("invalidPassword");

        //when
        Result result = new LogInController(mockConfig, mockFormFactory).logInSubmit();

        //then
        assertEquals(BAD_REQUEST, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());

        //Assert cookies aren't populated
        assertNull(request.cookie("username"));
        assertNull(request.cookie("userType"));
    }

    @After
    public void tearDown() {
        reset(mockConfig,
                mockFormFactory,
                mockForm,
                mockDynamoDb,
                mockTable,
                mockItem,
                mockObject);
        DynamoDbTableProvider.setInstance(null);
    }
}
