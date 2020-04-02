import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NewsApiAT extends TestBase {


    // return true if fresh stories are available in dublin city area since yesterday at 00:00:00.0000AM
    @Test
    public void testFreshStoriesToday() {

        Response getStoriesResult = given().spec(requestSpecification).
                basePath("/stories").
                header("X-AYLIEN-NewsAPI-Application-ID", NEWS_API_APPLICATION_ID).
                header("X-AYLIEN-NewsAPI-Application-Key", NEWS_API_APPLICATION_KEY).
                queryParam("source.locations.city[]", "dublin").
                queryParam("published_at.start", "NOW-1DAY/DAY").
                queryParam("published_at.end", "NOW").
                when().get();

        assertEquals(HttpStatus.SC_OK, getStoriesResult.statusCode());

        JsonPath jsonPathEvaluator = getStoriesResult.jsonPath();

        assertTrue(jsonPathEvaluator.getList("stories").size() > 0);
    }


    /*

    Keyword search works correctly on a set of words of yours.

    */

    // check that keyword search works correctly on news title and body field
    @Test
    public void testKeywordSearchFunctionWithNewsApi() {

        Response getStoriesResult = given().spec(requestSpecification).
                basePath("/stories").
                header("X-AYLIEN-NewsAPI-Application-ID", NEWS_API_APPLICATION_ID).
                header("X-AYLIEN-NewsAPI-Application-Key", NEWS_API_APPLICATION_KEY).
                queryParam("title", "dublin").
                queryParam("body", "lockdown").
                queryParam("source.locations.city[]", "dublin").
                queryParam("published_at.start", "NOW-1DAY").
                queryParam("published_at.end", "NOW").
                when().get();

        assertEquals(HttpStatus.SC_OK, getStoriesResult.statusCode());

        JsonPath jsonPathEvaluator = getStoriesResult.jsonPath();


        // return true if keyword can be found in the title and body field of each story
        for (int i = 0; i < jsonPathEvaluator.getList("stories").size(); i++) {

            Map<String, Object> payloadElement = (Map<String, Object>) jsonPathEvaluator.getList("stories").get(i);

            assertTrue(payloadElement.get("title").toString().toLowerCase().contains("dublin"));
            System.out.println(payloadElement.get("title"));

            assertTrue(payloadElement.get("body").toString().toLowerCase().contains("lockdown"));
        }
    }
}
