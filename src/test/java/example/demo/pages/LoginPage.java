package example.demo.pages;

import io.github.cdimascio.dotenv.Dotenv;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    public static final String EMAIL_ID = "email";
    public static final String PASSWORD_ID = "password";
    public static final String LOGIN_BUTTON_XPATH = "//form/button";
    public static final String DASHBOARD_URL_FRAGMENT = "/dashboard";
    public static final String USER_MANAGEMENT_LINK_CSS = "li.ant-menu-item.ant-menu-item-only-child > span > a";
    public static final String DASHBOARD_LINK_CSS = "[title=\"Dashboard\"]";
    public static final String SYSTEM_PROFILES_LINK_CSS = "[title=\"System profiles\"]";
    public static Dotenv dotenv = Dotenv.load();
    public static String adminEmail = dotenv.get("ADMIN_EMAIL");
    public static String adminPassword = dotenv.get("ADMIN_PASSWORD");
    public static String userEmail = dotenv.get("USER_EMAIL");
    public static String userPassword = dotenv.get("USER_PASSWORD");
    public static String baseUrl = dotenv.get("BASE_URL");

    public static void login(WebDriver driver) {
        driver.get(baseUrl);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        WebElement inputEmailField = wait.until(ExpectedConditions.elementToBeClickable(By.id(EMAIL_ID)));
        inputEmailField.sendKeys(userEmail);

        WebElement passwordField = driver.findElement(By.id(PASSWORD_ID));
        passwordField.sendKeys(userPassword);

        driver.findElement(By.xpath(LOGIN_BUTTON_XPATH)).click();
        wait.until(ExpectedConditions.urlContains(DASHBOARD_URL_FRAGMENT));
    }

}
