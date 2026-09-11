package qa.ui.dooffera;

import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("UI Tests — до оффера. (прод)")
@Feature("Cookie-уведомление")
@DisplayName("до оффера.: cookie-баннер и localStorage")
public class DooferaConsentTest extends BaseDooferaUiTest {

    @Test
    @Story("Свежий визит показывает баннер")
    @DisplayName("Первая загрузка без localStorage-флага — cookieBar виден")
    @Severity(SeverityLevel.NORMAL)
    void freshSession_cookieBarShouldBeVisible() {
        openPage("/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cookieBar")));

        assertThat(driver.findElement(By.id("cookieBar")).isDisplayed()).isTrue();
    }

    @Test
    @Story("Клик «Понятно» скрывает баннер и запоминает согласие")
    @DisplayName("После клика cookieOk — баннер скрыт и localStorage хранит cookie_ack_v1")
    @Severity(SeverityLevel.CRITICAL)
    void clickingOk_shouldHideBarAndPersistAcknowledgement() {
        openPage("/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cookieBar")));

        driver.findElement(By.id("cookieOk")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("cookieBar")));

        String stored = (String) ((JavascriptExecutor) driver)
                .executeScript("return localStorage.getItem('cookie_ack_v1');");
        assertThat(stored).isEqualTo("1");
    }

    @Test
    @Story("Повторный визит с сохранённым согласием не показывает баннер снова")
    @DisplayName("Reload после cookie_ack_v1 в localStorage — cookieBar не появляется")
    @Severity(SeverityLevel.NORMAL)
    void repeatVisit_withStoredAcknowledgement_shouldNotShowBar() {
        openPage("/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cookieBar")));
        driver.findElement(By.id("cookieOk")).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("cookieBar")));

        driver.navigate().refresh();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("f-name")));

        assertThat(driver.findElement(By.id("cookieBar")).isDisplayed()).isFalse();
    }
}
