# Problem Definition and MVP Scope

**Project Name:** Dockerized Hackathon Team Management System  
**Document Version:** 1.0.0  
**Date:** October 2026  
**Status:** Approved  

---

## 1. Problem Definition
Hackathons are fast-paced, high-intensity collaborative events where dozens or hundreds of teams conceptualize, build, and submit software and hardware solutions over 24 to 48 hours. Organizing such events requires managing registration, tracking progress across stages (registration, approval, development, submission, evaluation), handling member changes, ensuring proper track/category allocation, and delivering real-time operational insights to organizers and leadership.

Without a centralized, automated system, organizers rely on disparate spreadsheets, chat channels, and manual email exchanges. This leads to duplicate entries, inaccurate team sizes, unsynchronized status updates, security loopholes, lack of visibility into bottlenecks, and high cognitive load during judging and submissions.

## 2. Existing Pain Points
1. **Unsynchronized Team Information:** Discrepancies between participant sign-ups and actual active project teams.
2. **Untracked Progress:** Organizers lack real-time visibility into which teams are actively coding (`IN_PROGRESS`), submitted (`SUBMITTED`), or stalled (`DRAFT`).
3. **Role & Permission Ambiguity:** Unauthorized changes to team rosters or status changes without verified organizer approval.
4. **Manual & Error-Prone Reporting:** Calculating aggregate statistics (participation by track, submission rates) manually under tight deadlines.
5. **Deployment & Environment Inconsistencies:** Fragile manual server deployments causing downtime during live hackathons.

## 3. Target Users & Stakeholders
* **System Administrators (ADMIN):** Manage user accounts, role allocations, system configurations, and overall platform health.
* **Hackathon Organizers (ORGANIZER):** Review registrations, approve or reject teams, allocate tracks, monitor progress across the lifecycle, and view analytics.
* **Team Leads (TEAM_LEAD):** Create and update their team profile, project title, description, track, invite/manage team members, and progress their team status in accordance with the event workflow.
* **Hackathon Evaluators / Judges (Stakeholders):** Rely on accurate submission data and verified team lists for scoring.
* **Academic Reviewers / DevOps Examiners (Stakeholders):** Assess end-to-end software engineering excellence, CI/CD automation, testing, containerization, and configuration management.

## 4. Business Objectives
* **Streamline Operations:** Reduce team onboarding and approval time from hours to seconds.
* **Ensure Data Integrity:** Prevent duplicate team codes, maintain atomic member counts, and enforce strict status transition workflows.
* **Provide Actionable Insights:** Real-time visual dashboard showcasing status distribution, track breakdown, and recent activity.
* **Demonstrate DevOps Maturity:** Deliver a fully automated lifecycle encompassing Git workflows, unit/integration testing, Jenkins CI/CD, Selenium browser automation, Docker containerization, and Ansible configuration management.

## 5. Functional Requirements
* **FR-01 Authentication & Authorization:** Form-based login and logout with role-based access control (ADMIN, ORGANIZER, TEAM_LEAD). Secure server-managed sessions and BCrypt password encryption.
* **FR-02 Team Lifecycle Management:** Full CRUD operations for hackathon teams, including unique team code enforcement, track allocation, project details, and status tracking.
* **FR-03 Team Member Management:** Add, update, view, and remove team members per team with unique email constraints, role tagging within the team, and dynamic member count derivation.
* **FR-04 Status Workflow Enforcement:** Strict state machine governing transitions:  
  `DRAFT` → `REGISTERED` → `APPROVED` → `IN_PROGRESS` → `SUBMITTED` → `COMPLETED` (or `REJECTED`). Disallow illegal transitions.
* **FR-05 Search & Filter:** Backend-powered query engine supporting search by team name, team code, project title, and filtering by registration status and track.
* **FR-06 Dashboard Analytics:** Aggregated metrics for total teams, counts per status, distribution per track, and recently updated teams.
* **FR-07 Health & Observability:** Publicly available readiness/liveness health check endpoint via Spring Boot Actuator.

## 6. Non-Functional Requirements
* **NFR-01 Security:** No plaintext passwords; enforce role-based access on backend API endpoints; secure CSRF and CORS policies; sanitize inputs.
* **NFR-02 Performance:** API response times under 200ms for CRUD and search queries under normal load.
* **NFR-03 Reliability & Fault Tolerance:** Database persistence via Docker volumes; automated health monitoring and rollback support in deployment scripts.
* **NFR-04 Maintainability:** Clean separation of concerns (controller, service, repository, DTO) in Spring Boot; component-driven modular React architecture.
* **NFR-05 Reproducibility:** Single-command local startup via Docker Compose; declarative Ansible playbooks; automated Jenkinsfile.

## 7. Project Constraints & Assumptions
* Single-node or containerized multi-tier deployment; no unnecessary distributed systems (no Kubernetes, Kafka, or microservices).
* Relational database integrity via MySQL 8.x for production/dev and H2 for isolated automated unit/integration testing.
* Frontend served via Nginx reverse-proxying API traffic to Spring Boot.
* Jenkins CI/CD running on host port 8080 (or dedicated agent) without port conflict with Spring Boot (running on port 8082 locally or 8080 internally in container).

## 8. Risks and Mitigations
| Risk | Impact | Likelihood | Mitigation Strategy |
| :--- | :--- | :--- | :--- |
| Port 8080 conflict with existing Jenkins | High | High | Configure local Spring Boot to port 8082, Docker internal port 8080 mapped via Nginx on port 8081. |
| Test database contamination | High | Medium | Use in-memory H2 database with test profile for integration and unit testing. |
| Flaky Selenium browser tests | Medium | Medium | Use explicit WebDriverWait instead of hardcoded sleeps; deterministic seeded test data. |
| Container drift during deployments | High | Low | Versioned Docker image tagging (e.g., `1.0.0`) and automated health checks with rollback scripts. |

## 9. Measurable Success Criteria
1. All backend CRUD and search endpoints respond with appropriate HTTP status codes (200, 201, 204, 400, 401, 403, 404, 409).
2. Unauthorized role attempts (e.g. TEAM_LEAD deleting other teams) return HTTP 403 Forbidden.
3. Dashboard statistics accurately reflect real database counts.
4. Backend JUnit 5 test suite passes with 100% success rate.
5. Selenium WebDriver automation successfully runs all 5 critical user journeys.
6. Docker Compose boots MySQL, Spring Boot backend, and React/Nginx frontend with passing health checks.
7. Jenkins pipeline executes lint, build, test, package, and deploy stages deterministically.
8. Ansible playbook executes idempotently across repeat runs.
9. Rollback mechanism successfully recovers to the previous stable release upon simulated deployment failure.

## 10. Approved MVP Scope vs Out of Scope
### In MVP Scope:
* User authentication and role-based authorization (ADMIN, ORGANIZER, TEAM_LEAD).
* Team CRUD, member management, and status state machine.
* Multi-criteria backend search and filtering.
* Visual analytics dashboard with charts.
* Containerized multi-container setup (Nginx + React, Spring Boot, MySQL).
* Automated CI/CD Jenkinsfile, Selenium tests, and Ansible playbooks.

### Out of Scope for MVP:
* Real-time WebSocket notifications.
* Direct payment gateway integration for registration fees.
* Native mobile applications (iOS/Android).
* Multi-region cloud Kubernetes clustering.
