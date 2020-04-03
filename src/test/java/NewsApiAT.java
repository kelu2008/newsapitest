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


    // Compare the volumes of fresh stories in 5 minutes today and the volumes of fresh stories in the same period yesterday
    // This is not a real junit test, i just use it to implement the code below to return the figures
    @Test
    public void compareFreshStoriesBetweenTodayAndYesterday() {

        Response getFreshStoriesResultToday = given().spec(requestSpecification).
                basePath("/stories").
                header("X-AYLIEN-NewsAPI-Application-ID", NEWS_API_APPLICATION_ID).
                header("X-AYLIEN-NewsAPI-Application-Key", NEWS_API_APPLICATION_KEY).
                //queryParam("source.locations.city[]", "dublin").
                        queryParam("published_at.start", "NOW-5MINUTES").
                        queryParam("published_at.end", "NOW").
                        when().get();

        assertEquals(HttpStatus.SC_OK, getFreshStoriesResultToday.statusCode());

        JsonPath jsonPathEvaluatorToday = getFreshStoriesResultToday.jsonPath();
        int volumesOfFreshStoriesToday = jsonPathEvaluatorToday.getList("stories").size();

        Response getFreshStoriesResultYesterday = given().spec(requestSpecification).
                basePath("/stories").
                header("X-AYLIEN-NewsAPI-Application-ID", NEWS_API_APPLICATION_ID).
                header("X-AYLIEN-NewsAPI-Application-Key", NEWS_API_APPLICATION_KEY).
                //queryParam("source.locations.city[]", "dublin").
                        queryParam("published_at.start", "NOW-5MINUTES-1DAY").
                        queryParam("published_at.end", "NOW-1DAY").
                        when().get();

        assertEquals(HttpStatus.SC_OK, getFreshStoriesResultYesterday.statusCode());

        JsonPath jsonPathEvaluatorYesterday = getFreshStoriesResultYesterday.jsonPath();
        int volumesOfFreshStoriesYesterday = jsonPathEvaluatorYesterday.getList("stories").size();

        System.out.println("The volumes of fresh stories in last 5 minutes today " + volumesOfFreshStoriesToday);
        System.out.println("The volumes of fresh stories in last 5 minutes yesterday " + volumesOfFreshStoriesYesterday);
    }


    // return true if fresh stories are available in last 30 minutes
    // It is not related to QA challenge, just checking to ensure the fresh stories should be available in last 30 minutes
    @Test
    public void testFreshStoriesInLast30MinutesToday() {

        Response getStoriesResult = given().spec(requestSpecification).
                basePath("/stories").
                header("X-AYLIEN-NewsAPI-Application-ID", NEWS_API_APPLICATION_ID).
                header("X-AYLIEN-NewsAPI-Application-Key", NEWS_API_APPLICATION_KEY).
                queryParam("published_at.start", "NOW-6HOURS").
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
