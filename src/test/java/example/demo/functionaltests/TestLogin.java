package example.demo.functionaltests;

import example.demo.functionaltests.pages.LoginPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.*;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import static example.demo.config.Config.ZAP_PROXY_ADDRESS;
import static example.demo.config.Config.ZAP_PROXY_PORT;

public class TestLogin {

    private WebDriver driver;
    private WebDriverManager webDriverManager = WebDriverManager.chromedriver();
    private ClientApi clientApi;
    private static ApiResponse apiResponse;

    @BeforeTest
    public void setUpTest() {
        String proxyAddress = ZAP_PROXY_ADDRESS + ":" + ZAP_PROXY_PORT;
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyAddress).setSslProxy(proxyAddress);

        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);
        options.addArguments("--ignore-certificate-errors");
        options.setProxy(proxy);

        driver = webDriverManager.capabilities(options).browserInDocker().dockerNetwork("ridx-cicd_jenkins-net").create();
        driver.manage().window().maximize();
        clientApi = new ClientApi(ZAP_PROXY_ADDRESS, ZAP_PROXY_PORT);
    }

    @AfterTest
    public void tearDown() {
        waitTillPassiveScanCompleted();
        if(clientApi != null){
            String title = "RiDX ZAP Security Report";
            String template = "traditional-html";
            String description = "This is RiDX ZAP security test report";
            String reportFilename = "ridx-zap-report";
            String targetFolder = "/zap/wrk/";
            try {
                clientApi.reports.generate(title, template, null, description, null, null,
                        null, null, null, reportFilename, null,
                        targetFolder, null);

            } catch (ClientApiException e){
                e.printStackTrace();
            }
        }
        if (driver != null) {
            driver.manage().deleteAllCookies();
            driver.quit();
        }
        webDriverManager.quit();
    }

    @Test
    public void verifyLoginSuccessfullyWithUserRole() {
        LoginPage.login(driver);
        WebElement menuBar = driver.findElement(By.className("ant-layout-sider-children"));
        Assert.assertTrue(menuBar.isDisplayed(), "Menu bar should be displayed after successful login.");
    }

    @Test(dependsOnMethods = "verifyLoginSuccessfullyWithUserRole")
    public void testNavigationBarAfterLoginSuccessfullyWithUserRole() {
        WebElement webElement = driver.findElement(By.cssSelector(LoginPage.DASHBOARD_LINK_CSS));
        webElement.click();
        Assert.assertTrue(driver.getCurrentUrl().endsWith("/dashboard"), "Should navigate to dashboard page.");
        webElement = driver.findElement(By.cssSelector(LoginPage.SYSTEM_PROFILES_LINK_CSS));
        webElement.click();
        Assert.assertTrue(driver.getCurrentUrl().endsWith("/system-profiles"), "Should navigate to system profiles page.");
    }

    private void waitTillPassiveScanCompleted() {
        try {
            // Check if the clientApi object and its pscan property are not null.
            // Object is null, it means that there is no active session with OWASP ZAP, and therefore no passive scan is in progress.
            //
            if (clientApi != null && clientApi.pscan != null) {
                // Send an API request to retrieve the number of records that need to be scanned
                apiResponse = clientApi.pscan.recordsToScan();
                // Get the value of the response and store it in tempVal
                String tempVal = ((ApiResponseElement) apiResponse).getValue();

                while (!tempVal.equals("0")) {
                    System.out.println("Passive scan is in progress...");
                    // Send another API request to retrieve the number of records that need to be scanned
                    apiResponse = clientApi.pscan.recordsToScan();
                    // Get the value of the response and store it in tempVal
                    tempVal = ((ApiResponseElement) apiResponse).getValue();
                }
                System.out.println("Passive scan is completed.");
            } else {
                System.out.println("Client API object is null.");
            }
        } catch (ClientApiException  e) {
            e.printStackTrace();
        }
    }

}
