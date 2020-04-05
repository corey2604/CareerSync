package integrationTests;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RecruiterIntegrationTests extends CareerSyncIntegrationTest {

    @Test
    public void recruiterCanLogInAndViewJobDescriptions() {
        logIn("recruiterSeleniumTest", "TestPassword12345@");
        WebElement element = driver.findElement(By.id("uploadedJobDescriptions"));
        javaScriptClick(element);
        assertTrue(driver.findElement(By.id("yourJobDescriptions")).isDisplayed());
    }

    @Test
    public void recruiterCanLogInAndAddJobDescriptions() {
        logIn("recruiterSeleniumTest", "TestPassword12345@");
        WebElement element = driver.findElement(By.id("addJobDescription"));
        javaScriptClick(element);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("jobDescriptionDetails")));
        driver.findElement(By.name("referenceCode")).sendKeys(UUID.randomUUID().toString());
        driver.findElement(By.name("jobTitle")).sendKeys(UUID.randomUUID().toString());
        driver.findElement(By.name("location")).sendKeys(UUID.randomUUID().toString());
        driver.findElement(By.name("companyOrOrganisation")).sendKeys(UUID.randomUUID().toString());
        driver.findElement(By.name("hours")).sendKeys("100");
        driver.findElement(By.name("salary")).sendKeys("100");
        driver.findElement(By.name("mainPurposeOfJob")).sendKeys(UUID.randomUUID().toString());
        driver.findElement(By.name("mainResponsibilities")).sendKeys(UUID.randomUUID().toString());
        driver.findElement(By.name("qualificationArea")).sendKeys(UUID.randomUUID().toString());
        WebElement submitButton = driver.findElement(By.id("submitButton"));
        javaScriptClick(submitButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("careerSyncMainHeader")));
        assertEquals("http://localhost:9000/", driver.getCurrentUrl());
    }
}
