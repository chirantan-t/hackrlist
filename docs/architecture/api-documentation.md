# RESTful API Documentation & Contracts

**Base URL:** `/api`  
**Authentication:** Form/JSON session login with Cookie or Bearer authorization  
**Health Check:** `/actuator/health`  

---

## 1. Authentication Endpoints

### 1.1 POST `/api/auth/login`
* **Description:** Authenticates user credentials and establishes user session.
* **Request Body:**
  ```json
  {
    "username": "admin",
    "password": "Admin@123"
  }
  ```
* **Success Response (200 OK):**
  ```json
  {
    "id": 1,
    "username": "admin",
    "fullName": "System Administrator",
    "email": "admin@hackathon.dev",
    "role": "ADMIN"
  }
  ```
* **Error Response (401 Unauthorized):**
  ```json
  {
    "status": 401,
    "error": "Unauthorized",
    "message": "Invalid username or password",
    "timestamp": "2026-10-14T16:30:00Z"
  }
  ```

### 1.2 POST `/api/auth/logout`
* **Description:** Invalidates the current user session and security context.
* **Success Response (200 OK):**
  ```json
  {
    "message": "Successfully logged out"
  }
  ```

### 1.3 GET `/api/auth/me`
* **Description:** Returns the currently authenticated user's profile and active role.
* **Success Response (200 OK):** User summary object.
* **Error Response (401 Unauthorized):** When session is missing or expired.

---

## 2. Team Endpoints

### 2.1 GET `/api/teams`
* **Description:** Returns a list of teams with optional filtering, sorting, and pagination.
* **Query Parameters:**
  - `page` (integer, default: 0)
  - `size` (integer, default: 10)
  - `status` (string, optional: `REGISTERED`, `APPROVED`, etc.)
  - `track` (string, optional: `AI_ML`, `FINTECH`, etc.)
  - `sort` (string, default: `createdAt,desc`)
* **Success Response (200 OK):** Paginated JSON response with team list, totalElements, totalPages.

### 2.2 GET `/api/teams/search`
* **Description:** Searches teams by keyword matching team name, team code, or project title.
* **Query Parameters:**
  - `q` (string, required query keyword)
  - `status` (optional filter)
  - `track` (optional filter)
* **Success Response (200 OK):** Filtered list of team objects.

### 2.3 GET `/api/teams/{id}`
* **Description:** Returns full details of a specific team, including member roster.
* **Success Response (200 OK):** Full team object with member list.
* **Error Response (404 Not Found):** Team ID does not exist.

### 2.4 POST `/api/teams`
* **Description:** Registers a new hackathon team.
* **Roles Permitted:** `ADMIN`, `ORGANIZER`, `TEAM_LEAD`
* **Request Body:**
  ```json
  {
    "teamName": "Neural Hackers",
    "teamCode": "HTM-AI-01",
    "projectTitle": "Autonomous Vision Agent",
    "projectDescription": "Edge AI camera analyzing traffic flow in real time.",
    "teamLead": "chirantan",
    "track": "AI_ML"
  }
  ```
* **Success Response (201 Created):** Created team object.
* **Error Response (409 Conflict):** Team code already exists.

### 2.5 PUT `/api/teams/{id}`
* **Description:** Updates existing team metadata (name, project title, description, track).
* **Roles Permitted:** `ADMIN`, `ORGANIZER`, or `TEAM_LEAD` (matching team lead)
* **Success Response (200 OK):** Updated team object.

### 2.6 DELETE `/api/teams/{id}`
* **Description:** Permanently deletes a team and cascades to its members.
* **Roles Permitted:** `ADMIN` only.
* **Success Response (204 No Content).**
* **Error Response (403 Forbidden):** If attempted by non-admin.

### 2.7 PATCH `/api/teams/{id}/status`
* **Description:** Advances or alters team status according to state machine rules.
* **Roles Permitted:** `ADMIN`, `ORGANIZER` (or `TEAM_LEAD` for initial submission).
* **Request Body:**
  ```json
  {
    "status": "APPROVED",
    "remarks": "Project meets eligibility criteria"
  }
  ```
* **Success Response (200 OK):** Updated team object with new status.
* **Error Response (400 Bad Request):** If transition violates workflow (e.g. `DRAFT` directly to `COMPLETED`).

---

## 3. Team Member Endpoints

### 3.1 GET `/api/teams/{id}/members`
* **Description:** Returns all members belonging to the specified team.
* **Success Response (200 OK):** Array of team member objects.

### 3.2 POST `/api/teams/{id}/members`
* **Description:** Enrolls a new member into the team.
* **Request Body:**
  ```json
  {
    "fullName": "Sarah Connor",
    "email": "sarah.connor@mit.edu",
    "college": "MIT",
    "department": "Computer Science",
    "teamRole": "DATA_SCIENTIST"
  }
  ```
* **Success Response (201 Created):** Created member object.
* **Error Response (409 Conflict):** Member with this email is already enrolled in this team.

### 3.3 PUT `/api/teams/{id}/members/{memberId}`
* **Description:** Updates a team member's role or department.
* **Success Response (200 OK):** Updated member object.

### 3.4 DELETE `/api/teams/{id}/members/{memberId}`
* **Description:** Removes a member from the team roster.
* **Success Response (204 No Content).**

---

## 4. Dashboard Analytics Endpoints

### 4.1 GET `/api/dashboard/summary`
* **Description:** Returns aggregate KPI counts.
* **Success Response (200 OK):**
  ```json
  {
    "totalTeams": 24,
    "draftTeams": 2,
    "registeredTeams": 6,
    "approvedTeams": 8,
    "inProgressTeams": 5,
    "submittedTeams": 2,
    "completedTeams": 1,
    "rejectedTeams": 0,
    "totalMembers": 96
  }
  ```

### 4.2 GET `/api/dashboard/status-distribution`
* **Description:** Returns breakdown array for status pie chart.

### 4.3 GET `/api/dashboard/track-distribution`
* **Description:** Returns breakdown array for track bar chart.

---

## 5. System Observability & Health

### 5.1 GET `/actuator/health`
* **Description:** Public health endpoint utilized by Docker healthchecks, Jenkins, and Nginx probes.
* **Success Response (200 OK):**
  ```json
  {
    "status": "UP",
    "components": {
      "db": { "status": "UP" },
      "diskSpace": { "status": "UP" },
      "ping": { "status": "UP" }
    }
  }
  ```
