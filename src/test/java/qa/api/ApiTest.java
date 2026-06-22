package qa.api;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import qa.base.BaseApiTest;
import qa.models.Post;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.*;

/**
 * Тесты для REST API JSONPlaceholder — /posts
 * Покрывают CRUD-операции + негативные сценарии
 */
@Epic("API Tests")
@Feature("Posts API")
@DisplayName("Posts CRUD Tests")
public class ApiTest extends BaseApiTest {

    // ======================== GET ========================

    @Test
    @Story("GET /posts — получить все посты")
    @DisplayName("GET /posts — вернуть список всех постов (статус 200)")
    @Severity(SeverityLevel.BLOCKER)
    void getAllPosts_shouldReturn200() {
        var response = getRequest("/posts");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getList("$", Post.class)).isNotEmpty();
        assertThat(response.jsonPath().getList("$").size()).isEqualTo(100);
    }

    @Test
    @Story("GET /posts — получить пост по ID")
    @DisplayName("GET /posts/1 — вернуть конкретный пост")
    @Severity(SeverityLevel.CRITICAL)
    void getPostById_shouldReturnPost() {
        var response = getRequest("/posts/1");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getInt("id")).isEqualTo(1);
        assertThat(response.jsonPath().getInt("userId")).isPositive();
        assertThat(response.jsonPath().getString("title")).isNotBlank();
        assertThat(response.jsonPath().getString("body")).isNotBlank();
    }

    @Test
    @Story("GET /posts — несуществующий пост")
    @DisplayName("GET /posts/99999 — вернуть 404 для несуществующего поста")
    @Severity(SeverityLevel.NORMAL)
    void getPostById_notFound_shouldReturn404() {
        var response = getRequest("/posts/99999");

        assertThat(response.getStatusCode()).isEqualTo(404);
    }

    @Test
    @Story("GET /posts — фильтрация по userId")
    @DisplayName("GET /posts?userId=1 — фильтрация по пользователю")
    @Severity(SeverityLevel.NORMAL)
    void getPostsByUserId_shouldReturnFiltered() {
        var response = given()
                .queryParam("userId", 1)
                .when()
                .get("/posts")
                .then()
                .log().all()
                .extract().response();

        assertThat(response.getStatusCode()).isEqualTo(200);
        List<Integer> userIds = response.jsonPath().getList("userId", Integer.class);
        assertThat(userIds).allMatch(id -> id == 1);
    }

    @Test
    @Story("GET /posts — проверка структуры ответа")
    @DisplayName("GET /posts/1 — все поля имеют корректные типы")
    @Severity(SeverityLevel.NORMAL)
    void getPostById_shouldHaveCorrectTypes() {
        var response = getRequest("/posts/1");

        assertThat(response.jsonPath().get("id")).isInstanceOf(Integer.class);
        assertThat(response.jsonPath().get("userId")).isInstanceOf(Integer.class);
        assertThat(response.jsonPath().get("title")).isInstanceOf(String.class);
        assertThat(response.jsonPath().get("body")).isInstanceOf(String.class);
    }

    // ======================== POST ========================

    @Test
    @Story("POST /posts — создать новый пост")
    @DisplayName("POST /posts — создать пост (статус 201)")
    @Severity(SeverityLevel.BLOCKER)
    void createPost_shouldReturn201() {
        Post newPost = Post.builder()
                .userId(1)
                .title("Тестовый заголовок")
                .body("Тестовое тело поста для автоматизированного теста")
                .build();

        var response = postRequest("/posts", newPost);

        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getInt("id")).isPositive();
        assertThat(response.jsonPath().getString("title")).isEqualTo("Тестовый заголовок");
    }

    @Test
    @Story("POST /posts — создать пост с пустым телом")
    @DisplayName("POST /posts — пустое тело запроса")
    @Severity(SeverityLevel.NORMAL)
    void createPost_emptyBody_shouldHandleGracefully() {
        var response = postRequest("/posts", {});

        // JSONPlaceholder вернёт 201 с null-полями (особенность демо-API)
        assertThat(response.getStatusCode()).isIn(201, 400);
    }

    @Test
    @Story("POST /posts — создать пост с длинным заголовком")
    @DisplayName("POST /posts — заголовок > 1000 символов")
    @Severity(SeverityLevel.NORMAL)
    void createPost_longTitle_shouldReturn201() {
        String longTitle = "A".repeat(1000);
        Post post = Post.builder()
                .userId(1)
                .title(longTitle)
                .body("Тело поста")
                .build();

        var response = postRequest("/posts", post);

        assertThat(response.getStatusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getString("title")).hasSize(1000);
    }

    @Test
    @Story("POST /posts — создать пост с HTML-контентом")
    @DisplayName("POST /posts — XSS в заголовке")
    @Severity(SeverityLevel.NORMAL)
    void createPost_xssInTitle_shouldReturn201() {
        Post post = Post.builder()
                .userId(1)
                .title("<script>alert('xss')</script>")
                .body("Тело")
                .build();

        var response = postRequest("/posts", post);

        assertThat(response.getStatusCode()).isEqualTo(201);
        // Проверяем, что API не сломался
    }

    // ======================== PUT ========================

    @Test
    @Story("PUT /posts — обновить пост")
    @DisplayName("PUT /posts/1 — полностью обновить пост")
    @Severity(SeverityLevel.CRITICAL)
    void updatePost_shouldReturn200() {
        Post updatedPost = Post.builder()
                .userId(1)
                .id(1)
                .title("Обновлённый заголовок")
                .body("Обновлённое тело поста")
                .build();

        var response = putRequest("/posts/1", updatedPost);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("title")).isEqualTo("Обновлённый заголовок");
    }

    // ======================== PATCH ========================

    @Test
    @Story("PATCH /posts — частично обновить пост")
    @DisplayName("PATCH /posts/1 — обновить только заголовок")
    @Severity(SeverityLevel.NORMAL)
    void patchPost_shouldUpdateTitle() {
        var response = patchRequest("/posts/1", "{\"title\": \"Patched title\"}");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getString("title")).isEqualTo("Patched title");
    }

    // ======================== DELETE ========================

    @Test
    @Story("DELETE /posts — удалить пост")
    @DisplayName("DELETE /posts/1 — удалить пост (статус 200)")
    @Severity(SeverityLevel.CRITICAL)
    void deletePost_shouldReturn200() {
        var response = deleteRequest("/posts/1");

        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    // ======================== ПАРАМЕТРИЗОВАННЫЕ ========================

    @Test
    @Story("GET /posts — проверить каждый пост по ID")
    @DisplayName("GET /posts/{id} — параметризованный тест для ID 1-10")
    @Severity(SeverityLevel.NORMAL)
    @ParameterizedTest(name = "Пост с ID = {0}")
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void getPostById_parameterized(int postId) {
        var response = getRequest("/posts/" + postId);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.jsonPath().getInt("id")).isEqualTo(postId);
    }

    // ======================== СОВМЕСТНЫЕ ТЕСТЫ ========================

    @Test
    @Story("POST + GET — создать и найти пост")
    @DisplayName("POST → GET — создать пост, затем найти его по ID")
    @Severity(SeverityLevel.BLOCKER)
    void createAndRetrievePost() {
        // Создаём
        Post newPost = Post.builder()
                .userId(1)
                .title("Для проверки")
                .body("Создан для теста нахождения")
                .build();

        var createResponse = postRequest("/posts", newPost);
        int newId = createResponse.jsonPath().getInt("id");

        // Находим
        var getResponse = getRequest("/posts/" + newId);
        assertThat(getResponse.getStatusCode()).isEqualTo(200);
        assertThat(getResponse.jsonPath().getString("title")).isEqualTo("Для проверки");
    }
}
