package qa.base;

import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

public class BaseApiTest {

    protected static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.requestSpecification = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().all();
    }

    protected Response getRequest(String endpoint) {
        Response response = given()
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .extract().response();

        Allure.addAttachment("Response: " + endpoint, "application/json", response.asString(), ".json");
        return response;
    }

    protected Response postRequest(String endpoint, Object body) {
        Response response = given()
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();

        Allure.addAttachment("POST Response: " + endpoint, "application/json", response.asString(), ".json");
        return response;
    }

    protected Response putRequest(String endpoint, Object body) {
        Response response = given()
                .body(body)
                .when()
                .put(endpoint)
                .then()
                .log().all()
                .extract().response();

        Allure.addAttachment("PUT Response: " + endpoint, "application/json", response.asString(), ".json");
        return response;
    }

    protected Response patchRequest(String endpoint, Object body) {
        Response response = given()
                .body(body)
                .when()
                .patch(endpoint)
                .then()
                .log().all()
                .extract().response();

        Allure.addAttachment("PATCH Response: " + endpoint, "application/json", response.asString(), ".json");
        return response;
    }

    protected Response deleteRequest(String endpoint) {
        Response response = given()
                .when()
                .delete(endpoint)
                .then()
                .log().all()
                .extract().response();

        Allure.addAttachment("DELETE Response: " + endpoint, "application/json", response.asString(), ".json");
        return response;
    }
}
