package qa.api;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import qa.base.BaseApiTest;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@Epic("API Tests")
@Feature("Users API")
@DisplayName("Users API Tests")
public class UsersApiTest extends BaseApiTest {

    @Test
    @Story("GET /users — получить всех пользователей")
    @DisplayName("GET /users — вернуть список из 10 пользователей")
    @Severity(SeverityLevel.BLOCKER)
    void getAllUsers_shouldReturn10() {
        var response = getRequest("/users");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$").size()).isEqualTo(10);
    }

    @Test
    @Story("GET /users — проверить структуру")
    @DisplayName("GET /users/1 — все поля заполнены")
    @Severity(SeverityLevel.CRITICAL)
    void getUserById_shouldHaveAllFields() {
        var response = getRequest("/users/1");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("name")).isNotBlank();
        assertThat(response.jsonPath().getString("email")).contains("@");
        assertThat(response.jsonPath().getString("phone")).isNotBlank();
        assertThat(response.jsonPath().getString("website")).isNotBlank();
        assertThat(response.jsonPath().getString("address.city")).isNotBlank();
        assertThat(response.jsonPath().getString("address.street")).isNotBlank();
        assertThat(response.jsonPath().getString("company.name")).isNotBlank();
    }

    @Test
    @Story("GET /users — фильтрация по имени")
    @DisplayName("GET /users?name=Leanne — поиск по имени")
    @Severity(SeverityLevel.NORMAL)
    void getUsersByName_shouldFilter() {
        var response = given()
                .queryParam("name", "Leanne Graham")
                .when()
                .get("/users")
                .then()
                .log().all()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);
        List<String> names = response.jsonPath().getList("name", String.class);
        assertThat(names).contains("Leanne Graham");
    }

    @Test
    @Story("GET /users — все email содержат @")
    @DisplayName("GET /users — валидация формата email")
    @Severity(SeverityLevel.NORMAL)
    void getAllUsers_emailsShouldBeValid() {
        var response = getRequest("/users");

        List<String> emails = response.jsonPath().getList("email", String.class);
        assertThat(emails).allMatch(email -> email.contains("@") && email.contains("."));
    }

    @Test
    @Story("GET /users — все website содержат точку")
    @DisplayName("GET /users — website валидный домен")
    @Severity(SeverityLevel.MINOR)
    void getAllUsers_websitesShouldBeValid() {
        var response = getRequest("/users");

        List<String> websites = response.jsonPath().getList("website", String.class);
        assertThat(websites).allMatch(site -> site.contains("."));
    }

    @Test
    @Story("GET /users — проверить количество пользователей")
    @DisplayName("GET /users — ровно 10 пользователей")
    @Severity(SeverityLevel.CRITICAL)
    void getAllUsers_exactly10Users() {
        var response = getRequest("/users");

        assertThat(response.jsonPath().getList("$").size()).isEqualTo(10);
    }

    @Test
    @Story("GET /users — уникальные ID")
    @DisplayName("GET /users — все ID уникальны")
    @Severity(SeverityLevel.NORMAL)
    void getAllUsers_uniqueIds() {
        var response = getRequest("/users");

        List<Integer> ids = response.jsonPath().getList("id", Integer.class);
        assertThat(ids).doesNotHaveDuplicates();
    }

    @Test
    @Story("GET /users — негативный сценарий")
    @DisplayName("GET /users/99999 — несуществующий пользователь")
    @Severity(SeverityLevel.NORMAL)
    void getUser_notFound() {
        var response = getRequest("/users/99999");

        assertThat(response.getStatusCode()).isEqualTo(404);
    }
}
