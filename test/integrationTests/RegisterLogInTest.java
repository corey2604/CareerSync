package integrationTests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;

public class RegisterLogInTest extends CareerSyncIntegrationTest {

    @Test
    public void testLogIn() {
        // Launch website
        logIn("candidateTest", "TEMPoRARY_PASSWoRD1");
        assertEquals("http://localhost:9000/", driver.getCurrentUrl());
    }

    @Test
    public void testRegister() {
        // Launch website
        driver.navigate().to("http://localhost:9000/register");

        String username = UUID.randomUUID().toString();

        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("email")).sendKeys("test@email.com");
        driver.findElement(By.name("password")).sendKeys("TestPassword12345@");
        driver.findElement(By.name("firstName")).sendKeys("Selenium");
        driver.findElement(By.name("lastName")).sendKeys("Test");
        driver.findElement(By.name("phoneNumber")).sendKeys("0123456789");
        driver.findElement(By.id("signUpSubmit")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("careerSyncMainHeader")));
        assertEquals("http://localhost:9000/", driver.getCurrentUrl());
    }

}
