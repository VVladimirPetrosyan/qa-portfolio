package qa.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import qa.base.BaseUiTest;
import qa.pages.LoginPage;
import qa.pages.CatalogPage;

import static org.assertj.core.api.Assertions.*;

/**
 * UI-тесты для страницы логина (SauceDemo)
 * Позитивные и негативные сценарии
 */
@Epic("UI Tests")
@Feature("Login")
@DisplayName("Login Tests")
public class LoginTest extends BaseUiTest {

    private LoginPage loginPage;

    @BeforeEach
    void initPages() {
        loginPage = new LoginPage(driver, wait);
    }

    // ======================== ПОЗИТИВНЫЕ ========================

    @Test
    @Story("Успешный логин стандартного пользователя")
    @DisplayName("Логин: standard_user → перенаправление на каталог")
    @Severity(SeverityLevel.BLOCKER)
    void login_standardUser_shouldRedirectToCatalog() {
        loginPage.loginAs("standard_user", "secret_sauce");

        CatalogPage catalog = new CatalogPage(driver, wait);
        assertThat(catalog.isPageLoaded()).isTrue();
        assertThat(catalog.getPageTitle()).isEqualTo("Products");
    }

    @Test
    @Story("Логин problems_user")
    @DisplayName("Логин: problems_user → успешный вход")
    @Severity(SeverityLevel.CRITICAL)
    void login_problemsUser_shouldLogin() {
        loginPage.loginAs("problems_user", "secret_sauce");

        CatalogPage catalog = new CatalogPage(driver, wait);
        assertThat(catalog.isPageLoaded()).isTrue();
    }

    @Test
    @Story("Логин performance_glitch_user")
    @DisplayName("Логин: performance_glitch_user → медленный вход")
    @Severity(SeverityLevel.NORMAL)
    void login_performanceGlitchUser_shouldLoginWithDelay() {
        long start = System.currentTimeMillis();
        loginPage.loginAs("performance_glitch_user", "secret_sauce");
        long duration = System.currentTimeMillis() - start;

        CatalogPage catalog = new CatalogPage(driver, wait);
        assertThat(catalog.isPageLoaded()).isTrue();
        // Пользователь с задержкой — логин занимает больше времени
        assertThat(duration).isGreaterThan(0);
    }

    // ======================== НЕГАТИВНЫЕ ========================

    @Test
    @Story("Неверный пароль")
    @DisplayName("Логин: standard_user + неверный пароль → ошибка")
    @Severity(SeverityLevel.CRITICAL)
    void login_wrongPassword_shouldShowError() {
        loginPage.loginAs("standard_user", "wrong_password");

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Username and password do not match");
    }

    @Test
    @Story("Неверный логин")
    @DisplayName("Логин: неверный логин → ошибка")
    @Severity(SeverityLevel.CRITICAL)
    void login_wrongUsername_shouldShowError() {
        loginPage.loginAs("invalid_user", "secret_sauce");

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Username and password do not match");
    }

    @Test
    @Story("Пустой логин")
    @DisplayName("Логин: пустой логин → ошибка")
    @Severity(SeverityLevel.NORMAL)
    void login_emptyUsername_shouldShowError() {
        loginPage.open();
        loginPage.enterPassword("secret_sauce");
        loginPage.clickLogin();

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Username is required");
    }

    @Test
    @Story("Пустой пароль")
    @DisplayName("Логин: пустой пароль → ошибка")
    @Severity(SeverityLevel.NORMAL)
    void login_emptyPassword_shouldShowError() {
        loginPage.open();
        loginPage.enterUsername("standard_user");
        loginPage.clickLogin();

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Password is required");
    }

    @Test
    @Story("Пустые оба поля")
    @DisplayName("Логин: оба поля пустые → ошибка")
    @Severity(SeverityLevel.NORMAL)
    void login_emptyFields_shouldShowError() {
        loginPage.open();
        loginPage.clickLogin();

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Username is required");
    }

    @Test
    @Story("Логин заблокированного пользователя")
    @DisplayName("Логин: locked_out_user → сообщение о блокировке")
    @Severity(SeverityLevel.CRITICAL)
    void login_lockedUser_shouldShowLockedMessage() {
        loginPage.loginAs("locked_out_user", "secret_sauce");

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Sorry, this user has been locked out");
    }

    // ======================== UI-ПРОВЕРКИ ========================

    @Test
    @Story("Проверка элементов страницы логина")
    @DisplayName("Страница логина: все элементы отображаются")
    @Severity(SeverityLevel.NORMAL)
    void loginPage_shouldDisplayAllElements() {
        loginPage.open();

        assertThat(loginPage.isPageLoaded()).isTrue();
        assertThat(driver.findElement(org.openqa.selenium.By.id("user-name"))).isDisplayed();
        assertThat(driver.findElement(org.openqa.selenium.By.id("password"))).isDisplayed();
        assertThat(driver.findElement(org.openqa.selenium.By.id("login-button"))).isDisplayed();
    }

    @Test
    @Story("Кнопка логина кликабельна")
    @DisplayName("Страница логина: кнопка Login кликабельна")
    @Severity(SeverityLevel.MINOR)
    void loginPage_loginButtonIsClickable() {
        loginPage.open();

        var button = driver.findElement(org.openqa.selenium.By.id("login-button"));
        assertThat(button.isEnabled()).isTrue();
    }
}
