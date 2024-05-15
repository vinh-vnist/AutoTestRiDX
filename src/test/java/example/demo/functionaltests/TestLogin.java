package example.demo.functionaltests;

import example.demo.pages.LoginPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class TestLogin {

    private WebDriver driver;

    @BeforeMethod
    public void setUpTest() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.manage().deleteAllCookies();
            driver.quit();
        }
    }

    @Test
    public void verifyLoginSuccessfullyWithAdminRole() {
        LoginPage.login(driver);
        WebElement menuBar = driver.findElement(By.className("ant-layout-sider-children"));
        Assert.assertTrue(menuBar.isDisplayed(), "Menu bar should be displayed after successful login.");
    }

    @Test()
    public void verifyAccessUserManagementAfterLoginSuccessfullyWithAdminRole() {
        LoginPage.login(driver);
        WebElement userManagermentSide = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.cssSelector(LoginPage.USER_MANAGEMENT_LINK_CSS)));
        userManagermentSide.click();

        Assert.assertTrue(driver.getCurrentUrl().endsWith("/admin/users"), "Should navigate to user management page.");
    }

}
