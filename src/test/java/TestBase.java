import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import org.junit.Before;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.requestSpecification;
import static io.restassured.RestAssured.responseSpecification;

public class TestBase {


    public final String NEWS_API_APPLICATION_ID = "0d9478dd";
    public final String NEWS_API_APPLICATION_KEY = "4d514602b2e4fc3e7bb54572cf7e131b";


    @Before
    public void setUp() {

        configureRestAssured();
    }

    public void configureRestAssured() {

        RestAssured.baseURI = "https://api.aylien.com/news";
        RestAssured.useRelaxedHTTPSValidation();

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setContentType("application/json");
        requestSpecification = requestSpecBuilder.build();

        ResponseSpecBuilder builder = new ResponseSpecBuilder();
        responseSpecification = builder.build();
    }
}
