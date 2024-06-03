package example.demo.securitytests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.ProxySpecification;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ClientApi;
import org.zaproxy.clientapi.core.ClientApiException;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

public class PassiveScan {

    private static final String ZAP_PROXY_ADDRESS = "localhost";
    private static final int ZAP_PORT = 8080;
    private WebDriver driver;
    String targetSites = "https://demo.testfire.net/";
    private static ApiResponse apiResponse;
    private static ClientApi clientApi;
    private static String REPORT_DIR_NAME = "ReportArchive";

    @BeforeClass
    public void setUp() throws IOException {
        // Path to zap .bat  file.
        String zapPath = "C:" + File.separator + "Program Files" + File.separator + "OWASP" + File.separator + "Zed Attack Proxy" + File.separator + "zap.bat";
        startZapProxy(zapPath);
        // sets up the proxy configuration for RestAssured to use the specified proxy server and port for all HTTP requests sent through RestAssured.
        ProxySpecification proxySpec = new ProxySpecification(ZAP_PROXY_ADDRESS, ZAP_PORT, "http");
        verifyThatZapProxyIsUpAndRunning(proxySpec);
    }


    @Test
    public void passiveScanDemo(){
        loginToDemoSite();
    }

    private  void loginToDemoSite() {
        driver= setUpFireFoxDriver();
        // Navigate to login page
        driver.get("https://demo.testfire.net/login.jsp");
        // Find the username input field and enter email address
        WebElement usernameField = driver.findElement(By.id("uid"));
        usernameField.sendKeys("admin");
        WebElement password = driver.findElement(By.id("passw"));
        password.sendKeys("admin");
        WebElement submit = driver.findElement(By.name("btnSubmit"));
        submit.click();
        waitTillPassiveScanCompleted();
        driver.quit();

        System.out.println("loginToDemoSite is done.");
    }

    private static void startZapProxy(String zapPath) throws IOException {
        //ProcessBuilder is a class in the java.lang.ProcessBuilder package that allows you to create and control new processes in Java.
        ProcessBuilder processBuilder = new ProcessBuilder(zapPath);
        processBuilder.start();
        pause(60000);// Waited 1 minute  till it' started.
        /*
        This line starts the ZAP proxy process by calling the start() method on the processBuilder object.
        This launches the ZAP proxy process and returns a new Process object that represents the running process.
        By calling this method, you can launch the ZAP proxy process from within your Java code, which allows you to automate the process of starting and stopping the proxy, and integrate the proxy into your testing or security scanning workflows.
         */

    }


    private void verifyThatZapProxyIsUpAndRunning(ProxySpecification proxySpec) {
        RestAssured.proxy(proxySpec);

        // If the ZAP proxy is running and accessible status code should be 200.
        Response response = RestAssured.given()
                .get("http://localhost:8080/JSON/core/view/version/"); // Can observe this in a browser by manually go to http://localhost:8080/
        int statusCode = response.getStatusCode();
        if (statusCode != 200) {
            System.out.println("ZAP proxy is down. Status code: " + statusCode);
        }
        else{
            System.out.println("ZAP proxy is up and running. Status code: " + statusCode);
        }
    }
    // The method waitTillPassiveScanCompleted() is called to initiate the waiting process.
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

                // Loop until all records have been scanned
                while (!tempVal.equals("0")) {
                    // Print a message indicating that the passive scan is still in progress
                    System.out.println("Passive scan is in progress...");
                    // Send another API request to retrieve the number of records that need to be scanned
                    apiResponse = clientApi.pscan.recordsToScan();
                    // Get the value of the response and store it in tempVal
                    tempVal = ((ApiResponseElement) apiResponse).getValue();
                }
                // Print a message indicating that the passive scan is completed
                System.out.println("Passive scan is completed.");
            } else {
                // Print a message indicating that the client API object is null
                System.out.println("Client API object is null.");
            }
        } catch (ClientApiException  e) {
            // Print the stack trace of the exception if one occurs
            e.printStackTrace();
        }
    }


    /*

A while loop is then used to continuously check if the passive scan has completed or not. The loop condition checks if tempVal is not equal to "0". If it's not equal to "0", then the passive scan is still in progress and the loop continues.
If tempVal is equal to "0", then the passive scan has completed and the loop exits.
The method then prints a message indicating that the passive scan has completed.
         */


    // This was  the early report they  had.
    private static void generateHtmlReport() throws ClientApiException {
        clientApi = new ClientApi("localhost", ZAP_PORT);
        // Generate the HTML report and save it to a file
        byte[] reportBytes = clientApi.core.htmlreport();
        String reportFileName = "zap-report.html";
        try {
            FileUtils.writeByteArrayToFile(new File(reportFileName), reportBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ZAP HTML report generated: " + reportFileName);
    }

    // The new report
    public static void generateZapReport(String URLS) {
        String title = "Demo Passive report Title";
        String template = "traditional-html";
        String theme = null;
        String description = "Report description";
        String contexts = null;
        String sites = URLS;// you can give URL that wants to track.
        String sections = null;
        String includedconfidences = null;
        String includedrisks = null;
        String reportFileName = "PassiveScanHtmlReport";
        String reportfilenamepattern = null;
        String reportDirectory = System.getProperty("user.dir") + File.separator + REPORT_DIR_NAME;
        String display = null;

        try {
            clientApi.reports.generate(title, template, theme, description, contexts, sites, sections,
                    includedconfidences, includedrisks, reportFileName, reportfilenamepattern, reportDirectory, display);
        } catch (ClientApiException e) {
            e.printStackTrace();
        }
    }

    private static void pause(int pause) {
        try {
            Thread.sleep(pause);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private WebDriver setUpFireFoxDriver(){
        // This code creates a new Selenium proxy object
        // and sets the HTTP and SSL proxy settings to route all traffic through a ZAP proxy.
        Proxy proxy = new Proxy();
        // This line creates a new Selenium Proxy object.
        // A proxy is an intermediary server that acts as a gateway between a client (e.g. a web browser) and a server (e.g. a website).
        // This line sets the HTTP proxy setting of the Proxy object to the address and port of a ZAP proxy.
        proxy.setHttpProxy(ZAP_PROXY_ADDRESS + ":" + ZAP_PORT);
        /*
        This line sets the SSL proxy setting of the Proxy object to the same ZAP proxy address and port as the HTTP proxy setting.
        This is necessary for HTTPS traffic, which is encrypted and requires a separate SSL proxy to decrypt and intercept the traffic.
         */
        proxy.setSslProxy(ZAP_PROXY_ADDRESS + ":" + ZAP_PORT);


        // Create an instance of the ChromeDriver
        FirefoxOptions options = new FirefoxOptions();
        options.setCapability("proxy", proxy);
        /*
        By setting the "proxy" capability to the Proxy object, any traffic sent through
        the WebDriver-controlled Firefox  browser will be routed through the ZAP proxy that
        was previously configured, allowing ZAP to intercept and scan the traffic for security vulnerabilities.
         */
        driver = new FirefoxDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        return driver;
    }

    @AfterClass
    public void tearDown() throws  ClientApiException {
        generateHtmlReport();
        generateZapReport(targetSites);

    }

}