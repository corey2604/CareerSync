package controllers;

import models.KsaForm;
import models.UserKsas;
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
import utilities.DynamoAccessor;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;

@RunWith(MockitoJUnitRunner.class)
public class KsaFormControllerTest {

    @Mock
    private DynamoAccessor mockDynamoAccessor;

    @Mock
    private FormFactory mockFormFactory;

    @Mock
    private Form mockForm;

    @Mock
    private UserKsas mockUserKsas;

    @Mock
    private KsaForm mockKsaForm;

    @Before
    public void setUp() {
        DynamoAccessor.setDynamoAccessor(mockDynamoAccessor);
        doReturn(true).when(mockDynamoAccessor).doesUserHaveKsas(any());
        doReturn("MSC").when(mockUserKsas).getQualificationLevel();
        doReturn("Computing Systems").when(mockUserKsas).getQualificationArea();
        doReturn(Collections.EMPTY_LIST).when(mockUserKsas).getPeopleSkills();
        doReturn(Collections.EMPTY_LIST).when(mockUserKsas).getAdministrativeOrOrganisational();
        doReturn(Collections.EMPTY_LIST).when(mockUserKsas).getCommunicationSkills();
        doReturn(Collections.EMPTY_LIST).when(mockUserKsas).getCreativeOrInnovative();
        doReturn(Collections.EMPTY_LIST).when(mockUserKsas).getFinancialKnowledgeAndSkills();
        doReturn(Collections.EMPTY_LIST).when(mockUserKsas).getThinkingAndAnalysis();
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

    @Test
    public void testEditKsas() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();
        doReturn(mockUserKsas).when(mockDynamoAccessor).getKsasForUser("fakeName");

        //then
        Result result = new KsaFormController(mockFormFactory).editKsas(request);

        //when
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testSubmitForm() {
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();
        doReturn(mockForm).when(mockFormFactory).form(KsaForm.class);
        doReturn(mockForm).when(mockForm).bindFromRequest(request);
        doReturn(mockKsaForm).when(mockForm).get();
        doNothing().when(mockDynamoAccessor).putKsasInTable("fakeName", mockKsaForm);

        //then
        Result result = new KsaFormController(mockFormFactory).submitForm(request);

        //when
        assertEquals(SEE_OTHER, result.status());
        assertEquals("/", result.redirectLocation().get());
    }

    @After
    public void tearDown() {
        reset(mockDynamoAccessor, mockFormFactory);
        DynamoAccessor.setDynamoAccessor(null);
    }
}
