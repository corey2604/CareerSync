package controllers;

import awsWrappers.DynamoDbTableProvider;
import com.amazonaws.services.dynamodbv2.document.DeleteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.OK;

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


    @Before
    public void setup() {
        DynamoDbTableProvider.setDynamoDb(mockDB);
        when(mockDB.getTable(any())).thenReturn(mockTable);
        when(mockTable.deleteItem(any(DeleteItemSpec.class))).thenReturn(mockDeleteItemOutcome);
        when(mockTable.query(any(QuerySpec.class))).thenReturn(mockItemCollection);
        when(mockItemCollection.iterator()).thenReturn(mockIterator);
        when(mockIterator.hasNext()).thenReturn(false);
    }

    @Test
    public void testUploadJobApplication() {
        //given
        Http.RequestImpl request = Helpers.fakeRequest().build();

        //when
        Result result = new JobDescriptionController(mockFormFactory).uploadJobApplication(request);

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

    @After
    public void tearDown() {
        reset(mockFormFactory,
                mockDB,
                mockTable,
                mockDeleteItemOutcome,
                mockItemCollection,
                mockIterator);
    }
}
