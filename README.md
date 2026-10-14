# Dockerized Hackathon Team Management System

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.3-blue.svg)](https://react.dev/)
[![Docker](https://img.shields.io/badge/Docker-Multi--stage-2496ed.svg)](https://www.docker.com/)
[![Ansible](https://img.shields.io/badge/Ansible-Configuration-ee0000.svg)](https://www.ansible.com/)
[![Selenium](https://img.shields.io/badge/Selenium-WebDriver%20JUnit5-43B02A.svg)](https://www.selenium.dev/)
[![Release](https://img.shields.io/badge/Release-v1.0.0-indigo.svg)](https://github.com/)

An end-to-end, production-grade DevOps engineering platform built for academic examination and real-world operations. The system enables hackathon organizers to administer team registrations, manage rosters, govern progress status workflows, analyze participation telemetry in real time, and deploy continuously through automated Jenkins pipelines, Docker containers, and Ansible playbooks.

---

## 1. Features
* **Role-Based Access Control (RBAC):** Dedicated authorization boundaries for `ADMIN`, `ORGANIZER`, and `TEAM_LEAD` powered by Spring Security 6 with BCrypt password hashing.
* **Deterministic Status Workflow:** Enforced state machine preventing illegal status jumps:  
  `DRAFT` &rarr; `REGISTERED` &rarr; `APPROVED` &rarr; `IN_PROGRESS` &rarr; `SUBMITTED` &rarr; `COMPLETED` (or `REJECTED`).
* **Team & Roster Management:** Complete CRUD operations with unique team-code enforcement, track categorization, and dynamic atomic member counts.
* **Backend Search & Dynamic Filtering:** Real-time multi-attribute query engine with pagination and sorting.
* **Operational Dashboard:** Real-time Recharts visualizations showing status progress, track distributions, and recent audit activity.
* **Automated DevOps Lifecycle:** Multi-stage Docker packaging, Docker Compose multi-container orchestration, Jenkins Declarative CI/CD pipeline, JUnit 5 & MockMvc test suites, automated Selenium WebDriver browser journeys, and idempotent Ansible provisioning.

---

## 2. Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend REST API** | Java 21 LTS, Spring Boot 3.3.4, Spring Data JPA, Spring Security 6, Bean Validation, Maven, Spring Boot Actuator |
| **Frontend SPA** | React 18, Vite 5, JavaScript (ES2022), React Router v6, Axios, Recharts, Lucide Icons, Vanilla CSS Design System |
| **Database** | MySQL 8.x (production/docker), In-Memory H2 (automated test isolation) |
| **Reverse Proxy & Web** | Nginx 1.27 Alpine |
| **Test Automation** | JUnit 5, Mockito, MockMvc, Selenium WebDriver 4.x, Selenium Manager |
| **CI / CD Pipeline** | Jenkins 2.x Declarative Pipeline (`Jenkinsfile`), GitHub Actions |
| **Containerization** | Docker, Docker Compose, Multi-stage builds, Named persistent volumes |
| **Configuration Mgmt** | Ansible 2.x (roles: common, docker, app, nginx) |

---

## 3. System Architecture

```text
Browser Client (Port 8081 / Dev 5173)
       │
       ▼
Nginx Reverse Proxy / Static Web Server
  ├── Serves React 18 SPA static assets
  ├── Proxies /api/* requests to Spring Boot
  └── Proxies /actuator/* health probes
       │
       ▼
Spring Boot 3.x Backend (Internal Port 8080 / Local Port 8082)
  ├── Spring Security Filter Chain (RBAC & Sessions)
  ├── REST Controllers & Centralized Exception Handler
  ├── Transactional Service Layer & Workflow State Machine
  └── Spring Data JPA / Hibernate
       │
       ▼
MySQL 8.x Database (Internal Port 3306)
  └── Persistent Docker Named Volume: mysql_data
```

---

## 4. Port Allocations & Conflict Resolution

To avoid port conflicts with services already running on the host system:
* **Jenkins Controller:** Occupies port `8080`.
* **Spring Boot (Local Dev):** Configured to run on port `8082` (avoids port 8080 conflict).
* **React / Vite (Local Dev):** Port `5173`.
* **Nginx Entry Point (Docker):** Host port `8081`.
* **MySQL Database (Docker):** Internal port `3306` on Docker bridge network (unexposed to host for security).

---

## 5. Prerequisites
* **Java:** JDK 21 LTS (`java -version`)
* **Maven:** Apache Maven 3.9+ (`mvn -version`)
* **Node.js:** Node.js v20+ and npm 10+ (`node -v`, `npm -v`)
* **Browser:** Google Chrome (for Selenium automated journeys)
* **Docker & Docker Compose:** Docker Desktop or Docker Engine (for containerized deployment)
* **Ansible:** Ansible 2.15+ (for configuration management on Linux / WSL2)

---

## 6. Local Development Startup

### 6.1 Backend Startup
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```
* Backend starts on `http://localhost:8082`.
* Actuator Health endpoint: `http://localhost:8082/actuator/health`.
* H2 Database Console: `http://localhost:8082/h2-console` (JDBC URL: `jdbc:h2:mem:hackathondb`).

### 6.2 Frontend Startup
```bash
cd frontend
npm install
npm run dev
```
* Frontend starts on `http://localhost:5173`.
* Proxies `/api` and `/actuator` calls to `http://localhost:8082`.

### 6.3 Demo Login Accounts
| Username | Password | Role | Permissions |
| :--- | :--- | :--- | :--- |
| `admin` | `Admin@123` | `ADMIN` | Unrestricted CRUD, delete teams, modify all statuses |
| `organizer` | `Organizer@123` | `ORGANIZER` | Create & update teams, approve/reject/in-progress workflows |
| `teamlead` | `Lead@123` | `TEAM_LEAD` | View teams, update own team, submit for review, manage roster |

---

## 7. Docker Multi-Container Startup

```bash
# 1. Review environment configuration template
cp .env.example .env

# 2. Build multi-stage images and start all containers
docker compose up --build -d

# 3. Verify running containers and health status
docker compose ps

# 4. View container logs
docker compose logs -f

# 5. Stop containers
docker compose down
```
Access the application at: `http://localhost:8081`.

---

## 8. Automated Testing

### 8.1 Backend Unit & Integration Tests (JUnit 5 + MockMvc)
```bash
cd backend
mvn clean test
```
* All 13 tests execute against an isolated in-memory H2 database.
* Reports generated in `backend/target/surefire-reports/`.

### 8.2 Selenium WebDriver Browser Tests
```bash
cd selenium-tests
mvn clean test
```
* Automatically drives headless or headed Google Chrome.
* Covers all 5 critical user journeys:
  1. Login validation (valid vs invalid credentials).
  2. Team creation and listing verification.
  3. Search and dynamic filter verification.
  4. Team metadata update.
  5. Workflow status transition and illegal transition rejection.
* Captures automated PNG screenshots on failure under `selenium-tests/screenshots/`.

---

## 9. Jenkins CI/CD Pipeline

The included `Jenkinsfile` defines a production Declarative Pipeline:
1. **Checkout:** Clones repository source.
2. **Environment Validation:** Verifies Java 21, Maven, Node.js, and Docker availability.
3. **Backend Build & Test:** Compiles code and executes JUnit 5 tests.
4. **Publish Test Reports:** Publishes JUnit XML test results.
5. **Frontend Build:** Runs `npm ci` and `npm run build`.
6. **Selenium Testing:** Runs automated browser tests; failures block deployment.
7. **Docker Build & Tag:** Builds `hackathon-team-management-system:1.0.0`.
8. **Deployment & Health Probe:** Launches containers and probes `/actuator/health`.
9. **Automated Rollback:** Automatically redeploys previous stable tag if health check fails.

---

## 10. Ansible Configuration Management

Ansible playbooks are located in `ansible/`:
```bash
# 1. Test playbook syntax
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml --syntax-check

# 2. Execute idempotent configuration on target host
ansible-playbook -i ansible/inventory.ini ansible/playbook.yml
```

---

## 11. REST API Specification Summary

| Method | Endpoint | Description | Role Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Authenticate and create session | Public |
| `POST` | `/api/auth/logout` | Terminate session | Public |
| `GET` | `/api/auth/me` | Fetch active user profile | Authenticated |
| `GET` | `/api/teams` | Paginated team listing | Authenticated |
| `GET` | `/api/teams/search` | Search teams by keyword | Authenticated |
| `GET` | `/api/teams/{id}` | Team profile and roster | Authenticated |
| `POST` | `/api/teams` | Register new hackathon team | `ADMIN`, `ORGANIZER`, `TEAM_LEAD` |
| `PUT` | `/api/teams/{id}` | Update team metadata | `ADMIN`, `ORGANIZER`, `TEAM_LEAD` |
| `DELETE` | `/api/teams/{id}` | Permanently delete team | `ADMIN` only |
| `PATCH` | `/api/teams/{id}/status` | Enforce status transition | `ADMIN`, `ORGANIZER`, `TEAM_LEAD` |
| `GET` | `/api/teams/{id}/members` | Fetch team member roster | Authenticated |
| `POST` | `/api/teams/{id}/members` | Enroll team member | `ADMIN`, `ORGANIZER`, `TEAM_LEAD` |
| `DELETE` | `/api/teams/{id}/members/{mId}` | Remove team member | `ADMIN`, `ORGANIZER`, `TEAM_LEAD` |
| `GET` | `/api/dashboard/summary` | Aggregate dashboard KPIs | Authenticated |
| `GET` | `/actuator/health` | System health probe | Public |

---

## 12. Troubleshooting
* **Port 8080 already in use:** Local Jenkins is occupying port 8080. The backend is configured to use port `8082` by default. You can override it with `export SERVER_PORT=8085`.
* **Database connectivity:** In standalone local mode, the backend automatically uses in-memory H2. In Docker, it connects to MySQL container on internal network `db:3306`.
* **Selenium Browser Driver:** Selenium 4 uses built-in Selenium Manager to manage Chrome driver binaries automatically. Ensure Google Chrome is installed on the machine.

---

## 13. Project Limitations & Future Work
* **Single-Cluster Deployment:** Architected for single-host or multi-container Compose environments rather than multi-region Kubernetes clusters.
* **Email Verification:** Roster emails are validated via regex format rather than live SMTP token exchange.
* **File Uploads:** Project attachments (presentation decks, GitHub repo links) are currently stored as URL text rather than binary S3/Blob storage.
