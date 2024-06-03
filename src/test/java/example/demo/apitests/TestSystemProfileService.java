package example.demo.apitests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static example.demo.config.Config.*;

public class TestSystemProfileService extends TestAPI {
    private static final String SYSTEM_PROFILES_ENDPOINT = "/system_profiles/";
    private String id = "";

    @Test()
    public void testGetSystemProfiles() {
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

    @Test()
    public void testGetSystemProfilesWithInvalidToken() {
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

    @Test()
    public void testPostSystemProfiles() {
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

    @Test()
    public void testPostSystemProfilesWithInvalidToken() {

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

    @Test()
    public void testPostSystemProfilesInvalidTypeOfName() {

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

    @Test()
    public void testPostSystemProfilesInvalidTypeOfDescription() {

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

    @Test()
    public void testUpdateSystemProfileWithIncorrectId() {
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

    @Test()
    public void testDeleteSystemProfileWithIncorrectId() {
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
