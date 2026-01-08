#!/bin/bash

echo "🛑 Stopping docker-compose and removing containers + volumes..."
docker-compose down -v --remove-orphans

echo "🧹 Removing unused docker volumes (postgres leftovers)..."
docker volume prune -f

echo "🧽 Cleaning Maven project (target/, cache)..."
./mvnw clean

echo "🚀 Starting PostgreSQL container..."
docker-compose up -d

echo "⏳ Waiting for PostgreSQL to be ready..."
sleep 5

echo "▶️ Starting Spring Boot application..."
./mvnw spring-boot:run | grep -i error
