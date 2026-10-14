package com.hackathon.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;

    @BeforeEach
    public void setupDriver() {
        this.baseUrl = System.getProperty("app.base.url",
                System.getenv().getOrDefault("APP_BASE_URL", "http://localhost:5173"));

        ChromeOptions options = new ChromeOptions();
        boolean isHeadless = Boolean.parseBoolean(System.getProperty("headless",
                System.getenv().getOrDefault("HEADLESS", "true")));

        if (isHeadless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void setInputValue(WebElement element, String value) {
        ((JavascriptExecutor) driver).executeScript(
            "const el = arguments[0];" +
            "const val = arguments[1];" +
            "if (el._valueTracker) { el._valueTracker.setValue('__init__'); }" +
            "const proto = Object.getPrototypeOf(el);" +
            "const descriptor = Object.getOwnPropertyDescriptor(proto, 'value') || " +
            "                   Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value');" +
            "if (descriptor && descriptor.set) { descriptor.set.call(el, val); } else { el.value = val; }" +
            "el.dispatchEvent(new Event('input', { bubbles: true }));" +
            "el.dispatchEvent(new Event('change', { bubbles: true }));",
            element, value
        );
    }

    protected void setSelectValue(WebElement element, String value) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1];" +
            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            element, value
        );
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void loginAs(String username, String password) {
        driver.get(baseUrl + "/login");
        WebElement userField = waitForVisibility(By.id("username"));
        setInputValue(userField, username);

        WebElement passField = waitForVisibility(By.id("password"));
        setInputValue(passField, password);

        jsClick(waitForClickable(By.id("login-button")));
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
    }

    @RegisterExtension
    public AfterTestExecutionCallback screenshotWatcher = context -> {
        if (context.getExecutionException().isPresent() && driver instanceof TakesScreenshot) {
            try {
                File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String testName = context.getRequiredTestMethod().getName();
                Path destPath = Paths.get("target", "screenshots", testName + "_" + timestamp + ".png");
                Files.createDirectories(destPath.getParent());
                Files.copy(scrFile.toPath(), destPath);
                System.out.println(">>> Saved failure screenshot to: " + destPath.toAbsolutePath());
            } catch (Exception e) {
                System.err.println("Failed to capture screenshot: " + e.getMessage());
            }
        }
    };
}
