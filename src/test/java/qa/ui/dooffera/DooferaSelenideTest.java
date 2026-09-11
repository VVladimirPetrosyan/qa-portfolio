package qa.ui.dooffera;

import com.codeborne.selenide.Configuration;
import io.qameta.allure.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static com.codeborne.selenide.Selenide.open;

/**
 * Те же живые https://do-offera.github.io, что и в DooferaResumeBuilderTest,
 * но через Selenide вместо чистого Selenium WebDriver — компактный API
 * поверх того же драйвера, паттерн доступа к элементам через $() вместо
 * driver.findElement(By...).
 */
@Epic("UI Tests — до оффера. (прод)")
@Feature("Selenide")
@DisplayName("до оффера.: та же прод-цель через Selenide")
public class DooferaSelenideTest {

    @BeforeAll
    static void configureSelenide() {
        Configuration.baseUrl = "https://do-offera.github.io";
        Configuration.headless = true;
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;
    }

    @AfterAll
    static void tearDown() {
        closeWebDriver();
    }

    @BeforeEach
    void openPageAndDismissCookieBar() {
        open("/");
        if ($("#cookieBar").isDisplayed()) {
            $("#cookieOk").click();
        }
    }

    @Test
    @Story("Заполнение имени обновляет живой предпросмотр")
    @DisplayName("$(\"#f-name\").setValue(...) -> #sheet содержит введённое имя")
    @Severity(SeverityLevel.CRITICAL)
    void fillingNameField_updatesLivePreview() {
        $("#f-name").setValue("Тестовое Имя Selenide");
        $("#sheet").shouldHave(text("Тестовое Имя Selenide"));
    }

    @Test
    @Story("Ссылка «Заказать разбор» ведёт на настоящий контакт")
    @DisplayName("#expertBtn href == https://vk.me/dooffera")
    @Severity(SeverityLevel.NORMAL)
    void expertLink_pointsToRealContact() {
        $("#expertBtn").shouldHave(attribute("href", "https://vk.me/dooffera"));
    }

    @Test
    @Story("Добавление навыка через Enter")
    @DisplayName("skillInput.setValue(...).pressEnter() -> тег в skillTags")
    @Severity(SeverityLevel.NORMAL)
    void addingSkill_appendsTagOnEnter() {
        $("#skillInput").setValue("Selenide").pressEnter();
        $("#skillTags").shouldHave(text("Selenide"));
    }
}
