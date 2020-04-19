package controllers;

import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import models.JobDescription;
import models.UserAccountDetails;
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
import utilities.KsaMatcher;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;

@RunWith(MockitoJUnitRunner.class)
public class JobDescriptionControllerTest {

    @Mock
    private FormFactory mockFormFactory;

    @Mock
    private DynamoDB mockDB;

    @Mock
    private Table mockTable;

    @Mock
    private DeleteItemOutcome mockDeleteItemOutcome;

    @Mock
    private ItemCollection mockItemCollection;

    @Mock
    private IteratorSupport mockIterator;

    @Mock
    private JobDescription mockJobDescription;

    @Mock
    private DynamoAccessor mockDynamoAccessor;

    @Mock
    private UserKsas mockUserKsas;

    @Mock
    private KsaMatcher mockKsaMatcher;

    @Mock
    private Form mockForm;

    @Mock
    private UserAccountDetails mockUserAccountDetails;

    @Before
    public void setup() {
        DynamoDbTableProvider.setDynamoDb(mockDB);
        DynamoAccessor.setDynamoAccessor(mockDynamoAccessor);
        KsaMatcher.setInstance(mockKsaMatcher);
        when(mockDB.getTable(any())).thenReturn(mockTable);
        when(mockTable.deleteItem(any(DeleteItemSpec.class))).thenReturn(mockDeleteItemOutcome);
        when(mockTable.query(any(QuerySpec.class))).thenReturn(mockItemCollection);
        when(mockItemCollection.iterator()).thenReturn(mockIterator);
        when(mockIterator.hasNext()).thenReturn(false);
        doReturn(mockJobDescription).when(mockDynamoAccessor).getJobDescription(any(), any());

        doReturn("MSC").when(mockUserKsas).getQualificationLevel();
        doReturn("Computing Systems").when(mockUserKsas).getQualificationArea();
        doReturn(Collections.EMPTY_LIST).when(mockUserKsas).getPeopleSkills();
        doReturn(Collections.EMPTY_LIST).when(mockUserKsas).getAdministrativeOrOrganisational();
        doReturn(Collections.EMPTY_LIST).when(mockUserKsas).getCommunicationSkills();
        doReturn(Collections.EMPTY_LIST).when(mockUserKsas).getCreativeOrInnovative();
        doReturn(Collections.EMPTY_LIST).when(mockUserKsas).getFinancialKnowledgeAndSkills();
        doReturn(Collections.EMPTY_LIST).when(mockUserKsas).getThinkingAndAnalysis();

        doReturn(Optional.of("Computing Systems")).when(mockJobDescription).getQualificationArea();
        doReturn("101").when(mockJobDescription).getReferenceCode();
        doReturn("fakeUser").when(mockJobDescription).getRecruiter();
        doReturn("here").when(mockJobDescription).getLocation();
        doReturn("company").when(mockJobDescription).getCompanyOrOrganisation();
        doReturn("37.5").when(mockJobDescription).getHours();
        doReturn("Lead Tester").when(mockJobDescription).getJobTitle();
        doReturn("100000000000").when(mockJobDescription).getSalary();
        doReturn("To Test").when(mockJobDescription).getMainPurposeOfJob();
        doReturn("Testing").when(mockJobDescription).getMainResponsibilities();
        doReturn("01-01-2100").when(mockJobDescription).getClosingDate();
        doReturn(Optional.of("Permanent")).when(mockJobDescription).getDuration();
        doReturn(Optional.empty()).when(mockJobDescription).getGeneral();
        doReturn(Optional.empty()).when(mockJobDescription).getReportsTo();
        doReturn(Optional.empty()).when(mockJobDescription).getGrade();
        doReturn(Optional.empty()).when(mockJobDescription).getResponsibleTo();
        doReturn(Optional.empty()).when(mockJobDescription).getDepartment();
        doReturn(Optional.empty()).when(mockJobDescription).getSection();
        doReturn("01-01-2100").when(mockJobDescription).getCreatedAt();
        doReturn("01-01-2100").when(mockJobDescription).getLastUpdatedAt();
        doReturn(75).when(mockJobDescription).getPercentageMatchThreshold();
    }

    @Test
    public void testUploadJobDescription() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest().build();

        //when
        Result result = new JobDescriptionController(mockFormFactory).uploadJobDescription(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testViewJobDescriptionForRecruiter() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "recruiter").build())
                .build();

        //when
        Result result = new JobDescriptionController(mockFormFactory).viewJobDescription(request, "fakeName", "101");

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testViewJobDescriptionForCandidate() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "candidate").build())
                .build();

        //when
        Result result = new JobDescriptionController(mockFormFactory).viewJobDescription(request, "fakeName", "101");

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testGetUploadedJobDescriptions() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .build();

        //when
        Result result = new JobDescriptionController(mockFormFactory).getUploadedJobDescriptions(request);

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testDeleteJobDescription() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .build();

        //when
        Result result = new JobDescriptionController(mockFormFactory).deleteJobDescription(request, "101");

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testSubmitJobDescriptionWithNoKsas() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .build();
        doReturn(mockForm).when(mockFormFactory).form(JobDescription.class);
        doReturn(mockForm).when(mockForm).bindFromRequest();
        doReturn(mockJobDescription).when(mockForm).get();

        //when
        Result result = new JobDescriptionController(mockFormFactory).submitJobDescription(request);

        //then
        assertEquals(SEE_OTHER, result.status());
        assertEquals("/", result.redirectLocation().get());
    }

    @Test
    public void testSubmitJobDescriptionWithPopulatedKsas() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .build();
        doReturn(mockForm).when(mockFormFactory).form(JobDescription.class);
        doReturn(mockForm).when(mockForm).bindFromRequest();
        doReturn(mockJobDescription).when(mockForm).get();

        doReturn(Optional.of("Computing Systems")).when(mockJobDescription).getQualificationArea();
        doReturn(Collections.singletonList("Listening")).when(mockJobDescription).getCommunicationSkills();
        doReturn(Collections.singletonList("Counselling")).when(mockJobDescription).getPeopleSkills();
        doReturn(Collections.singletonList("VAT")).when(mockJobDescription).getFinancialKnowledgeAndSkills();
        doReturn(Collections.singletonList("Statistics")).when(mockJobDescription).getThinkingAndAnalysis();
        doReturn(Collections.singletonList("Creative")).when(mockJobDescription).getCreativeOrInnovative();
        doReturn(Collections.singletonList("Planning")).when(mockJobDescription).getAdministrativeOrOrganisational();

        //when
        Result result = new JobDescriptionController(mockFormFactory).submitJobDescription(request);

        //then
        assertEquals(SEE_OTHER, result.status());
        assertEquals("/", result.redirectLocation().get());
    }

    @Test
    public void testSubmitEditedJobDescriptionWithNoKsas() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .build();
        doReturn(mockForm).when(mockFormFactory).form(JobDescription.class);
        doReturn(mockForm).when(mockForm).bindFromRequest();
        doReturn(mockJobDescription).when(mockForm).get();

        //when
        Result result = new JobDescriptionController(mockFormFactory).submitEditedJobDescription(request);

        //then
        assertEquals(SEE_OTHER, result.status());
        assertEquals("/", result.redirectLocation().get());
    }

    @Test
    public void testSubmitEditedJobDescriptionWithPopulatedKsas() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .build();
        doReturn(mockForm).when(mockFormFactory).form(JobDescription.class);
        doReturn(mockForm).when(mockForm).bindFromRequest();
        doReturn(mockJobDescription).when(mockForm).get();

        doReturn(Optional.of("Computing Systems")).when(mockJobDescription).getQualificationArea();
        doReturn(Collections.singletonList("Listening")).when(mockJobDescription).getCommunicationSkills();
        doReturn(Collections.singletonList("Counselling")).when(mockJobDescription).getPeopleSkills();
        doReturn(Collections.singletonList("VAT")).when(mockJobDescription).getFinancialKnowledgeAndSkills();
        doReturn(Collections.singletonList("Statistics")).when(mockJobDescription).getThinkingAndAnalysis();
        doReturn(Collections.singletonList("Creative")).when(mockJobDescription).getCreativeOrInnovative();
        doReturn(Collections.singletonList("Planning")).when(mockJobDescription).getAdministrativeOrOrganisational();

        //when
        Result result = new JobDescriptionController(mockFormFactory).submitJobDescription(request);

        //then
        assertEquals(SEE_OTHER, result.status());
        assertEquals("/", result.redirectLocation().get());
    }

    @Test
    public void testEditJobDescription() {
        //given
        doReturn(mockUserKsas).when(mockJobDescription).getUserKsasFromJobDescription();

        //when
        Result result = new JobDescriptionController(mockFormFactory).editJobDescription("fakeName", "101");

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testGetCandidateKsaProfile() {
        //given
        doReturn(mockUserKsas).when(mockDynamoAccessor).getKsasForUser("fakeUser");

        //when
        Result result = new JobDescriptionController(mockFormFactory).getCandidateKsaProfile("fakeName", "surname", "fakeUser");

        //then
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testGetPotentialCandidates() {
        //given
        doReturn(Collections.EMPTY_LIST).when(mockKsaMatcher).getPotentialCandidates(any(), any());

        //when
        Result result = new JobDescriptionController(mockFormFactory).getPotentialCandidates("fakeName", "101");

        //then
        verify(mockKsaMatcher, times(1)).getPotentialCandidates(any(), any());
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testViewUserDetails() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "recruiter").build())
                .build();
        doReturn(mockUserAccountDetails).when(mockDynamoAccessor).getUserAccountDetails("userName");
        doReturn(true).when(mockDynamoAccessor).doesUserHaveKsas("fakeName");

        //when
        Result result = new JobDescriptionController(mockFormFactory).viewUserDetails(request, "userName");

        //then
        verify(mockDynamoAccessor, times(1)).getUserAccountDetails("userName");
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testSaveJobDescription() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "recruiter").build())
                .build();
        doNothing().when(mockDynamoAccessor).saveJobDescriptionForUser(anyString(), any(), any());
        doReturn(Collections.EMPTY_LIST).when(mockKsaMatcher).getJobRecommendations(anyString());

        //when
        Result result = new JobDescriptionController(mockFormFactory).saveJobDescription(request, "userName", "101");

        //then
        verify(mockDynamoAccessor, times(1)).saveJobDescriptionForUser(anyString(), any(), any());
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testGetSavedJobDescriptionsForUser() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "recruiter").build())
                .build();
        doReturn(Collections.EMPTY_LIST).when(mockDynamoAccessor).getSavedJobSpecifications("fakeName");

        //when
        Result result = new JobDescriptionController(mockFormFactory).getSavedJobDescriptionsForUser(request);

        //then
        verify(mockDynamoAccessor, times(1)).getSavedJobSpecifications("fakeName");
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @Test
    public void testRemoveSavedJobDescription() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest()
                .cookie(Http.Cookie.builder("username", "fakeName").build())
                .cookie(Http.Cookie.builder("userType", "recruiter").build())
                .build();
        doNothing().when(mockDynamoAccessor).removeSavedJobDescription(anyString(), any(), any());
        doReturn(Collections.EMPTY_LIST).when(mockKsaMatcher).getJobRecommendations(anyString());

        //when
        Result result = new JobDescriptionController(mockFormFactory).removeSavedJobDescription(request, "username","101");

        //then
        verify(mockDynamoAccessor, times(1)).removeSavedJobDescription(anyString(), any(), any());
        assertEquals(OK, result.status());
        assertEquals("text/html", result.contentType().get());
        assertEquals("utf-8", result.charset().get());
    }

    @After
    public void tearDown() {
        reset(mockFormFactory,
                mockDB,
                mockTable,
                mockDeleteItemOutcome,
                mockItemCollection,
                mockIterator,
                mockJobDescription,
                mockDynamoAccessor,
                mockUserKsas,
                mockKsaMatcher,
                mockForm,
                mockUserAccountDetails);
        DynamoDbTableProvider.setDynamoDb(null);
        DynamoAccessor.setDynamoAccessor(null);
        KsaMatcher.setInstance(null);
    }
}
