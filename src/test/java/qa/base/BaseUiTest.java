package qa.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Базовый класс для UI-тестов (Selenium WebDriver).
 * Управляет жизненным циклом браузера.
 */
public class BaseUiTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected static final String BASE_URL = "https://www.saucedemo.com";
    protected static final int TIMEOUT_SECONDS = 10;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--remote-allow-origins=*");

        String browser = System.getProperty("browser", "chrome");
        if (browser.equals("firefox")) {
            // Firefox конфигурация (опционально)
        }

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
        driver.manage().window().maximize();
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Открыть страницу и дождаться загрузки
     */
    protected void openPage(String path) {
        driver.get(BASE_URL + path);
    }
}
