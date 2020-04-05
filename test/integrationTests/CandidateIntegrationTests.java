package integrationTests;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CandidateIntegrationTests extends CareerSyncIntegrationTest {

    @Ignore
    @Test
    public void testCandidateCanLogInAndEnterKsas() {
        logIn("candidateTest", "TEMPoRARY_PASSWoRD1");
        String qualificationArea = "Selenium Testing: " + UUID.randomUUID().toString();
        WebElement element = driver.findElement(By.id("ksaProfile"));
        javaScriptClick(element);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("yourKsaProfile")));
        driver.findElement(By.name("qualificationArea")).clear();
        driver.findElement(By.name("qualificationArea")).sendKeys(qualificationArea);
        driver.findElement(By.xpath("/html/body/form/section[2]/div/button")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("careerSyncMainHeader")));
        assertEquals("http://localhost:9000/", driver.getCurrentUrl());

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("careerSyncMainHeader")));
        WebElement ksaProfileElement = driver.findElement(By.id("ksaProfile"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ksaProfileElement);
        assertEquals(qualificationArea, driver.findElement(By.name("qualificationArea")).getText());
    }

    @Ignore
    @Test
    public void testCandidateCanLogInAndViewJobRecommendations() {
        logIn("candidateTest", "TEMPoRARY_PASSWoRD1");
        WebElement element = driver.findElement(By.linkText("Job Recommendations"));
        javaScriptClick(element);
        assertEquals("http://localhost:9000/viewJobRecommendations", driver.getCurrentUrl());
    }

    @Ignore
    @Test
    public void testCandidateCanLogInAndEditTheirAccountDetails() {
        String firstName = "Selenium Test Name: " + UUID.randomUUID().toString();
        logIn("candidateTest", "TEMPoRARY_PASSWoRD1");
        navigateToEditAccount();
        WebElement firstNameInputElement = driver.findElement(By.name("firstName"));
        firstNameInputElement.clear();
        firstNameInputElement.sendKeys(firstName);
        driver.findElement(By.id("updateDetailsSubmit")).click();
        assertTrue(driver.findElement(By.xpath("/html/body/div[2]/div")).isDisplayed());
    }

    private void navigateToEditAccount() {
        WebElement myAccountElement = driver.findElement(By.linkText("My Account"));
        javaScriptClick(myAccountElement);
        WebElement editAccountElement = driver.findElement(By.linkText("Edit Account"));
        javaScriptClick(editAccountElement);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editAccountHeader")));
    }
}
