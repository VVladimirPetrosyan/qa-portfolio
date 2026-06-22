package qa.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import qa.base.BaseUiTest;
import qa.config.TestConfig;
import qa.pages.LoginPage;
import qa.pages.CatalogPage;

import static org.assertj.core.api.Assertions.*;

@Epic("UI Tests")
@Feature("Login")
@DisplayName("Login Tests")
public class LoginTest extends BaseUiTest {

    private LoginPage loginPage;

    @BeforeEach
    void initPages() {
        loginPage = new LoginPage(driver, wait);
    }

    @Test
    @Story("Успешный логин стандартного пользователя")
    @DisplayName("Логин: standard_user -> перенаправление на каталог")
    @Severity(SeverityLevel.BLOCKER)
    void login_standardUser_shouldRedirectToCatalog() {
        loginPage.loginAs(TestConfig.STANDARD_USER, TestConfig.PASSWORD);

        CatalogPage catalog = new CatalogPage(driver, wait);
        assertThat(catalog.isPageLoaded()).isTrue();
        assertThat(catalog.getPageTitle()).isEqualTo("Products");
    }

    @Test
    @Story("Логин problems_user")
    @DisplayName("Логин: problems_user -> успешный вход")
    @Severity(SeverityLevel.CRITICAL)
    void login_problemsUser_shouldLogin() {
        loginPage.loginAs(TestConfig.PROBLEM_USER, TestConfig.PASSWORD);

        CatalogPage catalog = new CatalogPage(driver, wait);
        assertThat(catalog.isPageLoaded()).isTrue();
    }

    @Test
    @Story("Логин performance_glitch_user")
    @DisplayName("Логин: performance_glitch_user -> медленный вход")
    @Severity(SeverityLevel.NORMAL)
    void login_performanceGlitchUser_shouldLoginWithDelay() {
        loginPage.loginAs(TestConfig.GLITCH_USER, TestConfig.PASSWORD);

        CatalogPage catalog = new CatalogPage(driver, wait);
        assertThat(catalog.isPageLoaded()).isTrue();
    }

    @Test
    @Story("Неверный пароль")
    @DisplayName("Логин: standard_user + неверный пароль -> ошибка")
    @Severity(SeverityLevel.CRITICAL)
    void login_wrongPassword_shouldShowError() {
        loginPage.loginAs(TestConfig.STANDARD_USER, TestConfig.WRONG_PASSWORD);

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Username and password do not match");
    }

    @Test
    @Story("Неверный логин")
    @DisplayName("Логин: неверный логин -> ошибка")
    @Severity(SeverityLevel.CRITICAL)
    void login_wrongUsername_shouldShowError() {
        loginPage.loginAs("invalid_user", TestConfig.PASSWORD);

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Username and password do not match");
    }

    @Test
    @Story("Пустой логин")
    @DisplayName("Логин: пустой логин -> ошибка")
    @Severity(SeverityLevel.NORMAL)
    void login_emptyUsername_shouldShowError() {
        loginPage.open();
        loginPage.enterPassword(TestConfig.PASSWORD);
        loginPage.clickLogin();

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Username is required");
    }

    @Test
    @Story("Пустой пароль")
    @DisplayName("Логин: пустой пароль -> ошибка")
    @Severity(SeverityLevel.NORMAL)
    void login_emptyPassword_shouldShowError() {
        loginPage.open();
        loginPage.enterUsername(TestConfig.STANDARD_USER);
        loginPage.clickLogin();

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Password is required");
    }

    @Test
    @Story("Пустые оба поля")
    @DisplayName("Логин: оба поля пустые -> ошибка")
    @Severity(SeverityLevel.NORMAL)
    void login_emptyFields_shouldShowError() {
        loginPage.open();
        loginPage.clickLogin();

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Username is required");
    }

    @Test
    @Story("Логин заблокированного пользователя")
    @DisplayName("Логин: locked_out_user -> сообщение о блокировке")
    @Severity(SeverityLevel.CRITICAL)
    void login_lockedUser_shouldShowLockedMessage() {
        loginPage.loginAs(TestConfig.LOCKED_USER, TestConfig.PASSWORD);

        assertThat(loginPage.isErrorDisplayed()).isTrue();
        assertThat(loginPage.getErrorMessage()).contains("Sorry, this user has been locked out");
    }

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
