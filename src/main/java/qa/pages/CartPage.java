package qa.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Page Object для корзины (SauceDemo)
 */
public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Локаторы
    private static final By PAGE_TITLE = By.cssSelector(".title");
    private static final By CART_ITEMS = By.cssSelector(".cart_item");
    private static final By CART_ITEM_NAMES = By.cssSelector(".inventory_item_name");
    private static final By CART_ITEM_PRICES = By.cssSelector(".inventory_item_price");
    private static final By REMOVE_BUTTONS = By.cssSelector("button[id^='remove']");
    private static final By CHECKOUT_BUTTON = By.id("checkout");
    private static final By CONTINUE_SHOPPING = By.id("continue-shopping");
    private static final By QUANTITY_LABEL = By.cssSelector(".cart_quantity");

    public CartPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    @Step("Получить заголовок страницы корзины")
    public String getPageTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_TITLE)).getText();
    }

    @Step("Получить количество товаров в корзине")
    public int getItemCount() {
        List<WebElement> items = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(CART_ITEMS));
        return items.size();
    }

    @Step("Получить список названий товаров в корзине")
    public List<String> getProductNames() {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(CART_ITEM_NAMES));
        return driver.findElements(CART_ITEM_NAMES).stream()
                .map(WebElement::getText)
                .toList();
    }

    @Step("Удалить товар из корзины: {productName}")
    public CartPage removeItem(String productName) {
        String id = productName.toLowerCase().replace(" ", "-");
        By button = By.id("remove-" + id);
        wait.until(ExpectedConditions.elementToBeClickable(button)).click();
        return this;
    }

    @Step("Перейти к оформлению заказа")
    public CheckoutPage goToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(CHECKOUT_BUTTON)).click();
        return new CheckoutPage(driver, wait);
    }

    @Step("Вернуться в каталог")
    public CatalogPage continueShopping() {
        wait.until(ExpectedConditions.elementToBeClickable(CONTINUE_SHOPPING)).click();
        return new CatalogPage(driver, wait);
    }

    @Step("Проверить, что корзина пуста")
    public boolean isCartEmpty() {
        try {
            List<WebElement> items = driver.findElements(CART_ITEMS);
            return items.isEmpty();
        } catch (Exception e) {
            return true;
        }
    }

    @Step("Проверить, что страница корзины загружена")
    public boolean isPageLoaded() {
        try {
            String title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_TITLE)).getText();
            return title.equals("Your Cart");
        } catch (Exception e) {
            return false;
        }
    }
}
