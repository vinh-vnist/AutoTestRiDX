package example.demo.apitests;

import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static example.demo.config.Config.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class TestAccountService extends TestAPI {
    private static final String LOGIN_ENDPOINT = "/login";
    private static final String LOGOUT_ENDPOINT = "/logout";
    private static final String CURRENT_USER_ENDPOINT = "/current_user";
    private static final String USERS_ENDPOINT = "/users";
    private String userToken = "";
    private String adminToken = "";

    private Response loginWithUsernamePassword(String username, String password) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", username);
        jsonObject.put("password", password);

        return RestAssured
                .given()
                .header(HEADER_ACCEPT, ALL_ACCEPT)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .body(jsonObject.toString())
                .when()
                .post(LOGIN_ENDPOINT)
                .andReturn();
    }
    @Test
    public void testLoginWithValidUserAccount(){
        Response response = loginWithUsernamePassword(USER_EMAIL, USER_PASSWORD);
        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");
        userToken = response.jsonPath().getString("data.token");
        String role = response.jsonPath().getString("data.user.role");

        Assert.assertEquals(statusCode, 200, "Login should return status 200");
        Assert.assertTrue(success, "Login should be successful");
        Assert.assertNotNull(userToken, "Token should not be null after successful login");
        Assert.assertEquals(role, "USER", "Role should be USER");
    }
    @Test
    public void testLoginWithValidAdminAccount(){
        Response response = loginWithUsernamePassword(ADMIN_EMAIL, ADMIN_PASSWORD);
        int statusCode = response.getStatusCode();
        boolean success = response.jsonPath().getBoolean("success");
        adminToken = response.jsonPath().getString("data.token");
        String role = response.jsonPath().getString("data.user.role");

        Assert.assertEquals(statusCode, 200, "Login should return status 200");
        Assert.assertTrue(success, "Login should be successful");
        Assert.assertNotNull(adminToken, "Token should not be null after successful login");
        Assert.assertEquals(role, "ADMIN", "Role should be ADMIN");
    }

    @Test
    public void testLogout(){
        given().
                header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON).
                header(HEADER_ACCEPT, ALL_ACCEPT).
                header(HEADER_AUTHORIZATION, TOKEN_PREFIX + userToken).
        when().
                post(LOGOUT_ENDPOINT).
        then().
                statusCode(equalTo(201)).
                body(is("true"));
    }
    @Test(dependsOnMethods = "testLoginWithValidUserAccount")
    public void testCurrentUser(){
        given().
                header(HEADER_ACCEPT, ALL_ACCEPT).
                header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON).
                header(HEADER_AUTHORIZATION, TOKEN_PREFIX + userToken).
        when().
                get(CURRENT_USER_ENDPOINT).
        then().
                statusCode(equalTo(200)).
                body("email", equalTo(USER_EMAIL)).
                body("role", equalTo("USER"));
    }

    @Test(dependsOnMethods = "testLoginWithValidAdminAccount")
    public void testGetListOfUsers(){
        given().
                header(HEADER_ACCEPT, ALL_ACCEPT).
                header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON).
                header(HEADER_AUTHORIZATION, TOKEN_PREFIX + adminToken).
        when().
                get(USERS_ENDPOINT).
        then().
                statusCode(equalTo(200)).
                body("success", equalTo(true)).
                body("data", notNullValue());
    }
    @Test(dependsOnMethods = "testLoginWithValidAdminAccount")
    public void testAddUserAccount(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", "testuser@gmail.com");
        jsonObject.put("password", "12345678");
        given().
                header(HEADER_ACCEPT, ALL_ACCEPT).
                header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON).
                header(HEADER_AUTHORIZATION, TOKEN_PREFIX + adminToken).
                body(jsonObject.toString()).
        when().
                post(USERS_ENDPOINT).
        then().
                statusCode(equalTo(201)).
                body("success", equalTo(true)).
                body("data", notNullValue());
    }
    @Test(dependsOnMethods = "testAddUserAccount")
    public void testUpdateUserAccount(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", "66415de74da8e2f7859f30bc");
        jsonObject.put("email", "user@gmail.com");
        jsonObject.put("active", true);
        given().
                header(HEADER_ACCEPT, ALL_ACCEPT).
                header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON).
                header(HEADER_AUTHORIZATION, TOKEN_PREFIX + adminToken).
                body(jsonObject.toString()).
        when().
                put(USERS_ENDPOINT).
        then().
                statusCode(equalTo(201)).
                body("success", equalTo(true)).
                body("data", notNullValue());
    }
}
