# Software Requirements Specification (SRS) Summary

**Project:** Dockerized Hackathon Team Management System  
**Version:** 1.0.0  
**Compliance Standard:** IEEE 830 / ISO 25010  

---

## 1. Scope & System Purpose
The Dockerized Hackathon Team Management System is an enterprise-grade academic web application designed for hackathon organizers to administer team registrations, manage rosters, govern progress status workflows, analyze participation demographics, and operate reliably across automated DevOps CI/CD pipelines.

## 2. User Classes and Characteristics
| User Role | Responsibilities | Access Scope |
| :--- | :--- | :--- |
| **ADMIN** | Full system administration, user management, and operational oversight. | Unrestricted access: create, view, update, delete any team; modify team statuses; manage all rosters; view full analytics. |
| **ORGANIZER** | Hackathon operations, team evaluation, track assignment, and workflow transitions. | Create teams, view and update all teams, execute authorized status transitions (Approve, In Progress, Submit, Complete, Reject), manage rosters, view analytics. Cannot delete teams or alter user accounts. |
| **TEAM_LEAD** | Team project execution and member coordination. | View team roster, edit project description/title, add/remove members for their own team, submit team for approval (`REGISTERED` -> `SUBMITTED`), view dashboard. |

## 3. Detailed Functional Requirements

### 3.1 Authentication & Security (SEC)
* **SEC-1:** System must support login with username and password.
* **SEC-2:** Passwords must be hashed using BCrypt (strength 10 or higher). Plaintext passwords must never be stored or logged.
* **SEC-3:** Authentication state must be maintained securely via HTTP sessions with `HttpOnly` and `SameSite` protections.
* **SEC-4:** Logout endpoint must invalidate the HTTP session and clear security context.
* **SEC-5:** Access to protected endpoints (`/api/**`) must enforce role permissions via Spring Security.

### 3.2 Team Management (TM)
* **TM-1:** Create team with name, unique team code, project title, project description, team lead name, and track/category.
* **TM-2:** Duplicate team codes must be rejected with HTTP 409 Conflict.
* **TM-3:** Read team details by ID or unique code.
* **TM-4:** Update permitted team metadata (project title, description, track).
* **TM-5:** Delete team (restricted to ADMIN role). Cascading removal of associated team members.
* **TM-6:** Filter teams by status (`DRAFT`, `REGISTERED`, `APPROVED`, `IN_PROGRESS`, `SUBMITTED`, `COMPLETED`, `REJECTED`) and track (`AI_ML`, `WEB3_BLOCKCHAIN`, `FINTECH`, `HEALTHTECH`, `OPEN_INNOVATION`).
* **TM-7:** Search teams via backend query on team name, team code, and project title.

### 3.3 Team Member Management (TMM)
* **TMM-1:** Add member with full name, email, college, department, and role in team (`FRONTEND`, `BACKEND`, `FULLSTACK`, `UI_UX`, `DATA_SCIENTIST`, `DEVOPS`).
* **TMM-2:** Validate email format; prevent duplicate emails in the same team.
* **TMM-3:** Team member count must dynamically reflect the active count of members in the team.
* **TMM-4:** Update and remove members with proper authorization.

### 3.4 Status Transition State Machine (WF)
* **WF-1:** The status workflow follows the deterministic state model:  
  `DRAFT` -> `REGISTERED` -> `APPROVED` -> `IN_PROGRESS` -> `SUBMITTED` -> `COMPLETED`.
* **WF-2:** `REJECTED` state can only be assigned from `REGISTERED` or `SUBMITTED` by an `ORGANIZER` or `ADMIN`.
* **WF-3:** Any attempt to skip workflow states (e.g., `DRAFT` -> `COMPLETED`) must be rejected with HTTP 400 Bad Request.

### 3.5 Dashboard Analytics (DASH)
* **DASH-1:** Aggregate count of total teams.
* **DASH-2:** Team counts categorized by each status (`APPROVED`, `IN_PROGRESS`, etc.).
* **DASH-3:** Team distribution broken down by track.
* **DASH-4:** Recent updates list showing the 5 most recently updated teams.

## 4. Non-Functional Requirements
* **Performance:** Maximum 250ms latency for 95% of standard REST API queries.
* **Data Integrity:** Foreign key integrity between teams and members; transactional rollback on error.
* **Containerization:** Self-contained Docker Compose deployment with MySQL volume persistence.
* **Auditability:** Timestamps (`createdAt`, `updatedAt`) persisted on every database record.
