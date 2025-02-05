#!/bin/bash

# This script will start all Docker containers, then wait until they are healthy before continuing.

set -e

docker compose up -d --build

MAX_RETRIES=30
RETRY_COUNT=1

wait_until_all_containers_healthy() {
  TOTAL_COUNT=$(docker compose ps -q | wc -l)
  for CONTAINER_ID in $(docker compose ps -q); do
    CONTAINER_NAME=$(docker inspect --format='{{.Name}}' "${CONTAINER_ID}" | cut -c2-)
    echo "Waiting for container: ${CONTAINER_NAME} (id: ${CONTAINER_ID})"
  done
  while true; do
    HEALTHY_COUNT=0
    echo "Checking container statuses..."
    for CONTAINER_ID in $(docker compose ps -q); do
      CONTAINER_NAME=$(docker inspect --format='{{.Name}}' "${CONTAINER_ID}" | cut -c2-)
      STATUS=$(docker inspect \
        --format='{{if .State.Health}}{{.State.Health.Status}}{{else}}unhealthy{{end}}' \
        "${CONTAINER_ID}")
      if [ "$STATUS" == "healthy" ]; then
        HEALTHY_COUNT=$((HEALTHY_COUNT + 1))
      else
        echo "Container still not ready: ${CONTAINER_NAME} (status: ${STATUS})"
      fi
    done
    echo "Waiting for containers to be healthy: $HEALTHY_COUNT/$TOTAL_COUNT (attempt ${RETRY_COUNT})"
    if [[ $HEALTHY_COUNT -eq $TOTAL_COUNT ]]; then
      break
    fi
    if [[ $RETRY_COUNT -ge $MAX_RETRIES ]]; then
      echo "Containers did not become healthy after $MAX_RETRIES attempts, exiting."
      exit 1
    fi
    RETRY_COUNT=$((RETRY_COUNT + 1))
    sleep 5
  done
}

wait_until_all_containers_healthy

echo "All containers are healthy."
