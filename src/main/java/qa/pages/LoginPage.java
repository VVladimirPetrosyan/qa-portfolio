package qa.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object для страницы логина (SauceDemo)
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Локаторы
    private static final By USERNAME_INPUT = By.id("user-name");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");
    private static final By ERROR_BUTTON = By.cssSelector(".error-button");
    private static final By HEADER_LOGO = By.cssSelector(".login_logo");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    @Step("Открыть страницу логина")
    public LoginPage open() {
        driver.get("https://www.saucedemo.com/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME_INPUT));
        return this;
    }

    @Step("Ввести логин: {username}")
    public LoginPage enterUsername(String username) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(USERNAME_INPUT));
        field.clear();
        field.sendKeys(username);
        return this;
    }

    @Step("Ввести пароль")
    public LoginPage enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.elementToBeClickable(PASSWORD_INPUT));
        field.clear();
        field.sendKeys(password);
        return this;
    }

    @Step("Нажать кнопку логина")
    public LoginPage clickLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON)).click();
        return this;
    }

    @Step("Полный логин: {username}")
    public LoginPage loginAs(String username, String password) {
        open();
        enterUsername(username);
        enterPassword(password);
        clickLogin();
        return this;
    }

    @Step("Получить текст ошибки")
    public String getErrorMessage() {
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
        return error.getText();
    }

    @Step("Проверить, что ошибка отображается")
    public boolean isErrorDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Закрыть ошибку")
    public LoginPage dismissError() {
        wait.until(ExpectedConditions.elementToBeClickable(ERROR_BUTTON)).click();
        return this;
    }

    @Step("Проверить, что страница логина загружена")
    public boolean isPageLoaded() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(HEADER_LOGO)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
