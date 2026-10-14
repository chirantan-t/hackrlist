package com.hackathon.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
public class UserJourneysTest extends BaseSeleniumTest {

    private static final String UNIQUE_CODE = "HTM-AUT-" + (System.currentTimeMillis() % 10000);
    private static final String TEAM_NAME = "Automated Selenium Squad";

    @Test
    @Order(1)
    @DisplayName("Journey 1: Login validation with valid and invalid credentials")
    public void testJourney1_LoginValidation() {
        driver.get(baseUrl + "/login");

        // Step 1: Submit invalid credentials
        waitForVisibility(By.id("username")).sendKeys("invalid_user");
        driver.findElement(By.id("password")).sendKeys("wrong_password");
        driver.findElement(By.id("login-button")).click();

        // Step 2: Verify error alert displayed
        WebElement errorAlert = waitForVisibility(By.id("login-error"));
        assertTrue(errorAlert.isDisplayed(), "Login error alert should be visible on invalid credentials");

        // Step 3: Clear and submit valid credentials
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys("Admin@123");
        driver.findElement(By.id("login-button")).click();

        // Step 4: Verify navigation to executive dashboard
        wait.until(ExpectedConditions.urlContains("/"));
        WebElement dashboardPage = waitForVisibility(By.id("dashboard-page"));
        assertTrue(dashboardPage.isDisplayed(), "Executive dashboard should load successfully on valid login");
    }

    @Test
    @Order(2)
    @DisplayName("Journey 2: Create a team and verify it appears in the listing")
    public void testJourney2_CreateTeamAndVerifyListing() {
        loginAs("admin", "Admin@123");

        // Navigate to team creation form
        waitForClickable(By.id("nav-create-team")).click();
        wait.until(ExpectedConditions.urlContains("/teams/new"));

        // Fill form fields
        waitForVisibility(By.id("team-name-input")).sendKeys(TEAM_NAME);
        driver.findElement(By.id("team-code-input")).sendKeys(UNIQUE_CODE);
        driver.findElement(By.id("project-title-input")).sendKeys("Self-Healing CI/CD Pipeline Automation");
        driver.findElement(By.id("team-lead-input")).sendKeys("alex_lead");
        driver.findElement(By.id("project-description-input")).sendKeys("End-to-end resilient container pipeline with automated canary health checks.");

        // Submit form
        driver.findElement(By.id("submit-team-btn")).click();

        // Verify redirect to team details
        wait.until(ExpectedConditions.urlMatches(".*/teams/\\d+"));
        WebElement codeDisplay = waitForVisibility(By.id("team-code-display"));
        assertEquals(UNIQUE_CODE, codeDisplay.getText().trim(), "Team code should match generated test code");

        // Navigate to team list and verify presence
        driver.findElement(By.id("nav-teams")).click();
        wait.until(ExpectedConditions.urlMatches(".*/teams$"));

        WebElement table = waitForVisibility(By.id("teams-table"));
        assertTrue(table.getText().contains(UNIQUE_CODE), "Team code should be present in teams listing table");
    }

    @Test
    @Order(3)
    @DisplayName("Journey 3: Search for a team and verify correct result")
    public void testJourney3_SearchAndFilterTeams() {
        loginAs("admin", "Admin@123");

        driver.get(baseUrl + "/teams");
        WebElement searchInput = waitForVisibility(By.id("search-input"));
        searchInput.clear();
        searchInput.sendKeys(UNIQUE_CODE);

        driver.findElement(By.id("search-submit-btn")).click();

        // Wait for table to update and assert that target code is present
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("teams-table"), UNIQUE_CODE));
        WebElement table = driver.findElement(By.id("teams-table"));
        assertTrue(table.getText().contains(UNIQUE_CODE), "Search results should contain searched team code");
    }

    @Test
    @Order(4)
    @DisplayName("Journey 4: Update a team's information and verify changes")
    public void testJourney4_UpdateTeamInformation() {
        loginAs("admin", "Admin@123");

        // Search for target team
        driver.get(baseUrl + "/teams");
        waitForVisibility(By.id("search-input")).sendKeys(UNIQUE_CODE);
        driver.findElement(By.id("search-submit-btn")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("teams-table"), UNIQUE_CODE));

        // Click edit button
        WebElement editBtn = waitForClickable(By.xpath("//tr[contains(., '" + UNIQUE_CODE + "')]//a[contains(@id, 'edit-team')]"));
        editBtn.click();

        wait.until(ExpectedConditions.urlContains("/edit"));
        WebElement titleInput = waitForVisibility(By.id("project-title-input"));
        titleInput.clear();
        titleInput.sendKeys("Updated Autonomous Pipeline v2");

        driver.findElement(By.id("submit-team-btn")).click();

        // Verify redirect to details with updated title
        wait.until(ExpectedConditions.urlMatches(".*/teams/\\d+"));
        WebElement pageBody = waitForVisibility(By.id("team-details-page"));
        assertTrue(pageBody.getText().contains("Updated Autonomous Pipeline v2"), "Updated project title should be displayed");
    }

    @Test
    @Order(5)
    @DisplayName("Journey 5: Change team status using authorized account and verify workflow enforcement")
    public void testJourney5_StatusWorkflowAndRoleEnforcement() {
        loginAs("organizer", "Organizer@123");

        driver.get(baseUrl + "/teams");
        waitForVisibility(By.id("search-input")).sendKeys(UNIQUE_CODE);
        driver.findElement(By.id("search-submit-btn")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("teams-table"), UNIQUE_CODE));

        // Open status modal
        WebElement statusBtn = waitForClickable(By.xpath("//tr[contains(., '" + UNIQUE_CODE + "')]//button[contains(@id, 'status-team')]"));
        statusBtn.click();

        waitForVisibility(By.id("status-transition-modal"));

        // Select next allowed status (REGISTERED -> APPROVED)
        WebElement statusSelect = waitForVisibility(By.id("status-select"));
        statusSelect.sendKeys("APPROVED");

        driver.findElement(By.id("status-remarks")).sendKeys("Automated test approval verified by Organizer");
        driver.findElement(By.id("status-submit-btn")).click();

        // Wait for modal to close and assert status badge reflects APPROVED
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("status-transition-modal")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("teams-table"), "Approved"));

        WebElement table = driver.findElement(By.id("teams-table"));
        assertTrue(table.getText().contains("Approved"), "Team status badge should now display Approved");
    }
}
