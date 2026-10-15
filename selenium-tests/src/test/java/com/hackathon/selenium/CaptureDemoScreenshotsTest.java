package com.hackathon.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CaptureDemoScreenshotsTest extends BaseSeleniumTest {

    private void saveScreenshot(String filename) {
        try {
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destPath = Paths.get("D:", "project ss", filename);
            Files.createDirectories(destPath.getParent());
            Files.copy(scrFile.toPath(), destPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println(">>> Successfully saved live demo screenshot to: " + destPath.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to capture demo screenshot: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Capture Live Demo Screenshots")
    public void captureAllDemoScreenshots() throws InterruptedException {
        // 1. Login as Admin
        loginAs("admin", "Admin@123");

        // 2. Dashboard Screenshot
        waitForVisibility(By.id("dashboard-page"));
        Thread.sleep(1000); // Allow charts to render smoothly
        saveScreenshot("demo01_executive_dashboard.png");

        // 3. Team Listing Screenshot
        driver.get(baseUrl + "/teams");
        waitForVisibility(By.id("team-list-page"));
        waitForVisibility(By.id("teams-table"));
        Thread.sleep(800);
        saveScreenshot("demo02_team_listing_and_filters.png");

        // 4. Team Creation Form Screenshot
        driver.get(baseUrl + "/teams/new");
        waitForVisibility(By.id("team-form-page"));
        Thread.sleep(500);
        saveScreenshot("demo03_team_registration_form.png");

        // 5. Team Details & Members Screenshot
        driver.get(baseUrl + "/teams/1");
        waitForVisibility(By.id("team-details-page"));
        Thread.sleep(500);
        saveScreenshot("demo04_status_workflow_and_members.png");
    }
}
