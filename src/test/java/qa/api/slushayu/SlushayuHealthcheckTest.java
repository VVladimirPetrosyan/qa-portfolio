package qa.api.slushayu;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("API Tests — Слушаю (прод)")
@Feature("Healthcheck")
@DisplayName("Слушаю: healthcheck и роутинг")
@EnabledIfEnvironmentVariable(named = "SLUSHAYU_BASE_URL", matches = ".+")
public class SlushayuHealthcheckTest extends BaseSlushayuApiTest {

    @Test
    @Story("GET / — сервис живой")
    @DisplayName("GET / — 200")
    @Severity(SeverityLevel.CRITICAL)
    void root_shouldReturn200() {
        given().when().get("/").then().statusCode(200);
    }

    @Test
    @Story("BUG-007: GET на несуществующий путь тоже отдаёт 200")
    @DisplayName("GET /несуществующий-путь — фактически 200 вместо ожидаемых 404 (баг зафиксирован в bug-reports.md)")
    @Severity(SeverityLevel.NORMAL)
    void getUnknownPath_returns200InsteadOf404() {
        var response = given().when().get("/qa-portfolio-check-unknown-path").thenReturn();

        assertThat(response.getStatusCode())
                .as("healthcheck не проверяет путь запроса — GET-обработчик отвечает 200 на любой путь (BUG-007)")
                .isEqualTo(200);
    }

    @Test
    @Story("Контраст: POST на тот же несуществующий путь роутится корректно")
    @DisplayName("POST /несуществующий-путь — 404 (в отличие от GET, доказывает, что баг именно в GET-обработчике)")
    @Severity(SeverityLevel.NORMAL)
    void postUnknownPath_shouldReturn404() {
        given().when().post("/qa-portfolio-check-unknown-path").then().statusCode(404);
    }
}
