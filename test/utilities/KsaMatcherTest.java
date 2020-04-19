package utilities;

import models.JobDescription;
import models.KsaValues;
import models.UserAccountDetails;
import models.UserKsas;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;

@RunWith(MockitoJUnitRunner.class)
public class KsaMatcherTest {
    private static final String EXISTING_RECRUITER = "requiredTestRecruiterUser";
    private static final String EXISTING_REFERENCE_CODE = "RequiredTestReference";

    @Mock
    private DynamoAccessor mockDynamoAccessor;

    @Mock
    private UserKsas mockUserKsas;

    @Mock
    private JobDescription mockJobDescription;

    @Before
    public void setUp() {
        DynamoAccessor.setDynamoAccessor(mockDynamoAccessor);
        doReturn(mockUserKsas).when(mockDynamoAccessor).getKsasForUser("username");
    }

    @Test
    public void testNoMatchingJobsAreReturnedForUserWithoutKsas() {
        //given
        doReturn(Collections.EMPTY_LIST).when(mockUserKsas).getAllKsas();

        //when
        List<JobDescription> matchingJobs = KsaMatcher.getInstance().getJobRecommendations("username");

        //then
        assertEquals(0, matchingJobs.size());
    }

    @Test
    public void testMatchingJobsAreReturnedForUserWithMatchingKsas() {
        //given
        doReturn(KsaValues.getAdministrativeOrOrganisational()).when(mockUserKsas).getAllKsas();

        //when
        List<JobDescription> matchingJobs = KsaMatcher.getInstance().getJobRecommendations("username");

        //then
        assertEquals(true, matchingJobs.size() > 0);
    }

    @Test
    public void testNoMatchingCandidatesAreReturnedForJobDescriptionWithoutKsas() {
        //given
        doReturn(mockJobDescription).when(mockDynamoAccessor).getJobDescription("recruiter", "ref01");
        doReturn(Collections.EMPTY_LIST).when(mockJobDescription).getAllJobRelatedKsas();

        //when
        List<UserAccountDetails> matchingCandidates = KsaMatcher.getInstance().getPotentialCandidates("recruiter", "ref01");

        //then
        assertEquals(0, matchingCandidates.size());
    }

    @Test
    public void testMatchingCandidatesAreReturnedForJobDescription() {
        //given
        DynamoAccessor.setDynamoAccessor(null);

        //when
        List<UserAccountDetails> matchingCandidates = KsaMatcher.getInstance().getPotentialCandidates(EXISTING_RECRUITER, EXISTING_REFERENCE_CODE);

        //then
        assertEquals(true, matchingCandidates.size() > 0);
    }

    @After
    public void tearDown() {
        DynamoAccessor.setDynamoAccessor(null);
        reset(mockDynamoAccessor, mockUserKsas);
    }

}
