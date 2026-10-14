# Production Deployment Guide

**Project:** Dockerized Hackathon Team Management System  
**Platforms:** Linux Server / Docker Host / WSL2  

---

## 1. Production Architecture Overview
In production, all traffic enters through Nginx on host port `8081` (or port `80` if standard HTTP is dedicated to this service). Nginx serves the React Single Page Application (SPA) static files and reverse proxies API traffic to Spring Boot on internal Docker network port `8080`.

```text
Host Port 8081 (Internet / LAN)
       │
       ▼
Nginx Container (frontend:80)
  ├── Static HTML/JS/CSS assets
  ├── Reverse proxy /api/*      ──► Backend Container (backend:8080)
  └── Reverse proxy /actuator/*  ──► Backend Container (backend:8080)
                                        │
                                        └──► MySQL Container (db:3306)
                                              └── mysql_data volume
```

---

## 2. Step-by-Step Deployment Procedure

### Step 1: Clone Repository
```bash
git clone https://github.com/YOUR_ORGANIZATION/hackathon-team-management-system.git /opt/hackathon-team-management-system
cd /opt/hackathon-team-management-system
```

### Step 2: Configure Environment
```bash
cp .env.example .env
chmod 600 .env
nano .env
```
Ensure strong passwords for `MYSQL_PASSWORD` and `MYSQL_ROOT_PASSWORD`, and set `APP_BOOTSTRAP_SEED_DATA=false` for production deployments.

### Step 3: Launch Containers
```bash
docker compose up --build -d
```

### Step 4: Verify Deployment Health
```bash
# Verify container statuses
docker compose ps

# Check Actuator health endpoint
curl -s http://localhost:8081/actuator/health
```
Expected output:
```json
{"status":"UP","components":{"db":{"status":"UP"},"diskSpace":{"status":"UP"},"ping":{"status":"UP"}}}
```

### Step 5: Verify Persistent Storage
1. Log in and create a new hackathon team via UI or API.
2. Restart or recreate the database container:
   ```bash
   docker compose restart db
   ```
3. Refresh the UI and verify that all registered teams, rosters, and audit records remain intact.
