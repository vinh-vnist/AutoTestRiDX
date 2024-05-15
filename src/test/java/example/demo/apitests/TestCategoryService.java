package example.demo.apitests;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static example.demo.config.Config.CATEGORY_PORT;

public class TestCategoryService {
    private static final String CPE_MAPPING_ENDPOINT = "/cves/mapping";
    private static final String CVE_ENDPOINT = "/cves";
    private static final String CWE_ENDPOINT = "/cwes";
    @BeforeTest
    public void setup(){
        RestAssured.port = CATEGORY_PORT;
    }
    @BeforeClass
    public void getAuthenToken(){

    }
    @Test
    public void testCpeMapping(){

    }
    @Test
    public void testGetCVEs(){

    }
    @Test
    public void testGetCVEById(){

    }
    @Test
    public void testGetCWEs(){

    }
    @Test
    public void testGetCWEById(){

    }
}
