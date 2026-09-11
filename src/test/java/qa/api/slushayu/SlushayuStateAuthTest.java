package qa.api.slushayu;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static io.restassured.RestAssured.given;

@Epic("API Tests — Слушаю (прод)")
@Feature("Авторизация /state")
@DisplayName("Слушаю: /state требует Bearer-токен")
@EnabledIfEnvironmentVariable(named = "SLUSHAYU_BASE_URL", matches = ".+")
public class SlushayuStateAuthTest extends BaseSlushayuApiTest {

    @Test
    @Story("Запрос без токена отклоняется")
    @DisplayName("GET /state без заголовка Authorization — 401")
    @Severity(SeverityLevel.BLOCKER)
    void noToken_shouldReturn401() {
        given().when().get("/state").then().statusCode(401);
    }

    @Test
    @Story("Запрос с неверным токеном отклоняется")
    @DisplayName("GET /state с неверным Bearer-токеном — 401")
    @Severity(SeverityLevel.BLOCKER)
    void wrongToken_shouldReturn401() {
        given().header("Authorization", "Bearer qa-portfolio-wrong-token")
                .when().get("/state")
                .then().statusCode(401);
    }
}
