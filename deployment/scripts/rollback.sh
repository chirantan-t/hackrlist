#!/usr/bin/env bash
# ===================================================================
# Automated Rollback Script - Restores Previous Stable Image
# ===================================================================
set -euo pipefail

echo "=========================================================="
echo " Executing Automated Rollback to Previous Stable Release"
echo "=========================================================="

if [ -f .previous_stable_version ]; then
    ROLLBACK_TAG=$(cat .previous_stable_version)
else
    ROLLBACK_TAG="1.0.0"
fi

echo "Rolling back to verified stable image tag: ${ROLLBACK_TAG}..."
export IMAGE_TAG="${ROLLBACK_TAG}"

docker compose up -d --remove-orphans

echo "Verifying health of rolled-back container..."
sleep 5
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "http://localhost:${NGINX_PORT:-8081}/actuator/health" || true)

if [ "$HTTP_STATUS" = "200" ]; then
    echo "Rollback to ${ROLLBACK_TAG} SUCCESSFUL! Service restored and healthy."
    echo "${ROLLBACK_TAG}" > .current_stable_version
    exit 0
else
    echo "EMERGENCY: Rollback verification returned status ${HTTP_STATUS}. Immediate manual intervention required!"
    exit 2
fi
