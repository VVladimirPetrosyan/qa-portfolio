package qa.config;

/**
 * Test configuration constants.
 * In production, use environment variables or properties files.
 */
public class TestConfig {

    private TestConfig() {}

    // SauceDemo credentials
    public static final String STANDARD_USER = "standard_user";
    public static final String LOCKED_USER = "locked_out_user";
    public static final String PROBLEM_USER = "problem_user";
    public static final String GLITCH_USER = "performance_glitch_user";
    public static final String PASSWORD = "secret_sauce";
    public static final String WRONG_PASSWORD = "wrong_password";

    // URLs
    public static final String JSONPLACEHOLDER_URL = "https://jsonplaceholder.typicode.com";
    public static final String SAUCEDEMO_URL = "https://www.saucedemo.com";

    // Timeouts
    public static final int TIMEOUT_SECONDS = 10;
    public static final int API_TIMEOUT_MS = 2000;
}
