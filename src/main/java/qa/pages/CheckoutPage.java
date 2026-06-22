package qa.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object для оформления заказа (SauceDemo)
 */
public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Локаторы
    private static final By PAGE_TITLE = By.cssSelector(".title");
    private static final By FIRST_NAME_INPUT = By.id("first-name");
    private static final By LAST_NAME_INPUT = By.id("last-name");
    private static final By POSTAL_CODE_INPUT = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");
    private static final By FINISH_BUTTON = By.id("finish");
    private static final By CANCEL_BUTTON = By.id("cancel");
    private static final By BACK_HOME_BUTTON = By.id("back-to-products");
    private static final By COMPLETE_HEADER = By.cssSelector(".complete-header");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");
    private static final By ITEM_TOTAL = By.cssSelector(".summary_subtotal_label");
    private static final By TAX = By.cssSelector(".summary_tax_label");
    private static final By TOTAL = By.cssSelector(".summary_total_label");

    public CheckoutPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    @Step("Заполнить данные покупателя: {firstName} {lastName}, {postalCode}")
    public CheckoutPage fillCheckoutInfo(String firstName, String lastName, String postalCode) {
        wait.until(ExpectedConditions.elementToBeClickable(FIRST_NAME_INPUT)).sendKeys(firstName);
        driver.findElement(LAST_NAME_INPUT).sendKeys(lastName);
        driver.findElement(POSTAL_CODE_INPUT).sendKeys(postalCode);
        return this;
    }

    @Step("Нажать 'Continue'")
    public CheckoutPage clickContinue() {
        wait.until(ExpectedConditions.elementToBeClickable(CONTINUE_BUTTON)).click();
        return this;
    }

    @Step("Нажать 'Finish'")
    public CheckoutPage clickFinish() {
        wait.until(ExpectedConditions.elementToBeClickable(FINISH_BUTTON)).click();
        return this;
    }

    @Step("Нажать 'Cancel'")
    public CartPage clickCancel() {
        wait.until(ExpectedConditions.elementToBeClickable(CANCEL_BUTTON)).click();
        return new CartPage(driver, wait);
    }

    @Step("Получить сообщение об ошибке")
    public String getErrorMessage() {
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
        return error.getText();
    }

    @Step("Проверить, что заказ завершён")
    public boolean isOrderComplete() {
        try {
            String header = wait.until(ExpectedConditions.visibilityOfElementLocated(COMPLETE_HEADER)).getText();
            return header.contains("Thank you") || header.contains("COMPLETE");
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Получить итоговую сумму")
    public String getTotal() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(TOTAL)).getText();
    }

    @Step("Получить сумму товаров")
    public String getItemTotal() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ITEM_TOTAL)).getText();
    }

    @Step("Получить налог")
    public String getTax() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(TAX)).getText();
    }

    @Step("Вернуться на главную")
    public CatalogPage goBackHome() {
        wait.until(ExpectedConditions.elementToBeClickable(BACK_HOME_BUTTON)).click();
        return new CatalogPage(driver, wait);
    }

    @Step("Проверить, что страница Checkout загружена")
    public boolean isPageLoaded() {
        try {
            String title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_TITLE)).getText();
            return title.contains("Checkout");
        } catch (Exception e) {
            return false;
        }
    }
}
