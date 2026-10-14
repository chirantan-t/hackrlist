# Sprint Plan

**Project:** Dockerized Hackathon Team Management System  
**Sprint Cycle:** 3 Iterative Sprints across 15 DevOps Tasks  

---

## Sprint 1: Foundation, Core Application & Version Control (Tasks 1 - 6)
* **Goal:** Deliver a working, tested full-stack MVP application with Git versioning and branch protection.
* **Scope:**
  - Task 1: Problem Definition and MVP Scope.
  - Task 2: Agile Planning and DevOps Workflow.
  - Task 3: Requirements, Architecture, and Technology Setup.
  - Task 4: Git and GitHub Repository Initialization (.gitignore, README, templates, branching strategy).
  - Task 5: Feature Branch Development: Team Registration, Entity mapping, REST APIs, and UI listing.
  - Task 6: Complete MVP: Member management, Search/Filter, Status state machine, Analytics Dashboard, and merge conflict resolution simulation.
* **Sprint Deliverable:** Verified full-stack Spring Boot + React application running locally with passing JUnit tests and Git release tag `v1.0.0`.

---

## Sprint 2: Automated Testing, Containerization & CI Pipeline (Tasks 7 - 11)
* **Goal:** Containerize the full-stack system and establish continuous integration with automated browser testing.
* **Scope:**
  - Task 7: Jenkins Installation and Continuous Integration setup.
  - Task 8: Pipeline as Code (Jenkinsfile) and Server Deployment scripts (Nginx reverse proxy).
  - Task 9: Selenium WebDriver test suite covering 5 critical user journeys with screenshot capture.
  - Task 10: Continuous Testing in Jenkins: test report publishing, failure gating, and defect correction demonstration.
  - Task 11: Production-ready multi-stage Docker containerization and Docker Compose orchestration.
* **Sprint Deliverable:** Multi-container application running under Docker Compose with passing Jenkins CI builds and Selenium tests.

---

## Sprint 3: Continuous Deployment, Configuration Management & Reliability (Tasks 12 - 15)
* **Goal:** Implement automated deployment, Ansible configuration management, health verification, and rollback recovery.
* **Scope:**
  - Task 12: Jenkins-Docker Continuous Deployment with automated version tagging and rollback on health failure.
  - Task 13: Ansible Configuration Management: Playbooks, roles (common, docker, app, nginx), and variables.
  - Task 14: Automated Provisioning & Reliability Validation: Idempotent runs, health checks, and simulated failure recovery.
  - Task 15: Final Integration, Documentation, and Demonstration: Final project report, demo script, and evidence checklist.
* **Sprint Deliverable:** Complete academic DevOps demonstration package with full documentation, verified deployment, and disaster recovery.
