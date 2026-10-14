# Product Backlog

**Project:** Dockerized Hackathon Team Management System  
**Methodology:** Agile / Scrum with Kanban Workflow  
**Iteration:** 15 Top-Level Engineering Tasks  

---

## 1. User Stories

### US-01: User Authentication & Role Detection
* **User Story:** As an organizer, admin, or team lead, I want to securely log in with my credentials and role so that I can access features authorized for my role.
* **Acceptance Criteria:**
  - System verifies email/username and encrypted password via BCrypt.
  - Server returns authenticated user details (username, role, full name).
  - Invalid credentials return HTTP 401 Unauthorized with meaningful error message.
  - User session is established and destroyed upon logout.

### US-02: Team Registration & Profile Creation
* **User Story:** As a team lead or organizer, I want to register a new hackathon team with project title, track, and description so that our project is officially entered into the event.
* **Acceptance Criteria:**
  - Team name and unique team code are mandatory and validated.
  - Duplicate team codes are rejected with HTTP 409 Conflict.
  - Initial registration status defaults to `DRAFT` or `REGISTERED`.
  - Created and updated timestamps are automatically recorded.

### US-03: Team Member Roster Management
* **User Story:** As a team lead, I want to add, edit, and remove team members so that our team composition accurately reflects our active contributors.
* **Acceptance Criteria:**
  - Members include name, valid email, college, department, and team role.
  - Prevent duplicate member emails within the same team.
  - Team member count dynamically matches actual membership records.
  - Member deletion is safe and cascades correctly.

### US-04: Role-Aware Status Workflow
* **User Story:** As an organizer, I want to review registered teams and transition them through approved workflow states (`REGISTERED` → `APPROVED` → `IN_PROGRESS` → `SUBMITTED` → `COMPLETED` or `REJECTED`) so that teams only progress when criteria are met.
* **Acceptance Criteria:**
  - Illegal status jumps (e.g. `DRAFT` directly to `COMPLETED`) are rejected with HTTP 400 Bad Request.
  - Non-authorized roles cannot approve or reject teams.
  - Every status change updates the `updatedAt` audit timestamp.

### US-05: Multi-Criteria Team Search & Filtering
* **User Story:** As an organizer or admin, I want to search teams by name, code, or project title, and filter by status and track, so that I can rapidly find specific cohorts during the event.
* **Acceptance Criteria:**
  - Query executed on backend with SQL/JPA filtering.
  - Case-insensitive substring matching for name, code, and project title.
  - Supports combining track and status filters with search terms.

### US-06: Operational Analytics Dashboard
* **User Story:** As a hackathon administrator, I want to view aggregate summary statistics and visual charts so that I can assess event health at a glance.
* **Acceptance Criteria:**
  - Total teams, approved teams, active teams, and submission counts displayed in KPI cards.
  - Visual charts showing distribution across tracks and statuses.
  - Recent team updates table with direct links to team details.

### US-07: Automated CI/CD & Reliability Validation
* **User Story:** As a DevOps engineer, I want automated testing, Docker containerization, Jenkins CI/CD, and Ansible provisioning so that software releases are continuous, reproducible, and verifiable.
* **Acceptance Criteria:**
  - Maven unit and integration tests run automatically before packaging.
  - Selenium browser tests execute critical user journeys.
  - Multi-stage Docker builds generate minimal production images.
  - Ansible ensures idempotent server configuration and health verification.

---

## 2. Exactly 15 Top-Level Engineering Tasks

| Task # | Task Title | Priority | Dependencies | Status | Deliverables |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Task 1** | Problem Definition and MVP Scope | High | None | DONE | `docs/requirements/problem-statement.md` |
| **Task 2** | Agile Planning and DevOps Workflow | High | Task 1 | DONE | Backlog, Task Board, Sprint Plan, DoD, Lifecycle docs |
| **Task 3** | Requirements, Architecture, and Technology Setup | High | Task 2 | DONE | SRS summary, architecture diagrams, data models, API docs |
| **Task 4** | Git and GitHub Repository Initialization | High | Task 3 | READY | Git repo, .gitignore, README.md, issue/PR templates |
| **Task 5** | Feature Branch Development (Team Registration & Listing) | High | Task 4 | IN PROGRESS | Entities, Repositories, Services, Controllers, UI list & form |
| **Task 6** | Complete MVP and Git Collaboration | High | Task 5 | IN PROGRESS | Full CRUD, Member management, Status workflow, Dashboard |
| **Task 7** | Jenkins Installation and Continuous Integration | High | Task 6 | READY | Jenkinsfile (build, test, package, JUnit reporting) |
| **Task 8** | Pipeline as Code and Server Deployment | High | Task 7 | READY | Production Jenkinsfile, Nginx reverse proxy configs |
| **Task 9** | Selenium Test Design and Local Execution | High | Task 6 | READY | Java/Maven JUnit 5 Selenium suite (5 user journeys) |
| **Task 10** | Continuous Testing in Jenkins | High | Task 8, 9 | READY | Automated test pipeline execution, failure simulation & fix |
| **Task 11** | Docker Image and Container Lifecycle | High | Task 6 | READY | Multi-stage Dockerfile, docker-compose.yml, volumes, health checks |
| **Task 12** | Jenkins-Docker Continuous Deployment | High | Task 10, 11 | READY | Automated image tagging, container deployment, rollback logic |
| **Task 13** | Ansible Configuration Management | High | Task 11 | READY | Playbook, inventory, roles (common, docker, app, nginx) |
| **Task 14** | Automated Provisioning and Reliability Validation | High | Task 13 | READY | Idempotency validation, health verification, rollback demonstration |
| **Task 15** | Final Integration, Documentation, and Demonstration | High | Task 1-14 | READY | Final report, demo script, evidence checklist, troubleshooting guide |
