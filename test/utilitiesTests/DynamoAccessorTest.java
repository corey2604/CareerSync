package utilitiesTests;

import awsWrappers.AmazonDynamoDbClientWrapper;
import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import models.JobDescription;
import models.UserAccountDetails;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import utilities.DynamoAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DynamoAccessorTest {
    private static final String USERNAME = "fakeUsername";
    private static final String SECOND_USERNAME = "secondUsername";
    private static final String EXISTING_RECRUITER = "testRecruiterUser";
    private static final String EXISTING_REFERENCE_CODE = "TestReference";
    private static final String FIRST_NAME = "firstName";
    private static final String SURNAME = "surname";
    private static final String EMAIL = "fake@email.com";
    private static final String PHONE_NUMBER = "0123456789";

    @Mock
    private DynamoDB mockDynamoDb;

    @Mock
    private Table mockTable;

    @Mock
    private Item mockItem;

    @Mock
    private AmazonDynamoDB mockAmazonDynamoDb;

    @Mock
    private ScanResult mockScanResult;

    @Mock
    private Map<String, AttributeValue> mockItemValue;

    @Mock
    private AttributeValue mockAttributeValue;

    @Before
    public void setUp() {
        DynamoDbTableProvider.setInstance(mockDynamoDb);
        doReturn(mockTable).when(mockDynamoDb).getTable(any());
        doReturn(mockItem).when(mockTable).getItem("username", USERNAME);
    }

    @Test
    public void testRetrievesCorrectUserAccountDetails() {
        //given
        doReturn(USERNAME).when(mockItem).get("username");
        doReturn(FIRST_NAME).when(mockItem).get("firstName");
        doReturn(SURNAME).when(mockItem).get("surname");
        doReturn(EMAIL).when(mockItem).get("email");
        doReturn(PHONE_NUMBER).when(mockItem).get("phoneNumber");

        //when
        UserAccountDetails userAccountDetails = DynamoAccessor.getInstance().getUserAccountDetails(USERNAME);

        //then
        assertEquals(USERNAME, userAccountDetails.getUsername());
        assertEquals(FIRST_NAME, userAccountDetails.getFirstName());
        assertEquals(SURNAME, userAccountDetails.getSurname());
        assertEquals(EMAIL, userAccountDetails.getEmailAddress());
        assertEquals(PHONE_NUMBER, userAccountDetails.getPhoneNumber());
    }

    @Test
    public void testSuccessfullyRetrievesAllUsernames() {
        //given
        List<Map<String, AttributeValue>> mockItemList = new ArrayList<>();
        mockItemList.add(mockItemValue);
        mockItemList.add(mockItemValue);
        AmazonDynamoDbClientWrapper.setInstance(mockAmazonDynamoDb);
        doReturn(mockScanResult).when(mockAmazonDynamoDb).scan(any());
        doReturn(mockItemList).when(mockScanResult).getItems();
        when(mockAttributeValue.getS()).thenReturn(USERNAME, SECOND_USERNAME);
        when(mockItemValue.get("username")).thenReturn(mockAttributeValue, mockAttributeValue);

        //when
        List<String> usernames = DynamoAccessor.getInstance().getAllUsernames();

        //then
        assertEquals(2, usernames.size());
        assertEquals(USERNAME, usernames.get(0));
        assertEquals(SECOND_USERNAME, usernames.get(1));
    }

    @Test
    public void testGetJobDescription() {
        //when
        JobDescription jobDescription = DynamoAccessor.getInstance().getJobDescription(EXISTING_RECRUITER, EXISTING_REFERENCE_CODE);

        //then
        assertNotNull(jobDescription);
        assertEquals(EXISTING_RECRUITER, jobDescription.getRecruiter());
        assertEquals(EXISTING_REFERENCE_CODE, jobDescription.getReferenceCode());
    }

    @After
    public void tearDown() {
        reset(mockDynamoDb,
                mockTable,
                mockItem,
                mockAmazonDynamoDb,
                mockScanResult,
                mockItemValue,
                mockAttributeValue);
        DynamoDbTableProvider.setInstance(null);
        AmazonDynamoDbClientWrapper.setInstance(null);
    }
}
