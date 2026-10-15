package com.hackathon.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CaptureRoleDashboardsTest extends BaseSeleniumTest {

    private void saveScreenshot(String filename) {
        try {
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destPath = Paths.get("D:", "project ss", filename);
            Files.createDirectories(destPath.getParent());
            Files.copy(scrFile.toPath(), destPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println(">>> Saved: " + destPath.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Capture Role Dashboards")
    public void captureAllRoleDashboards() throws InterruptedException {
        // 1. Admin Dashboard
        loginAs("admin", "Admin@123");
        waitForVisibility(By.id("dashboard-page"));
        Thread.sleep(800);
        saveScreenshot("demo_role_admin_dashboard.png");

        // Logout
        driver.get(baseUrl + "/login");

        // 2. Organizer Dashboard
        loginAs("organizer", "Organizer@123");
        waitForVisibility(By.id("dashboard-page"));
        Thread.sleep(800);
        saveScreenshot("demo_role_organizer_dashboard.png");

        // Logout
        driver.get(baseUrl + "/login");

        // 3. Team Lead Dashboard
        loginAs("teamlead", "Lead@123");
        waitForVisibility(By.id("dashboard-page"));
        Thread.sleep(800);
        saveScreenshot("demo_role_teamlead_dashboard.png");
    }
}
