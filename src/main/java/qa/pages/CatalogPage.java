package qa.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Page Object для каталога товаров (SauceDemo)
 */
public class CatalogPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Локаторы
    private static final By PAGE_TITLE = By.cssSelector(".title");
    private static final By INVENTORY_ITEMS = By.cssSelector(".inventory_item");
    private static final By INVENTORY_NAMES = By.cssSelector(".inventory_item_name");
    private static final By INVENTORY_PRICES = By.cssSelector(".inventory_item_price");
    private static final By ADD_TO_CART_BUTTONS = By.cssSelector("button[id^='add-to-cart']");
    private static final By REMOVE_BUTTONS = By.cssSelector("button[id^='remove']");
    private static final By SHOPPING_CART_BADGE = By.cssSelector(".shopping_cart_badge");
    private static final By SHOPPING_CART_LINK = By.cssSelector(".shopping_cart_link");
    private static final By SORT_DROPDOWN = By.cssSelector("[data-test='product_sort_container']");
    private static final By BURGER_MENU = By.cssSelector(".bm-burger-button");
    private static final By LOGOUT_LINK = By.id("logout_sidebar_link");
    private static final By SIDEBAR = By.cssSelector(".bm-menu");
    private static final By SIDEBAR_CLOSE = By.cssSelector(".bm-cross-button");

    public CatalogPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    @Step("Получить заголовок страницы")
    public String getPageTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_TITLE)).getText();
    }

    @Step("Получить количество товаров")
    public int getItemCount() {
        List<WebElement> items = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(INVENTORY_ITEMS));
        return items.size();
    }

    @Step("Получить список названий товаров")
    public List<String> getProductNames() {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(INVENTORY_NAMES));
        return driver.findElements(INVENTORY_NAMES).stream()
                .map(WebElement::getText)
                .toList();
    }

    @Step("Получить список цен товаров")
    public List<String> getProductPrices() {
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(INVENTORY_PRICES));
        return driver.findElements(INVENTORY_PRICES).stream()
                .map(WebElement::getText)
                .toList();
    }

    @Step("Добавить товар в корзину: {productName}")
    public CatalogPage addToCart(String productName) {
        String id = productName.toLowerCase().replace(" ", "-");
        By button = By.id("add-to-cart-" + id);
        wait.until(ExpectedConditions.elementToBeClickable(button)).click();
        return this;
    }

    @Step("Добавить первый товар в корзину")
    public CatalogPage addFirstProductToCart() {
        List<WebElement> buttons = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(ADD_TO_CART_BUTTONS));
        if (!buttons.isEmpty()) {
            buttons.get(0).click();
        }
        return this;
    }

    @Step("Удалить товар из корзины: {productName}")
    public CatalogPage removeFromCart(String productName) {
        String id = productName.toLowerCase().replace(" ", "-");
        By button = By.id("remove-" + id);
        wait.until(ExpectedConditions.elementToBeClickable(button)).click();
        return this;
    }

    @Step("Получить количество товаров в корзине")
    public int getCartCount() {
        try {
            WebElement badge = wait.until(ExpectedConditions.visibilityOfElementLocated(SHOPPING_CART_BADGE));
            return Integer.parseInt(badge.getText());
        } catch (Exception e) {
            return 0;
        }
    }

    @Step("Перейти в корзину")
    public CartPage goToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(SHOPPING_CART_LINK)).click();
        return new CartPage(driver, wait);
    }

    @Step("Сортировать товары: {option}")
    public CatalogPage sortBy(String option) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(SORT_DROPDOWN));
        dropdown.sendKeys(option);
        return this;
    }

    @Step("Открыть бургер-меню")
    public CatalogPage openMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(BURGER_MENU)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(SIDEBAR));
        return this;
    }

    @Step("Выйти из аккаунта")
    public LoginPage logout() {
        openMenu();
        wait.until(ExpectedConditions.elementToBeClickable(LOGOUT_LINK)).click();
        return new LoginPage(driver, wait);
    }

    @Step("Проверить, что страница каталога загружена")
    public boolean isPageLoaded() {
        try {
            String title = wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_TITLE)).getText();
            return title.equals("Products");
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Проверить, что товар добавлен в корзину (кнопка 'Remove' видна)")
    public boolean isProductAddedToCart(String productName) {
        String id = productName.toLowerCase().replace(" ", "-");
        By removeButton = By.id("remove-" + id);
        try {
            return driver.findElement(removeButton).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
