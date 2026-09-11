package qa.api.slushayu;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static io.restassured.RestAssured.given;

/**
 * Контракт вебхука ЮKassa: POST /yookassa-webhook. Все тела запросов —
 * синтетические (несуществующие id платежей), поэтому verify_succeeded()
 * на стороне сервера не может подтвердить платёж через реальный API ЮKassa
 * и выдача PDF не запускается — тесты безопасны для боевых данных.
 */
@Epic("API Tests — Слушаю (прод)")
@Feature("YooKassa webhook contract")
@DisplayName("Слушаю: контракт /yookassa-webhook")
@EnabledIfEnvironmentVariable(named = "SLUSHAYU_BASE_URL", matches = ".+")
public class SlushayuWebhookTest extends BaseSlushayuApiTest {

    @Test
    @Story("Пустое тело запроса не роняет обработчик")
    @DisplayName("POST /yookassa-webhook с пустым телом — 200 'ignored'")
    @Severity(SeverityLevel.CRITICAL)
    void emptyBody_shouldBeIgnoredGracefully() {
        given().body("")
                .when().post("/yookassa-webhook")
                .then().statusCode(200)
                .body(org.hamcrest.Matchers.equalTo("ignored"));
    }

    @Test
    @Story("Невалидный JSON не роняет обработчик")
    @DisplayName("POST /yookassa-webhook с битым JSON — 200 'ignored', не 500")
    @Severity(SeverityLevel.CRITICAL)
    void malformedJson_shouldBeIgnoredNotCrash() {
        given().body("{not-valid-json")
                .when().post("/yookassa-webhook")
                .then().statusCode(200)
                .body(org.hamcrest.Matchers.equalTo("ignored"));
    }

    @Test
    @Story("Неизвестный тип события игнорируется без попытки верификации")
    @DisplayName("POST /yookassa-webhook с неизвестным event — 200 'ignored'")
    @Severity(SeverityLevel.NORMAL)
    void unknownEventType_shouldBeIgnored() {
        String unknownEventPayload = """
                {"event":"qa.portfolio.synthetic.unknown_event","object":{"id":"qa-portfolio-synthetic-id"}}
                """;

        given().body(unknownEventPayload)
                .when().post("/yookassa-webhook")
                .then().statusCode(200)
                .body(org.hamcrest.Matchers.equalTo("ignored"));
    }

    @Test
    @Story("BUG-008: payment.succeeded с несуществующим id не отличается от сетевого сбоя — сервер просит повтор")
    @DisplayName("POST /yookassa-webhook с payment.succeeded и фейковым id — 500 'retry', не 200/400")
    @Severity(SeverityLevel.NORMAL)
    void forgedSucceededEvent_triggersRetryInsteadOfRejection() {
        String forgedPayload = """
                {"event":"payment.succeeded","object":{"id":"qa-portfolio-synthetic-00000000-0000-0000-0000-000000000000"}}
                """;

        var response = given().body(forgedPayload).when().post("/yookassa-webhook").thenReturn();

        org.assertj.core.api.Assertions.assertThat(response.getStatusCode())
                .as("verify_succeeded() не различает 'платежа не существует' и 'сеть/ЮKassa недоступны' — оба "
                        + "случая дают 500+retry, а не 4xx (BUG-008). На практике это значит: POST с любым "
                        + "несуществующим id заставит ЮKassa бесконечно ретраить вебхук по расписанию их стороны.")
                .isEqualTo(500);
        org.assertj.core.api.Assertions.assertThat(response.asString()).isEqualTo("retry");
    }
}
