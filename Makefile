# ===================================================================
# Dockerized Hackathon Team Management System - Automation Makefile
# ===================================================================

.PHONY: help setup backend frontend test test-selenium docker-build docker-up docker-down clean

help:
	@echo "Hackathon Team Management System - Build & Operation Targets:"
	@echo "  make setup          - Install dependencies and build all components"
	@echo "  make backend        - Run Spring Boot backend locally (port 8082)"
	@echo "  make frontend       - Run React Vite dev server (port 5173)"
	@echo "  make test           - Execute backend unit and integration tests"
	@echo "  make test-selenium  - Run Selenium WebDriver automated browser tests"
	@echo "  make docker-build   - Build production multi-stage Docker image"
	@echo "  make docker-up      - Launch multi-container stack via Docker Compose"
	@echo "  make docker-down    - Tear down multi-container stack"
	@echo "  make clean          - Clean compiled target and dist directories"

setup:
	@echo "==> Setting up Frontend..."
	cd frontend && npm install
	@echo "==> Compiling Backend..."
	cd backend && mvn clean compile

backend:
	cd backend && mvn spring-boot:run

frontend:
	cd frontend && npm run dev

test:
	cd backend && mvn clean test

test-selenium:
	cd selenium-tests && mvn test

docker-build:
	docker build -t hackathon-team-management-system:1.0.0 -t hackathon-team-management-system:latest .

docker-up:
	docker compose up --build -d
	docker compose ps

docker-down:
	docker compose down

clean:
	cd backend && mvn clean
	rm -rf frontend/dist
