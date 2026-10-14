# Final Examination Demonstration Script

**Project:** Dockerized Hackathon Team Management System  
**Audience:** Academic Reviewers, DevOps Evaluators, and Engineering Leads  
**Duration:** 15-20 Minutes Live Demonstration  

---

## Demonstration Sequence

### Step 1: User Authentication & Role Detection
1. Open browser at `http://localhost:5173` (or `http://localhost:8081` in Docker).
2. Attempt login with invalid credentials: `admin` / `WrongPass`.
3. *Expected Result:* System displays inline red alert `"Invalid username or password"`.
4. Click the **ADMIN** quick-fill button (`admin` / `Admin@123`) and click **Sign In**.
5. *Expected Result:* User session is established and user is redirected to the executive dashboard.

### Step 2: Executive Dashboard & Live Telemetry
1. Inspect the 7 KPI StatCards:
   - Total Teams, Registered, Approved, In Progress, Submitted, Completed, Total Members.
2. Observe the interactive Recharts visualizations:
   - Status Distribution Bar Chart.
   - Track Distribution Bar Chart.
3. Review the **Recently Updated Teams** audit table.

### Step 3: Hackathon Team Registration
1. Click **Register New Team** (`/teams/new`).
2. Enter team details:
   - Team Name: `Autonomous Vision Swarm`
   - Team Code: Click the sparkle button to generate a unique code (e.g. `HTM-8F2A`).
   - Project Title: `Edge AI Aerial Mapping`
   - Project Description: `Real-time multi-agent disaster response mapping.`
   - Track: `AI_ML`
3. Click **Complete Registration**.
4. *Expected Result:* System returns HTTP 201 Created and redirects to the team details profile.

### Step 4: Team Listing & Pagination
1. Click **Hackathon Teams** in the sidebar (`/teams`).
2. Verify that the newly registered team appears in the list with `REGISTERED` status and code badge.
3. Inspect pagination controls showing total page counts and navigation buttons.

### Step 5: Multi-Criteria Search & Filtering
1. In the search bar, type `Autonomous` and click **Search**.
2. *Expected Result:* Table filters in real time to show only the matching team.
3. Clear search and set **Track Filter** to `FinTech`.
4. *Expected Result:* Displays only teams allocated to the FinTech track. Click **Reset** to restore full list.

### Step 6: Team Metadata Update
1. Click the **Edit** action icon next to a team.
2. Update the project title: Append `[v2.0 Revision]`.
3. Click **Update Team**.
4. *Expected Result:* Team profile updates immediately with new project title and updated timestamp.

### Step 7: Deterministic Status Workflow State Machine
1. Open the **Transition Status** modal on a `REGISTERED` team.
2. Notice the target dropdown only permits: `APPROVED` or `REJECTED`.
3. Select `APPROVED`, enter audit remarks `"Project proposal meets all hackathon criteria"`, and click **Apply Transition**.
4. *Expected Result:* Status badge transitions to green `Approved`.

### Step 8: Role-Based Access Control (RBAC) Enforcement
1. Log out as `admin`.
2. Log in as `teamlead` (`teamlead` / `Lead@123`).
3. Notice:
   - Delete team button is hidden and forbidden.
   - Team Lead can only edit their own assigned team (`Alex Rivera`).
   - Any attempt to delete another team via API returns `HTTP 403 Forbidden`.

### Step 9: Automated Backend & Isolated Integration Tests
1. Run backend tests in terminal:
   ```bash
   cd backend && mvn clean test
   ```
2. *Expected Result:* 13 tests execute across `TeamServiceTest`, `AuthControllerIntegrationTest`, and `TeamControllerIntegrationTest` with **BUILD SUCCESS (100% pass rate)**.

### Step 10: Selenium Automated Browser Journeys
1. Run Selenium browser test suite:
   ```bash
   cd selenium-tests && mvn test
   ```
2. *Expected Result:* Automated headless Chrome browser executes all 5 critical journeys, asserting login, team creation, search, edit, and status transitions.

### Step 11: Multi-Container Docker Lifecycle
1. Inspect `Dockerfile` and `docker-compose.yml`.
2. Launch multi-tier stack:
   ```bash
   docker compose up -d
   docker compose ps
   ```
3. *Expected Result:* MySQL 8.0, Spring Boot backend, and Nginx frontend all report status `healthy`.

### Step 12: Ansible Configuration Management
1. Review `ansible/inventory.ini` and `ansible/playbook.yml`.
2. Validate syntax:
   ```bash
   ansible-playbook -i ansible/inventory.ini ansible/playbook.yml --syntax-check
   ```
3. Highlight role modularity (`common`, `docker`, `app`, `nginx`) and idempotency.

### Step 13: Live Actuator Health Check Verification
1. Probe health endpoint:
   ```bash
   curl -s http://localhost:8081/actuator/health
   ```
2. *Expected Result:* Returns `{"status":"UP"}` with database and disk space healthy.

### Step 14: Simulated Deployment Failure & Automated Rollback
1. Execute deployment script with an intentionally broken image tag:
   ```bash
   ./deployment/scripts/deploy.sh broken-image-tag 8081
   ```
2. *Expected Result:*
   - Health probe loop detects non-200 / failed status after retries.
   - Script automatically invokes `deployment/scripts/rollback.sh`.
   - Previous stable release (`1.0.0`) is restored and verified healthy.
