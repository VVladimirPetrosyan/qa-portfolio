package qa.api.slushayu;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.given;

/**
 * Базовый класс для контрактных тестов против живого прод-бэкенда «Слушаю»
 * (Amvera). Адрес не хранится в коде — только в переменной окружения
 * SLUSHAYU_BASE_URL (GitHub Actions secret), чтобы не публиковать домен
 * личного сервиса в открытом репозитории. Классы-наследники помечены
 * {@code @EnabledIfEnvironmentVariable}: без переменной тесты пропускаются
 * (не падают), поэтому `mvn test` у любого, кто клонирует репозиторий без
 * секрета, остаётся зелёным.
 */
public class BaseSlushayuApiTest {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = System.getenv("SLUSHAYU_BASE_URL");
        RestAssured.requestSpecification = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().all();
    }
}
