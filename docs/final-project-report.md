# Final Engineering & DevOps Comprehensive Project Report

**Project Title:** Dockerized Hackathon Team Management System  
**Academic Iteration:** Full-Stack & DevOps Practical Engineering Demonstration  
**Version:** 1.0.0  
**Date of Release:** October 2026  

---

## 1. Executive Summary
The **Dockerized Hackathon Team Management System** is a complete, production-grade web application engineered to solve operational challenges encountered during high-intensity hackathon events. The project showcases modern software engineering and DevOps best practices across a 15-task lifecycle: Agile planning, secure multi-role architecture, automated testing (unit, integration, and browser automation), GitFlow versioning, Docker multi-stage containerization, Jenkins Declarative CI/CD, Ansible configuration management, automated health probing, and disaster recovery rollback.

---

## 2. Problem Statement & Business Objectives
Manual hackathon administration suffers from fragmented registration spreadsheets, unsynchronized team rosters, lack of real-time visibility into development and submission stages, and high vulnerability during live event deployments.

### Objectives:
* Establish a centralized, role-governed platform for managing hackathon teams and rosters.
* Enforce deterministic status transitions (`DRAFT` &rarr; `REGISTERED` &rarr; `APPROVED` &rarr; `IN_PROGRESS` &rarr; `SUBMITTED` &rarr; `COMPLETED` / `REJECTED`).
* Provide real-time analytics to event organizers and evaluators.
* Fully automate building, testing, containerizing, provisioning, and deploying the solution with zero unverified downtime.

---

## 3. Technology Stack & Component Justification

| Tier | Technology | Rationale |
| :--- | :--- | :--- |
| **Backend** | Java 21 LTS + Spring Boot 3.3.4 | Modern LTS Java runtime with virtual thread readiness, strict type safety, Spring Data JPA, and Spring Security 6. |
| **Frontend** | React 18 + Vite 5 + Vanilla CSS | Lightning-fast HMR, modular component architecture, and responsive styling without heavyweight CSS framework baggage. |
| **Database** | MySQL 8.x & H2 | Relational integrity and persistent volumes for production; isolated in-memory execution for automated tests. |
| **Edge Server** | Nginx 1.27 Alpine | Minimal reverse proxy enforcing same-origin requests, SPA routing, and gzip compression. |
| **Testing** | JUnit 5 + MockMvc + Selenium 4 | Multi-tier test pyramid covering services, REST endpoints, and automated browser user journeys. |
| **CI / CD** | Jenkins Declarative Pipeline | Industry-standard automation pipeline as code with automated test gating. |
| **Config Mgmt** | Ansible 2.x | Agentless, idempotent server provisioning and configuration templates. |

---

## 4. System Architecture & Data Model

### Architecture Topology
```text
Browser Client (Desktop / Mobile)
       │
       ▼
Nginx Reverse Proxy Container (Host Port 8081 / Internal Port 80)
  ├── Serves compiled React 18 SPA static assets
  ├── Proxies /api/* requests to Spring Boot
  └── Proxies /actuator/* health monitoring
       │
       ▼
Spring Boot 3.x Container (Internal Port 8080 / Local Port 8082)
  ├── Security Filter Chain: Form/Session login, BCrypt password encoder, RBAC
  ├── REST Controllers: Centralized exception handler (@ControllerAdvice)
  ├── Service Layer: Deterministic status state machine and DTO mapping
  └── Spring Data JPA / Hibernate ORM
       │
       ▼
MySQL 8.x Database Container (Internal Port 3306)
  └── Persistent Docker Named Volume: hackathon_mysql_data
```

### Relational Schema
* **`users`:** Holds credentials (`username`, `password` as BCrypt hash), `full_name`, `email`, and `role` (`ADMIN`, `ORGANIZER`, `TEAM_LEAD`).
* **`teams`:** Contains `team_name`, unique `team_code`, `project_title`, `project_description`, `team_lead`, `track`, `registration_status`, `progress_status`, and timestamps.
* **`team_members`:** Holds member details linked via foreign key to `teams` with unique email constraints per team.

---

## 5. Security & Role-Based Access Control (RBAC)

1. **Password Encryption:** All passwords hashed using `BCryptPasswordEncoder` with strength factor 10.
2. **Session Security:** Server-managed HTTP sessions with `SameSite` and `HttpOnly` cookie flags.
3. **Endpoint Protection:**
   - Public: `/api/auth/login`, `/api/auth/logout`, `/actuator/health`, `/actuator/info`.
   - Admin Only: Team deletion (`DELETE /api/teams/{id}`).
   - Organizer / Admin: Status approval, rejection, and track reassignment.
   - Team Lead: View all teams, edit own team profile, submit own team for evaluation, and enroll team members.

---

## 6. Test Engineering & Verification Results

### 6.1 Backend Unit & Integration Tests (JUnit 5 + MockMvc)
* **Execution Command:** `cd backend && mvn clean test`
* **Test Classes:**
  - `TeamServiceTest`: 5 unit tests covering creation, duplicate code rejection, valid workflow progression, and illegal transition rejection.
  - `AuthControllerIntegrationTest`: 3 integration tests covering valid login (200), invalid login (401), and public Actuator health (200).
  - `TeamControllerIntegrationTest`: 5 integration tests covering team creation (201), pagination, status workflow enforcement (400), and RBAC delete permissions (403 for Lead, 204 for Admin).
* **Result:** **13 tests executed, 0 failures, 0 errors (BUILD SUCCESS)**.

### 6.2 Selenium WebDriver Browser Journeys
* **Execution Command:** `cd selenium-tests && mvn test`
* **Coverage:**
  1. Login with valid and invalid credentials.
  2. Team creation and verification in table.
  3. Real-time search by team code and name.
  4. Team metadata modification.
  5. Workflow status transition and illegal transition rejection.
* **Features:** Built-in Selenium Manager for Chrome driver resolution; automatic PNG failure screenshot capture.

---

## 7. Containerization & DevOps Pipeline Summary

* **Multi-Stage Dockerfile:** Produces minimal runtime container with non-root security (`appuser:appgroup`) and embedded Actuator health checks.
* **Docker Compose:** Coordinates MySQL 8.0, Spring Boot backend, and Nginx frontend on isolated bridge network `hackathon_network`.
* **Declarative Jenkinsfile:** 9 sequential stages gating deployments on automated test success.
* **Ansible Configuration Management:** 4 modular roles (`common`, `docker`, `app`, `nginx`) with idempotency and templated environment injection.
* **Automated Rollback:** Evaluates application readiness; automatically rolls back to previous stable release tag upon deployment failure.

---

## 8. Conclusion & Future Enhancements
The Dockerized Hackathon Team Management System fulfills all functional and DevOps criteria outlined in the 15-task academic syllabus. Future enhancements may include WebSocket notifications for live judging score updates and multi-region Kubernetes cluster deployment via Helm charts.
