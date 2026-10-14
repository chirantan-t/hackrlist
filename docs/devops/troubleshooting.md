# DevOps Troubleshooting & Diagnostics Guide

**Project:** Dockerized Hackathon Team Management System  

---

## 1. Port Conflicts

### Symptom: `Address already in use: bind` on Port 8080
* **Root Cause:** A host service (such as Jenkins 2.x) is already occupying port 8080.
* **Diagnosis:**
  ```powershell
  # Windows
  Get-NetTCPConnection -LocalPort 8080 | Select-Object LocalAddress, LocalPort, State, OwningProcess
  ```
  ```bash
  # Linux
  sudo ss -tulpn | grep 8080
  ```
* **Resolution:**
  - In local development, the Spring Boot backend defaults to port `8082`.
  - Override via environment variable:
    ```bash
    export SERVER_PORT=8085
    mvn spring-boot:run
    ```

---

## 2. Database Connectivity Failures

### Symptom: `Communications link failure` or `Connection refused`
* **Root Cause:** MySQL container is still initializing or network name resolution failed.
* **Diagnosis:**
  ```bash
  docker compose logs db
  ```
* **Resolution:**
  - The `backend` service in `docker-compose.yml` uses `depends_on: db: condition: service_healthy` to ensure Spring Boot does not attempt connection before MySQL is fully ready.
  - Check credentials match between `.env` and Spring datasource properties.

---

## 3. Selenium Browser Automation Issues

### Symptom: `SessionNotCreatedException` or `Could not start a new session`
* **Root Cause:** Chrome binary missing or incompatible driver version.
* **Diagnosis:**
  Verify Chrome installation:
  ```powershell
  (Get-Item 'C:\Program Files\Google\Chrome\Application\chrome.exe').VersionInfo.FileVersion
  ```
* **Resolution:**
  - Selenium 4 includes **Selenium Manager**, which automatically resolves the appropriate ChromeDriver binary for the installed Chrome version.
  - Run headless mode with `-Dheadless=true` if executing on a headless CI agent.

---

## 4. Failed Health Check Rollback

### Symptom: `Deployment failed health checks. Triggering automated rollback.`
* **Root Cause:** New container image crashed or took longer than 45 seconds to initialize.
* **Resolution:**
  1. Inspect container logs:
     ```bash
     docker compose logs --tail=100 backend
     ```
  2. Verify that `.previous_stable_version` points to a known good image tag (e.g., `1.0.0`).
  3. Re-run `./deployment/scripts/rollback.sh` manually if needed.
