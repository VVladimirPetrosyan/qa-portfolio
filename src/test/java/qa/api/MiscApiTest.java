package qa.api;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import qa.base.BaseApiTest;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

@Epic("API Tests")
@Feature("Miscellaneous API")
@DisplayName("Comments, Albums, Todos Tests")
public class MiscApiTest extends BaseApiTest {

    @Test
    @Story("GET /comments — получить все комментарии")
    @DisplayName("GET /comments — вернуть 500 комментариев")
    @Severity(SeverityLevel.CRITICAL)
    void getAllComments_shouldReturn500() {
        var response = getRequest("/comments");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$").size()).isEqualTo(500);
    }

    @Test
    @Story("GET /comments — фильтрация по postId")
    @DisplayName("GET /comments?postId=1 — комментарии к посту 1")
    @Severity(SeverityLevel.NORMAL)
    void getCommentsByPostId_shouldFilter() {
        var response = given()
                .queryParam("postId", 1)
                .when()
                .get("/comments")
                .then()
                .log().all()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);
        List<Integer> postIds = response.jsonPath().getList("postId", Integer.class);
        assertThat(postIds).allMatch(id -> id == 1);
    }

    @Test
    @Story("GET /comments — проверить email в комментариях")
    @DisplayName("GET /comments — email содержит @")
    @Severity(SeverityLevel.NORMAL)
    void getComments_emailsShouldBeValid() {
        var response = getRequest("/comments");

        List<String> emails = response.jsonPath().getList("email", String.class);
        assertThat(emails).isNotEmpty();
        assertThat(emails).allMatch(e -> e.contains("@"));
    }

    @Test
    @Story("GET /albums — получить все альбомы")
    @DisplayName("GET /albums — вернуть 100 альбомов")
    @Severity(SeverityLevel.CRITICAL)
    void getAllAlbums_shouldReturn100() {
        var response = getRequest("/albums");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$").size()).isEqualTo(100);
    }

    @Test
    @Story("GET /albums — фильтрация по userId")
    @DisplayName("GET /albums?userId=1 — альбомы пользователя 1")
    @Severity(SeverityLevel.NORMAL)
    void getAlbumsByUserId() {
        var response = given()
                .queryParam("userId", 1)
                .when()
                .get("/albums")
                .then()
                .log().all()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);
        List<Integer> userIds = response.jsonPath().getList("userId", Integer.class);
        assertThat(userIds).allMatch(id -> id == 1);
    }

    @Test
    @Story("GET /todos — получить все задачи")
    @DisplayName("GET /todos — вернуть 200 задач")
    @Severity(SeverityLevel.CRITICAL)
    void getAllTodos_shouldReturn200() {
        var response = getRequest("/todos");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$").size()).isEqualTo(200);
    }

    @Test
    @Story("GET /todos — фильтрация по completed")
    @DisplayName("GET /todos?completed=true — только выполненные")
    @Severity(SeverityLevel.NORMAL)
    void getCompletedTodos_shouldFilter() {
        var response = given()
                .queryParam("completed", true)
                .when()
                .get("/todos")
                .then()
                .log().all()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);
        List<Boolean> completed = response.jsonPath().getList("completed", Boolean.class);
        assertThat(completed).allMatch(b -> b);
    }

    @Test
    @Story("GET /todos — фильтрация по userId + completed")
    @DisplayName("GET /todos?userId=1&completed=false — невыполненные пользователя 1")
    @Severity(SeverityLevel.NORMAL)
    void getTodosByUserAndStatus() {
        var response = given()
                .queryParam("userId", 1)
                .queryParam("completed", false)
                .when()
                .get("/todos")
                .then()
                .log().all()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);
        List<Integer> userIds = response.jsonPath().getList("userId", Integer.class);
        List<Boolean> completed = response.jsonPath().getList("completed", Boolean.class);
        assertThat(userIds).allMatch(id -> id == 1);
        assertThat(completed).allMatch(b -> !b);
    }

    @Test
    @Story("GET /posts — проверка времени ответа")
    @DisplayName("GET /posts — ответ быстрее 2000мс")
    @Severity(SeverityLevel.CRITICAL)
    void getPosts_responseTimeShouldBeFast() {
        long startTime = System.currentTimeMillis();
        var response = getRequest("/posts");
        long duration = System.currentTimeMillis() - startTime;

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(duration).isLessThan(2000);
    }

    @Test
    @Story("GET /posts — проверка Content-Type")
    @DisplayName("GET /posts — Content-Type application/json")
    @Severity(SeverityLevel.NORMAL)
    void getPosts_contentTypeShouldBeJson() {
        var response = getRequest("/posts");

        assertThat(response.contentType()).contains("application/json");
    }
}
