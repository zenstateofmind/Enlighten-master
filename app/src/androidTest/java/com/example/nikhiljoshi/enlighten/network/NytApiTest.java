package com.example.nikhiljoshi.enlighten.network;

import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import com.example.nikhiljoshi.enlighten.pojo.Book;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Tests for NYT API calls
 */
@RunWith(AndroidJUnit4.class)
public class NytApiTest extends InstrumentationTestCase {

    private MockWebServer server;
    private NytApi api;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        server = new MockWebServer();
        server.start();
        // Since we are modifying the API_BASE_URL... we end up
        // hitting the mock web server when we use okHttp to talk
        // to API
        ServiceGenerator.API_BASE_URL = server.url("/").toString();
        api = new NytApi();
    }

    /**
     * Hitting the servers returns the json for book reviews for Top Non Fiction Books
     */
    @Test
    public void testGetTopNonFictionBooksList() throws Exception {
        String filename = "positive_response.json";

        server.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(RestServiceTestHelper.getStringFromFile(getInstrumentation().getContext(), filename)));

        final List<Book> topNonFictionBooksList = api.getTopNonFictionBooksList(new Date());
        assertTrue(topNonFictionBooksList.size() > 0);
    }

    /**
     * Hit the servers to get top non fiction books but get a failure response code.
     * We should ensure that the test returns 0 books and ends gracefully
     */
    @Test
    public void testGetTopNonFictionBooksResponseUnsucessful() {
        server.enqueue(new MockResponse().setResponseCode(400));
        final List<Book> topNonFictionBooksList = api.getTopNonFictionBooksList(new Date());
        assertTrue(topNonFictionBooksList.size() == 0);
    }

}
