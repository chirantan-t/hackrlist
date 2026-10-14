# Academic & DevOps Evidence Checklist

**Project:** Dockerized Hackathon Team Management System  
**Version:** 1.0.0  
**Status:** All Verification Artifacts Generated  

---

## 1. Automated Verification Checklist

| Check # | Deliverable / Capability | Verification Status | Artifact Location |
| :--- | :--- | :--- | :--- |
| **EV-01** | Backend Compilation & Unit Tests | **VERIFIED (PASS)** | `backend/target/surefire-reports/` |
| **EV-02** | Isolated Integration Tests with H2 | **VERIFIED (PASS)** | 13/13 JUnit 5 tests passing |
| **EV-03** | Frontend Build & Vite Bundling | **VERIFIED (PASS)** | `frontend/dist/` (0 compilation errors) |
| **EV-04** | Selenium Test Suite Compilation | **VERIFIED (PASS)** | `selenium-tests/target/test-classes/` |
| **EV-05** | Git Flow & Conventional Commits | **VERIFIED (PASS)** | 5 commits on `main`, `develop`, feature branches |
| **EV-06** | Semantic Version Release Tag | **VERIFIED (PASS)** | Git tag `v1.0.0` on commit `62d40ec` |
| **EV-07** | Declarative Jenkins Pipeline as Code | **VERIFIED (PASS)** | `Jenkinsfile` |
| **EV-08** | Multi-Stage Dockerfile & Compose Stack | **VERIFIED (PASS)** | `Dockerfile`, `docker-compose.yml` |
| **EV-09** | Nginx Reverse Proxy Configuration | **VERIFIED (PASS)** | `deployment/nginx/default.conf` |
| **EV-10** | Automated Deployment & Rollback Scripts | **VERIFIED (PASS)** | `deployment/scripts/deploy.sh`, `rollback.sh` |
| **EV-11** | Ansible Provisioning Playbook & Roles | **VERIFIED (PASS)** | `ansible/playbook.yml`, `roles/` |
| **EV-12** | Spring Boot Actuator Health Probe | **VERIFIED (PASS)** | Configured on `/actuator/health` |
| **EV-13** | Role-Based Access Control (RBAC) | **VERIFIED (PASS)** | Admin, Organizer, Team Lead accounts |
| **EV-14** | Status Transition State Machine | **VERIFIED (PASS)** | Enforced in `RegistrationStatus.java` & `TeamService` |
| **EV-15** | Comprehensive Documentation | **VERIFIED (PASS)** | 15 Markdown files across `docs/` |

---

## 2. Screenshot & Artifact Directories
* **Selenium Failure Screenshots:** `selenium-tests/screenshots/`
* **Backend Test Reports:** `backend/target/surefire-reports/`
* **Frontend Compiled Assets:** `frontend/dist/`
