#!/usr/bin/env bash
# ===================================================================
# Automated Deployment Script - Hackathon Team Management System
# ===================================================================
set -euo pipefail

IMAGE_TAG="${1:-1.0.0}"
APP_PORT="${2:-8081}"

echo "=========================================================="
echo " Starting Automated Deployment for Release: ${IMAGE_TAG}"
echo " Host Entry Port: ${APP_PORT}"
echo "=========================================================="

export IMAGE_TAG
export NGINX_PORT="${APP_PORT}"

# Step 1: Record previous stable tag for automatic rollback
if [ -f .current_stable_version ]; then
    PREVIOUS_TAG=$(cat .current_stable_version)
    echo "Found previous stable release: ${PREVIOUS_TAG}"
else
    PREVIOUS_TAG="1.0.0"
    echo "No prior release record found. Initial baseline: ${PREVIOUS_TAG}"
fi
echo "${PREVIOUS_TAG}" > .previous_stable_version

# Step 2: Deploy new container stack
echo "Deploying Docker Compose stack with tag: ${IMAGE_TAG}..."
docker compose pull || true
docker compose up -d --remove-orphans

# Step 3: Run Health Probe Loop
echo "Probing application health on http://localhost:${APP_PORT}/actuator/health..."
MAX_ATTEMPTS=20
ATTEMPT=0
HEALTHY=false

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    ATTEMPT=$((ATTEMPT + 1))
    HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:${APP_PORT}/actuator/health" || true)

    if [ "$HTTP_STATUS" = "200" ]; then
        echo "Health probe passed with HTTP 200 OK on attempt ${ATTEMPT}/${MAX_ATTEMPTS}!"
        HEALTHY=true
        break
    else
        echo "Attempt ${ATTEMPT}/${MAX_ATTEMPTS}: Health probe returned HTTP ${HTTP_STATUS}. Retrying in 3s..."
        sleep 3
    fi
done

# Step 4: Handle Failure or Finalize Success
if [ "$HEALTHY" = true ]; then
    echo "Deployment of version ${IMAGE_TAG} SUCCEEDED and is verified healthy."
    echo "${IMAGE_TAG}" > .current_stable_version
    exit 0
else
    echo "CRITICAL: Health probe failed after ${MAX_ATTEMPTS} attempts. Triggering rollback!"
    ./deployment/scripts/rollback.sh
    exit 1
fi
