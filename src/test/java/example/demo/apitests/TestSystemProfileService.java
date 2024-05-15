package example.demo.apitests;

import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestSystemProfileService {
    private static final String BASE_URL_LOGIN = "http://ridx.id.vn:3002";
    private static final String BASE_URL_SYSTEM_PROFILE = "http://ridx.id.vn:3004";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_ACCEPT = "accept";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String SYSTEM_PROFILES_ENDPOINT = "/system_profiles/";
    private static final String ALL_ACCEPT = "*/*";
    public static Dotenv dotenv = Dotenv.load();
    public static String userEmail = dotenv.get("USER_EMAIL");
    public static String userPassword = dotenv.get("USER_PASSWORD");
    private String token = "";
    private String id = "";

    @Test
    public void testLoginAPI() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", userEmail);
        jsonObject.put("password", userPassword);

        RestAssured.baseURI = BASE_URL_LOGIN;

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

    @Test(dependsOnMethods = "testLoginAPI")
    public void testGetSystemProfiles() {
        RestAssured.baseURI = BASE_URL_SYSTEM_PROFILE;

        Response response = RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + token)
                .when()
                .get(SYSTEM_PROFILES_ENDPOINT + "?current=1&pageSize=0")
                .andReturn();

        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");

        Assert.assertEquals(statusCode, 200, "Fetching system profiles should return status 200");
        Assert.assertTrue(success, "Fetching system profiles should be successful");
    }

    @Test(dependsOnMethods = "testLoginAPI")
    public void testGetSystemProfilesWithInvalidToken() {
        RestAssured.baseURI = BASE_URL_SYSTEM_PROFILE;

        Response response = RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + "abc")
                .when()
                .get(SYSTEM_PROFILES_ENDPOINT + "?current=1&pageSize=0")
                .andReturn();

        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");
        Assert.assertEquals(statusCode, 401);
        Assert.assertFalse(success);
    }

    @Test(dependsOnMethods = "testLoginAPI")
    public void testPostSystemProfiles() {
        RestAssured.baseURI = BASE_URL_SYSTEM_PROFILE;

        JSONObject requestBody = new JSONObject();
        requestBody.put("name", "string");
        requestBody.put("description", "string");
        requestBody.put("customFields", new JSONObject());

        Response response = RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + token)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .body(requestBody.toJSONString())
                .when()
                .post(SYSTEM_PROFILES_ENDPOINT)
                .andReturn();

        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");
        id = response.jsonPath().getString("data");

        Assert.assertEquals(statusCode, 201, "Posting system profiles should return status 201");
        Assert.assertTrue(success, "Posting system profiles should be successful");
        Assert.assertNotNull(id, "System profile ID should not be null after successful posting");
    }

    @Test(dependsOnMethods = "testLoginAPI")
    public void testPostSystemProfilesWithInvalidToken() {
        RestAssured.baseURI = BASE_URL_SYSTEM_PROFILE;

        JSONObject requestBody = new JSONObject();
        requestBody.put("name", "string");
        requestBody.put("description", "string");
        requestBody.put("customFields", new JSONObject());

        Response response = RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + "abc")
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .body(requestBody.toJSONString())
                .when()
                .post(SYSTEM_PROFILES_ENDPOINT)
                .andReturn();

        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");
        String actualId = response.jsonPath().getString("data");
        Assert.assertEquals(statusCode, 401);
        Assert.assertFalse(success);
        Assert.assertNull(actualId);
    }

    @Test(dependsOnMethods = "testLoginAPI")
    public void testPostSystemProfilesInvalidTypeOfName() {
        RestAssured.baseURI = BASE_URL_SYSTEM_PROFILE;

        JSONObject requestBody = new JSONObject();
        requestBody.put("name", 123);
        requestBody.put("description", "string");
        requestBody.put("customFields", new JSONObject());

        Response response = RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + token)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .body(requestBody.toJSONString())
                .when()
                .post(SYSTEM_PROFILES_ENDPOINT)
                .andReturn();

        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");
        String actualId = response.jsonPath().getString("data");

        Assert.assertEquals(statusCode, 400);
        Assert.assertFalse(success);
        Assert.assertNull(actualId);
    }

    @Test(dependsOnMethods = "testLoginAPI")
    public void testPostSystemProfilesInvalidTypeOfDescription() {
        RestAssured.baseURI = BASE_URL_SYSTEM_PROFILE;

        JSONObject requestBody = new JSONObject();
        requestBody.put("name", "string");
        requestBody.put("description", 1);
        requestBody.put("customFields", new JSONObject());

        Response response = RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + token)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .body(requestBody.toJSONString())
                .when()
                .post(SYSTEM_PROFILES_ENDPOINT)
                .andReturn();

        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");
        String actualId = response.jsonPath().getString("data");

        Assert.assertEquals(statusCode, 400);
        Assert.assertFalse(success);
        Assert.assertNull(actualId);
    }

    @Test(dependsOnMethods = "testPostSystemProfiles")
    public void testGetSystemProfileById() {
        RestAssured.baseURI = BASE_URL_SYSTEM_PROFILE;
        Response response = RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + token)
                .when()
                .get(SYSTEM_PROFILES_ENDPOINT + id)
                .andReturn();

        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");
        String actualId = response.jsonPath().getString("data.id");

        Assert.assertEquals(statusCode, 200);
        Assert.assertTrue(success);
        Assert.assertEquals(actualId, id);
    }

    @Test
    public void testGetSystemProfileByIncorrectId() {
        RestAssured.baseURI = BASE_URL_SYSTEM_PROFILE;

        Response response = RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + token)
                .when()
                .get(SYSTEM_PROFILES_ENDPOINT + 6666)
                .andReturn();

        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");
        String actualId = response.jsonPath().getString("data.id");

        Assert.assertEquals(statusCode, 500);
        Assert.assertFalse(success);
        Assert.assertNull(actualId);
    }

    @Test(dependsOnMethods = "testPostSystemProfiles")
    public void testUpdateSystemProfile() {
        RestAssured.baseURI = BASE_URL_SYSTEM_PROFILE;

        JSONObject requestBody = new JSONObject();
        requestBody.put("name", "stringe");
        requestBody.put("description", "stringe");
        requestBody.put("customFields", new JSONObject());

        Response response = RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + token)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .body(requestBody.toJSONString())
                .when()
                .put(SYSTEM_PROFILES_ENDPOINT + id)
                .andReturn();
        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");

        Assert.assertEquals(statusCode, 200);
        Assert.assertTrue(success);
    }

    @Test(dependsOnMethods = "testLoginAPI")
    public void testUpdateSystemProfileWithIncorrectId() {
        RestAssured.baseURI = BASE_URL_SYSTEM_PROFILE;

        JSONObject requestBody = new JSONObject();
        requestBody.put("name", "stringe");
        requestBody.put("description", "stringe");
        requestBody.put("customFields", new JSONObject());

        Response response = RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + token)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .body(requestBody.toJSONString())
                .when()
                .put(SYSTEM_PROFILES_ENDPOINT + 9999)
                .andReturn();

        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");

        Assert.assertEquals(statusCode, 500);
        Assert.assertFalse(success);
    }


    @Test(dependsOnMethods = "testUpdateSystemProfile")
    public void testDeleteSystemProfile() {
        RestAssured.baseURI = BASE_URL_SYSTEM_PROFILE;
        Response response = RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + token)
                .when()
                .delete(SYSTEM_PROFILES_ENDPOINT + id)
                .andReturn();

        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");

        Assert.assertEquals(statusCode, 200);
        Assert.assertTrue(success);
    }

    @Test(dependsOnMethods = "testLoginAPI")
    public void testDeleteSystemProfileWithIncorrectId() {
        RestAssured.baseURI = BASE_URL_SYSTEM_PROFILE;
        Response response = RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_AUTHORIZATION, TOKEN_PREFIX + token)
                .when()
                .delete(SYSTEM_PROFILES_ENDPOINT + 8888)
                .andReturn();
        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");
        Assert.assertEquals(statusCode, 500);
        Assert.assertFalse(success);
    }
}
