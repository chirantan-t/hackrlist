#!/usr/bin/env bash
# ===================================================================
# Health Check Verification Script
# ===================================================================
PORT="${1:-8081}"
URL="http://localhost:${PORT}/actuator/health"

echo "Checking application health at: ${URL}"
RESPONSE=$(curl -s "${URL}" || echo "FAILED")

if echo "${RESPONSE}" | grep -q '"status":"UP"'; then
    echo "SUCCESS: Health probe returned status UP"
    echo "${RESPONSE}"
    exit 0
else
    echo "ERROR: Health check failed! Response: ${RESPONSE}"
    exit 1
fi
