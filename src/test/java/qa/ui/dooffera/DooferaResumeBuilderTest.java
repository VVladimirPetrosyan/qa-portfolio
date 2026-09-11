package qa.ui.dooffera;

import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("UI Tests — до оффера. (прод)")
@Feature("Конструктор резюме")
@DisplayName("до оффера.: заполнение и предпросмотр резюме")
public class DooferaResumeBuilderTest extends BaseDooferaUiTest {

    @BeforeEach
    void openEditor() {
        openPage("/");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("f-name")));
        acceptCookieBarIfPresent();
    }

    private void acceptCookieBarIfPresent() {
        var bar = driver.findElement(By.id("cookieBar"));
        if (bar.isDisplayed()) {
            driver.findElement(By.id("cookieOk")).click();
        }
    }

    @Test
    @Story("Пустой конструктор стартует с нулевой готовности")
    @DisplayName("Свежая сессия — readyPct = 0%")
    @Severity(SeverityLevel.NORMAL)
    void freshSession_readinessShouldBeZero() {
        String pct = driver.findElement(By.id("readyPct")).getText();
        assertThat(pct).isEqualTo("0%");
    }

    @Test
    @Story("Заполнение имени и должности поднимает готовность и обновляет превью")
    @DisplayName("Ввод имени/должности/города — readyPct растёт, превью показывает имя")
    @Severity(SeverityLevel.BLOCKER)
    void fillingPersonalFields_shouldUpdateProgressAndPreview() {
        driver.findElement(By.id("f-name")).sendKeys("Тестовое Имя QA");
        driver.findElement(By.id("f-title")).sendKeys("QA Engineer");
        driver.findElement(By.id("f-city")).sendKeys("Ижевск");

        wait.until(d -> !driver.findElement(By.id("readyPct")).getText().equals("0%"));

        String pct = driver.findElement(By.id("readyPct")).getText();
        assertThat(pct).as("процент готовности должен вырасти после заполнения полей").isNotEqualTo("0%");

        WebElement sheet = driver.findElement(By.id("sheet"));
        assertThat(sheet.getText()).contains("Тестовое Имя QA");
    }

    @Test
    @Story("Кнопка «Показать пример» заполняет форму на пустом черновике без диалога подтверждения")
    @DisplayName("fillExample на пустой форме — данные подставляются сразу, без confirm()")
    @Severity(SeverityLevel.NORMAL)
    void fillExample_onEmptyDraft_shouldFillWithoutConfirm() {
        driver.findElement(By.id("fillExample")).click();

        wait.until(d -> !driver.findElement(By.id("f-name")).getAttribute("value").isEmpty());

        String name = driver.findElement(By.id("f-name")).getAttribute("value");
        assertThat(name).isNotBlank();
    }

    @Test
    @Story("Добавление навыка через Enter")
    @DisplayName("Ввод навыка в skillInput + Enter — тег появляется в skillTags")
    @Severity(SeverityLevel.NORMAL)
    void addingSkill_shouldAppendTag() {
        WebElement skillInput = driver.findElement(By.id("skillInput"));
        skillInput.sendKeys("Selenium" + org.openqa.selenium.Keys.ENTER);

        wait.until(d -> !driver.findElement(By.id("skillTags")).getText().isEmpty());

        assertThat(driver.findElement(By.id("skillTags")).getText()).contains("Selenium");
    }

    @Test
    @Story("Ссылка «Заказать разбор» ведёт на настоящий контакт, а не заглушку")
    @DisplayName("expertBtn href == https://vk.me/dooffera")
    @Severity(SeverityLevel.CRITICAL)
    void expertLink_shouldPointToRealContact() {
        String href = driver.findElement(By.id("expertBtn")).getAttribute("href");
        assertThat(href).isEqualTo("https://vk.me/dooffera");
    }

    @Test
    @Story("Очистка формы требует подтверждения и правда очищает поля")
    @DisplayName("resetAll — confirm(), после Accept все поля пустые и readyPct = 0%")
    @Severity(SeverityLevel.NORMAL)
    void resetAll_afterConfirm_shouldClearAllFields() {
        driver.findElement(By.id("f-name")).sendKeys("Будет стёрто");
        wait.until(d -> !driver.findElement(By.id("readyPct")).getText().equals("0%"));

        driver.findElement(By.id("resetAll")).click();
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        wait.until(d -> driver.findElement(By.id("readyPct")).getText().equals("0%"));

        assertThat(driver.findElement(By.id("f-name")).getAttribute("value")).isEmpty();
        assertThat(driver.findElement(By.id("readyPct")).getText()).isEqualTo("0%");
    }
}
