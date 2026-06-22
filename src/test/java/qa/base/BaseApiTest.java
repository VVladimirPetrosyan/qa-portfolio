package qa.base;

import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static io.restassured.RestAssured.given;

/**
 * Базовый класс для API-тестов.
 * Настраивает RestAssured, логирование и Allure-степы.
 */
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

    @BeforeEach
    void startTest(org.junit.jupiter.api.TestInfo testInfo) {
        Allure.step("Начало теста: " + testInfo.getDisplayName());
    }

    @AfterEach
    void logResult(org.junit.jupiter.api.TestInfo testInfo, org.junit.jupiter.api.TestExtensionContext context) {
        Allure.step("Завершение теста: " + testInfo.getDisplayName());
    }

    /**
     * Универсальный GET-запрос с логированием в Allure
     */
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

    /**
     * POST-запрос с телом
     */
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

    /**
     * PUT-запрос
     */
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

    /**
     * PATCH-запрос
     */
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

    /**
     * DELETE-запрос
     */
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
