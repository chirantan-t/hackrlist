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
        WebElement userInput = waitForVisibility(By.id("username"));
        setInputValue(userInput, "invalid_user");

        WebElement passInput = waitForVisibility(By.id("password"));
        setInputValue(passInput, "wrong_password");

        jsClick(waitForClickable(By.id("login-button")));

        // Step 2: Verify error alert displayed
        WebElement errorAlert = waitForVisibility(By.id("login-error"));
        assertTrue(errorAlert.isDisplayed(), "Login error alert should be visible on invalid credentials");

        // Step 3: Clear and submit valid credentials
        setInputValue(userInput, "admin");
        setInputValue(passInput, "Admin@123");

        jsClick(waitForClickable(By.id("login-button")));

        // Step 4: Verify navigation to executive dashboard
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
        WebElement dashboardPage = waitForVisibility(By.id("dashboard-page"));
        assertTrue(dashboardPage.isDisplayed(), "Executive dashboard should load successfully on valid login");
    }

    @Test
    @Order(2)
    @DisplayName("Journey 2: Create a team and verify it appears in the listing")
    public void testJourney2_CreateTeamAndVerifyListing() {
        loginAs("admin", "Admin@123");

        // Navigate to team creation form
        jsClick(waitForClickable(By.id("nav-create-team")));
        wait.until(ExpectedConditions.urlContains("/teams/new"));

        // Fill form fields
        setInputValue(waitForVisibility(By.id("team-name-input")), TEAM_NAME);
        setInputValue(driver.findElement(By.id("team-code-input")), UNIQUE_CODE);
        setInputValue(driver.findElement(By.id("project-title-input")), "Self-Healing CI/CD Pipeline Automation");
        setInputValue(driver.findElement(By.id("team-lead-input")), "alex_lead");
        setInputValue(driver.findElement(By.id("project-description-input")), "End-to-end resilient container pipeline with automated canary health checks.");

        // Submit form
        jsClick(waitForClickable(By.id("submit-team-btn")));

        // Verify redirect to team details
        wait.until(ExpectedConditions.urlMatches(".*/teams/\\d+"));
        WebElement codeDisplay = waitForVisibility(By.id("team-code-display"));
        assertEquals(UNIQUE_CODE, codeDisplay.getText().trim(), "Team code should match generated test code");

        // Navigate to team list and verify presence
        jsClick(waitForClickable(By.id("nav-teams")));
        wait.until(ExpectedConditions.urlMatches(".*/teams$"));

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("teams-table"), UNIQUE_CODE));
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
        setInputValue(searchInput, UNIQUE_CODE);

        jsClick(waitForClickable(By.id("search-submit-btn")));

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
        setInputValue(waitForVisibility(By.id("search-input")), UNIQUE_CODE);
        jsClick(waitForClickable(By.id("search-submit-btn")));

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("teams-table"), UNIQUE_CODE));

        // Click edit button
        WebElement editBtn = waitForClickable(By.xpath("//tr[contains(., '" + UNIQUE_CODE + "')]//a[contains(@id, 'edit-team')]"));
        jsClick(editBtn);

        wait.until(ExpectedConditions.urlContains("/edit"));
        WebElement titleInput = waitForVisibility(By.id("project-title-input"));
        setInputValue(titleInput, "Updated Autonomous Pipeline v2");

        jsClick(waitForClickable(By.id("submit-team-btn")));

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
        setInputValue(waitForVisibility(By.id("search-input")), UNIQUE_CODE);
        jsClick(waitForClickable(By.id("search-submit-btn")));

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("teams-table"), UNIQUE_CODE));

        // Open status modal
        WebElement statusBtn = waitForClickable(By.xpath("//tr[contains(., '" + UNIQUE_CODE + "')]//button[contains(@id, 'status-team')]"));
        jsClick(statusBtn);

        waitForVisibility(By.id("status-transition-modal"));

        // Select next allowed status (REGISTERED -> APPROVED)
        WebElement statusSelect = waitForVisibility(By.id("status-select"));
        setSelectValue(statusSelect, "APPROVED");

        setInputValue(driver.findElement(By.id("status-remarks")), "Automated test approval verified by Organizer");
        jsClick(waitForClickable(By.id("status-submit-btn")));

        // Wait for modal to close and assert status badge reflects APPROVED
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("status-transition-modal")));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("teams-table"), "Approved"));

        WebElement table = driver.findElement(By.id("teams-table"));
        assertTrue(table.getText().contains("Approved"), "Team status badge should now display Approved");
    }
}
