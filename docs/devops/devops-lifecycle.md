# DevOps Lifecycle & Continuous Workflow

**Project:** Dockerized Hackathon Team Management System  
**Framework:** Continuous Integration, Continuous Delivery & Reliability Engineering  

---

## 1. The 10-Phase DevOps Lifecycle

The project follows the standard closed-loop DevOps lifecycle:

```mermaid
flowchart LR
    PLAN[1. PLAN] --> CODE[2. CODE]
    CODE --> BUILD[3. BUILD]
    BUILD --> TEST[4. TEST]
    TEST --> PACKAGE[5. PACKAGE]
    PACKAGE --> RELEASE[6. RELEASE]
    RELEASE --> DEPLOY[7. DEPLOY]
    DEPLOY --> OPERATE[8. OPERATE]
    OPERATE --> MONITOR[9. MONITOR]
    MONITOR --> FEEDBACK[10. FEEDBACK]
    FEEDBACK --> PLAN
```

### Detailed Lifecycle Phases
1. **PLAN:** Agile backlog grooming, Jira/Kanban task mapping, user stories with acceptance criteria, and 15-task scope freezing.
2. **CODE:** Modular Spring Boot 3.x backend, React/Vite component frontend, feature branching (`feature/*`), Git conventional commits, and peer code reviews.
3. **BUILD:** Maven compilation, dependency resolution, frontend bundle creation via Vite, and Docker multi-stage artifact extraction.
4. **TEST:** Comprehensive testing pyramid: JUnit 5 unit tests, Spring MockMvc API integration tests, and Selenium WebDriver browser automation for critical user journeys.
5. **PACKAGE:** Multi-stage Docker image creation (`Dockerfile`), layering Nginx reverse proxy with static assets, and packaging Spring Boot executable jar with pinned Java 21 runtime.
6. **RELEASE:** Semantic version tagging (`v1.0.0`), Docker image tagging (`hackathon-team-management-system:1.0.0` and `:latest`), and release artifact archiving.
7. **DEPLOY:** Automated zero-downtime deployment via Jenkins CD and Ansible configuration management playbooks on target hosts.
8. **OPERATE:** Containerized runtime orchestration via Docker Compose, isolated internal bridge networks, persistent database volumes, and non-root process security.
9. **MONITOR:** Continuous health probing via Spring Boot Actuator (`/actuator/health`), container status inspection, and Nginx access/error logging.
10. **FEEDBACK:** Automated deployment health verification, rollback trigger upon failed readiness probes, and test reporting back to developers.

---

## 2. CI/CD Pipeline Architecture & Flow

```mermaid
sequenceDiagram
    autonumber
    actor Dev as Developer
    participant Git as Git/GitHub Repo
    participant Jenkins as Jenkins CI/CD Controller
    participant Maven as Maven / Test Agent
    participant Selenium as Selenium Headless Chrome
    participant Docker as Docker Daemon / Registry
    participant Target as Production Server (Nginx + Compose)

    Dev->>Git: git push origin feature/xyz
    Dev->>Git: Pull Request -> develop / main
    Git->>Jenkins: SCM Webhook / Poll Trigger
    Jenkins->>Git: Clone repository (checkout)
    Jenkins->>Maven: Compile & Run JUnit Tests
    alt Tests Fail
        Maven-->>Jenkins: Test Failure Reported
        Jenkins-->>Git: Mark Commit Failed (Block Deployment)
    else Tests Pass
        Maven-->>Jenkins: Test Success (JUnit XML Report)
        Jenkins->>Selenium: Execute 5 Critical User Journeys
        Selenium-->>Jenkins: Selenium Tests Pass
        Jenkins->>Docker: Build Multi-stage Image (tag v1.0.0)
        Jenkins->>Docker: Push Image / Stage Artifact
        Jenkins->>Target: Trigger Deployment / Ansible Playbook
        Target->>Target: Start Container & Run Health Check (/actuator/health)
        alt Health Check Fails
            Target-->>Jenkins: Health Probe Timeout (HTTP 500 / Down)
            Jenkins->>Target: Auto-Rollback to Previous Stable Tag
        else Health Check Succeeds
            Target-->>Jenkins: HTTP 200 OK (Application UP)
            Jenkins-->>Dev: Pipeline Succeeded (Notification)
        end
    end
```

---

## 3. End-to-End System Deployment Topology

```mermaid
graph TD
    subgraph Client ["Client Browser Layer"]
        User["Organizer / Admin / Team Lead"]
    end

    subgraph Host ["Target Deployment Host (Host Port 8081 / 8082)"]
        subgraph NginxProxy ["Reverse Proxy Container (Port 8081)"]
            Nginx["Nginx 1.27 Alpine"]
            StaticFiles["React 18 SPA Static Assets"]
        end

        subgraph BackendApp ["Spring Boot Backend Container (Internal Port 8080)"]
            SpringBoot["Spring Boot 3.x (Java 21)"]
            Actuator["Spring Actuator (/actuator/health)"]
            Security["Spring Security (RBAC + BCrypt)"]
        end

        subgraph Database ["Database Container (Internal Port 3306)"]
            MySQL["MySQL 8.0 Engine"]
            Volume[("Persistent Docker Volume: mysql_data")]
        end
    end

    User -->|HTTP Requests on Port 8081| Nginx
    Nginx -->|Serves UI /| StaticFiles
    Nginx -->|Proxies /api/* & /actuator/*| SpringBoot
    SpringBoot --> Security
    SpringBoot --> Actuator
    SpringBoot -->|Internal Network TCP 3306| MySQL
    MySQL --- Volume
```
