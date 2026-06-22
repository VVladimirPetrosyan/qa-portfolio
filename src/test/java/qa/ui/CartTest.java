package qa.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import qa.base.BaseUiTest;
import qa.config.TestConfig;
import qa.pages.*;

import static org.assertj.core.api.Assertions.*;

@Epic("UI Tests")
@Feature("Cart & Checkout")
@DisplayName("Cart and Checkout Tests")
public class CartTest extends BaseUiTest {

    private LoginPage loginPage;
    private CatalogPage catalogPage;

    @BeforeEach
    void loginAndGoToCatalog() {
        loginPage = new LoginPage(driver, wait);
        loginPage.loginAs(TestConfig.STANDARD_USER, TestConfig.PASSWORD);
        catalogPage = new CatalogPage(driver, wait);
    }

    @Test
    @Story("Корзина: пустая корзина")
    @DisplayName("Корзина: пустая -> 0 товаров")
    @Severity(SeverityLevel.CRITICAL)
    void cart_empty_shouldHave0Items() {
        var cartPage = catalogPage.goToCart();

        assertThat(cartPage.isCartEmpty()).isTrue();
        assertThat(cartPage.getItemCount()).isEqualTo(0);
    }

    @Test
    @Story("Корзина: добавить 1 товар")
    @DisplayName("Добавить товар -> корзина: 1 товар")
    @Severity(SeverityLevel.BLOCKER)
    void cart_addOneItem_shouldHave1Item() {
        catalogPage.addToCart("Sauce Labs Backpack");
        var cartPage = catalogPage.goToCart();

        assertThat(cartPage.getItemCount()).isEqualTo(1);
        assertThat(cartPage.getProductNames()).contains("Sauce Labs Backpack");
    }

    @Test
    @Story("Корзина: добавить 3 товара")
    @DisplayName("Добавить 3 товара -> корзина: 3 товара")
    @Severity(SeverityLevel.CRITICAL)
    void cart_addThreeItems_shouldHave3Items() {
        catalogPage.addToCart("Sauce Labs Backpack");
        catalogPage.addToCart("Sauce Labs Bike Light");
        catalogPage.addToCart("Sauce Labs Bolt T-Shirt");

        var cartPage = catalogPage.goToCart();

        assertThat(cartPage.getItemCount()).isEqualTo(3);
    }

    @Test
    @Story("Корзина: удалить товар из корзины")
    @DisplayName("Добавить -> удалить из корзины -> корзина пуста")
    @Severity(SeverityLevel.CRITICAL)
    void cart_removeItem_shouldEmptyCart() {
        catalogPage.addToCart("Sauce Labs Backpack");
        var cartPage = catalogPage.goToCart();

        cartPage.removeItem("Sauce Labs Backpack");

        assertThat(cartPage.isCartEmpty()).isTrue();
    }

    @Test
    @Story("Корзина: вернуться в каталог")
    @DisplayName("Корзина -> Continue Shopping -> каталог")
    @Severity(SeverityLevel.NORMAL)
    void cart_continueShopping_shouldReturnToCatalog() {
        var cartPage = catalogPage.goToCart();
        var returnedCatalog = cartPage.continueShopping();

        assertThat(returnedCatalog.isPageLoaded()).isTrue();
    }

    @Test
    @Story("Checkout: успешная покупка (E2E)")
    @DisplayName("E2E: Логин -> товар -> корзина -> Checkout -> Finish")
    @Severity(SeverityLevel.BLOCKER)
    void checkout_fullFlow_shouldComplete() {
        catalogPage.addToCart("Sauce Labs Backpack");
        var cartPage = catalogPage.goToCart();
        var checkoutPage = cartPage.goToCheckout();
        assertThat(checkoutPage.isPageLoaded()).isTrue();

        checkoutPage.fillCheckoutInfo("Roman", "Shklyaev", "12345");
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();

        assertThat(checkoutPage.isOrderComplete()).isTrue();
    }

    @Test
    @Story("Checkout: пустое имя")
    @DisplayName("Checkout: пустое имя -> ошибка 'First Name is required'")
    @Severity(SeverityLevel.CRITICAL)
    void checkout_emptyFirstName_shouldShowError() {
        catalogPage.addToCart("Sauce Labs Backpack");
        var cartPage = catalogPage.goToCart();
        var checkoutPage = cartPage.goToCheckout();

        checkoutPage.fillCheckoutInfo("", "Shklyaev", "12345");
        checkoutPage.clickContinue();

        assertThat(checkoutPage.getErrorMessage()).contains("First Name is required");
    }

    @Test
    @Story("Checkout: пустая фамилия")
    @DisplayName("Checkout: пустая фамилия -> ошибка 'Last Name is required'")
    @Severity(SeverityLevel.CRITICAL)
    void checkout_emptyLastName_shouldShowError() {
        catalogPage.addToCart("Sauce Labs Backpack");
        var cartPage = catalogPage.goToCart();
        var checkoutPage = cartPage.goToCheckout();

        checkoutPage.fillCheckoutInfo("Roman", "", "12345");
        checkoutPage.clickContinue();

        assertThat(checkoutPage.getErrorMessage()).contains("Last Name is required");
    }

    @Test
    @Story("Checkout: пустой индекс")
    @DisplayName("Checkout: пустой индекс -> ошибка 'Postal Code is required'")
    @Severity(SeverityLevel.CRITICAL)
    void checkout_emptyPostalCode_shouldShowError() {
        catalogPage.addToCart("Sauce Labs Backpack");
        var cartPage = catalogPage.goToCart();
        var checkoutPage = cartPage.goToCheckout();

        checkoutPage.fillCheckoutInfo("Roman", "Shklyaev", "");
        checkoutPage.clickContinue();

        assertThat(checkoutPage.getErrorMessage()).contains("Postal/Zip code is required");
    }

    @Test
    @Story("Checkout: отмена заказа")
    @DisplayName("Checkout -> Cancel -> возврат в корзину")
    @Severity(SeverityLevel.NORMAL)
    void checkout_cancel_shouldReturnToCart() {
        catalogPage.addToCart("Sauce Labs Backpack");
        var cartPage = catalogPage.goToCart();
        var checkoutPage = cartPage.goToCheckout();

        var returnedCart = checkoutPage.clickCancel();

        assertThat(returnedCart.isPageLoaded()).isTrue();
    }

    @Test
    @Story("Checkout: итоговая сумма")
    @DisplayName("Checkout: отображается Item Total, Tax, Total")
    @Severity(SeverityLevel.NORMAL)
    void checkout_shouldShowPriceSummary() {
        catalogPage.addToCart("Sauce Labs Backpack");
        var cartPage = catalogPage.goToCart();
        var checkoutPage = cartPage.goToCheckout();

        checkoutPage.fillCheckoutInfo("Roman", "Shklyaev", "12345");
        checkoutPage.clickContinue();

        assertThat(checkoutPage.getItemTotal()).contains("Item total:");
        assertThat(checkoutPage.getTax()).contains("Tax:");
        assertThat(checkoutPage.getTotal()).contains("Total:");
    }

    @Test
    @Story("Checkout: возврат на главную после покупки")
    @DisplayName("После покупки -> Back Home -> каталог")
    @Severity(SeverityLevel.NORMAL)
    void checkout_afterComplete_shouldReturnToCatalog() {
        catalogPage.addToCart("Sauce Labs Backpack");
        var cartPage = catalogPage.goToCart();
        var checkoutPage = cartPage.goToCheckout();

        checkoutPage.fillCheckoutInfo("Roman", "Shklyaev", "12345");
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();

        var catalog = checkoutPage.goBackHome();
        assertThat(catalog.isPageLoaded()).isTrue();
    }
}
