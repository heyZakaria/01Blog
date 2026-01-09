#!/bin/bash

set -o pipefail

LOG_FILE="spring-error.log"
DB_SERVICE="postgres"

cleanup_db() {
  echo ""
  echo "🛑 Application failed → stopping DB container..."
  docker-compose stop "$DB_SERVICE"
  echo "🧹 DB container stopped"
}

echo "🛑 Stopping docker containers + removing volumes..."
docker-compose down -v --remove-orphans || {
  echo "❌ docker-compose down failed"
  exit 1
}

echo "🧹 Pruning docker volumes..."
docker volume prune -f

echo "🧽 Cleaning Maven project..."
./mvnw clean || {
  echo "❌ Maven clean failed"
  exit 1
}

echo "🚀 Starting PostgreSQL container..."
docker-compose up -d || {
  echo "❌ Failed to start PostgreSQL"
  exit 1
}

echo "⏳ Waiting for PostgreSQL (5s)..."
sleep 5

echo "▶️ Starting Spring Boot (ERRORS + DB info only)..."
echo "📄 Errors log: $LOG_FILE"

./mvnw spring-boot:run \
  | grep -Ei "ERROR|Exception|Caused by|HHH10001005|Database info" \
  | tee "$LOG_FILE"

EXIT_CODE=${PIPESTATUS[0]}

if [ $EXIT_CODE -ne 0 ]; then
  echo ""
  echo "❌ APPLICATION FAILED"
  echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
  tail -n 20 "$LOG_FILE"
  echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

  cleanup_db
  exit 1
fi

echo "✅ Application started successfully"
