package example.demo.apitests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;

import static example.demo.config.Config.*;
import static example.demo.config.Config.CONTENT_TYPE_JSON;

public class TestAPI {
    private final String LOGIN_ENDPOINT = "/login";
    public String token = "";
    @BeforeTest
    public void setup(){
        RestAssured.baseURI = BASE_URL + API_PREFIX;
    }
    @BeforeClass
    public void getAuthenToken() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", USER_EMAIL);
        jsonObject.put("password", USER_PASSWORD);

        Response response = RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .body(jsonObject.toString())
                .when()
                .post(LOGIN_ENDPOINT)
                .andReturn();

        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");
        token = response.jsonPath().getString("data.token");

        Assert.assertEquals(statusCode, 200, "Login should return status 200");
        Assert.assertTrue(success, "Login should be successful");
        Assert.assertNotNull(token, "Token should not be null after successful login");
    }
}
