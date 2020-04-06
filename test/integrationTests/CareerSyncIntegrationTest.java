package integrationTests;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class CareerSyncIntegrationTest {
    WebDriver driver;
    WebDriverWait wait;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 20);
    }

    protected void logIn(String username, String password) {
        driver.navigate().to("http://localhost:9000/logIn");
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.id("logInSubmit")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("careerSyncMainHeader")));
    }

    protected void javaScriptClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}
