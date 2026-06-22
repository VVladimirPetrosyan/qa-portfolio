package qa.ui;

import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import qa.base.BaseUiTest;
import qa.config.TestConfig;
import qa.pages.CatalogPage;
import qa.pages.LoginPage;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Epic("UI Tests")
@Feature("Catalog")
@DisplayName("Catalog Tests")
public class CatalogTest extends BaseUiTest {

    private CatalogPage catalogPage;

    @BeforeEach
    void loginAndGoToCatalog() {
        LoginPage loginPage = new LoginPage(driver, wait);
        loginPage.loginAs(TestConfig.STANDARD_USER, TestConfig.PASSWORD);
        catalogPage = new CatalogPage(driver, wait);
    }

    @Test
    @Story("Каталог: отображение товаров")
    @DisplayName("Каталог: 6 товаров на странице")
    @Severity(SeverityLevel.BLOCKER)
    void catalog_shouldDisplay6Items() {
        assertThat(catalogPage.isPageLoaded()).isTrue();
        assertThat(catalogPage.getItemCount()).isEqualTo(6);
    }

    @Test
    @Story("Каталог: заголовок страницы")
    @DisplayName("Каталог: заголовок 'Products'")
    @Severity(SeverityLevel.CRITICAL)
    void catalog_shouldShowProductsTitle() {
        assertThat(catalogPage.getPageTitle()).isEqualTo("Products");
    }

    @Test
    @Story("Каталог: все товары имеют название")
    @DisplayName("Каталог: у всех товаров есть имя")
    @Severity(SeverityLevel.CRITICAL)
    void catalog_allItemsHaveNames() {
        List<String> names = catalogPage.getProductNames();

        assertThat(names).hasSize(6);
        assertThat(names).allMatch(name -> !name.isEmpty());
    }

    @Test
    @Story("Каталог: все товары имеют цену")
    @DisplayName("Каталог: у всех товаров есть цена ($)")
    @Severity(SeverityLevel.CRITICAL)
    void catalog_allItemsHavePrices() {
        List<String> prices = catalogPage.getProductPrices();

        assertThat(prices).hasSize(6);
        assertThat(prices).allMatch(price -> price.startsWith("$"));
    }

    @Test
    @Story("Каталог: добавить товар в корзину")
    @DisplayName("Добавить 'Sauce Labs Backpack' -> кнопка меняется на 'Remove'")
    @Severity(SeverityLevel.BLOCKER)
    void catalog_addToCart_shouldChangeButtonToRemove() {
        catalogPage.addToCart("Sauce Labs Backpack");

        assertThat(catalogPage.isProductAddedToCart("Sauce Labs Backpack")).isTrue();
        assertThat(catalogPage.getCartCount()).isEqualTo(1);
    }

    @Test
    @Story("Каталог: добавить несколько товаров")
    @DisplayName("Добавить 3 товара -> корзина показывает 3")
    @Severity(SeverityLevel.CRITICAL)
    void catalog_addMultipleItems_shouldUpdateBadge() {
        catalogPage.addToCart("Sauce Labs Backpack");
        catalogPage.addToCart("Sauce Labs Bike Light");
        catalogPage.addToCart("Sauce Labs Bolt T-Shirt");

        assertThat(catalogPage.getCartCount()).isEqualTo(3);
    }

    @Test
    @Story("Каталог: удалить товар из каталога")
    @DisplayName("Добавить -> удалить -> кнопка возвращается")
    @Severity(SeverityLevel.CRITICAL)
    void catalog_addThenRemove_shouldResetButton() {
        catalogPage.addToCart("Sauce Labs Backpack");
        assertThat(catalogPage.isProductAddedToCart("Sauce Labs Backpack")).isTrue();

        catalogPage.removeFromCart("Sauce Labs Backpack");
        assertThat(catalogPage.isProductAddedToCart("Sauce Labs Backpack")).isFalse();
        assertThat(catalogPage.getCartCount()).isEqualTo(0);
    }

    @Test
    @Story("Каталог: сортировка по цене (от дешёвых)")
    @DisplayName("Сортировка: Price (low to high)")
    @Severity(SeverityLevel.NORMAL)
    void catalog_sortByPriceLowToHigh() {
        catalogPage.sortBy("lohi");

        List<String> prices = catalogPage.getProductPrices();
        for (int i = 0; i < prices.size() - 1; i++) {
            double current = Double.parseDouble(prices.get(i).replace("$", ""));
            double next = Double.parseDouble(prices.get(i + 1).replace("$", ""));
            assertThat(current).isLessThanOrEqualTo(next);
        }
    }

    @Test
    @Story("Каталог: сортировка по цене (от дорогих)")
    @DisplayName("Сортировка: Price (high to low)")
    @Severity(SeverityLevel.NORMAL)
    void catalog_sortByPriceHighToLow() {
        catalogPage.sortBy("hilo");

        List<String> prices = catalogPage.getProductPrices();
        for (int i = 0; i < prices.size() - 1; i++) {
            double current = Double.parseDouble(prices.get(i).replace("$", ""));
            double next = Double.parseDouble(prices.get(i + 1).replace("$", ""));
            assertThat(current).isGreaterThanOrEqualTo(next);
        }
    }

    @Test
    @Story("Каталог: сортировка по имени (A-Z)")
    @DisplayName("Сортировка: Name (A to Z)")
    @Severity(SeverityLevel.NORMAL)
    void catalog_sortByNameAtoZ() {
        catalogPage.sortBy("az");

        List<String> names = catalogPage.getProductNames();
        List<String> sorted = names.stream().sorted().toList();
        assertThat(names).isEqualTo(sorted);
    }

    @Test
    @Story("Каталог: сортировка по имени (Z-A)")
    @DisplayName("Сортировка: Name (Z to A)")
    @Severity(SeverityLevel.NORMAL)
    void catalog_sortByNameZtoA() {
        catalogPage.sortBy("za");

        List<String> names = catalogPage.getProductNames();
        List<String> sortedDesc = names.stream().sorted(java.util.Comparator.reverseOrder()).toList();
        assertThat(names).isEqualTo(sortedDesc);
    }

    @Test
    @Story("Каталог: переход в корзину")
    @DisplayName("Каталог -> Корзина: переход по иконке корзины")
    @Severity(SeverityLevel.CRITICAL)
    void catalog_goToCart_shouldNavigateToCartPage() {
        var cartPage = catalogPage.goToCart();

        assertThat(cartPage.isPageLoaded()).isTrue();
        assertThat(cartPage.getPageTitle()).isEqualTo("Your Cart");
    }

    @Test
    @Story("Каталог: добавить товар и перейти в корзину")
    @DisplayName("Добавить товар -> перейти в корзину -> товар виден")
    @Severity(SeverityLevel.BLOCKER)
    void catalog_addAndGoToCart_shouldSeeItem() {
        catalogPage.addToCart("Sauce Labs Backpack");
        var cartPage = catalogPage.goToCart();

        assertThat(cartPage.getProductNames()).contains("Sauce Labs Backpack");
    }

    @Test
    @Story("Каталог: выход из аккаунта")
    @DisplayName("Бургер-меню -> Logout -> страница логина")
    @Severity(SeverityLevel.NORMAL)
    void catalog_logout_shouldReturnToLoginPage() {
        var loginPage = catalogPage.logout();

        assertThat(loginPage.isPageLoaded()).isTrue();
    }
}
