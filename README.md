# 01Blog

01Blog is a full-stack social blogging platform for students to share learning progress, follow peers, and engage in discussions.

## Features
- JWT-based authentication with user/admin roles
- User profiles ("blocks") with subscriptions
- Post CRUD with media (image/video) and timestamps
- Likes and comments on posts
- Reporting system and admin moderation tools
- Notifications for updates from subscriptions

## Tech Stack
- Backend: Java 17, Spring Boot, Spring Security, Spring Data JPA, JWT
- Database: PostgreSQL
- Frontend: Angular, RxJS
- Storage: local filesystem for uploads

## Prerequisites
- Java 17+
- Maven (or `backend/mvnw`)
- Node.js 20+ and npm
- PostgreSQL (or Docker)

## Installation & Running

## Database
Option A (Docker):
```bash
cd backend
docker compose up -d
```

Option B (Local PostgreSQL):
Create a database `mydb` with user `root` and password `root`, or update:
`backend/src/main/resources/application.properties`.

## Backend
```bash
cd backend
./mvnw spring-boot:run
```
or
```bash
cd backend
./clean-run.sh
```
Backend runs at `http://localhost:8080`.

## Frontend
```bash
cd frontend
npm install
ng serve
```
Frontend runs at `http://localhost:4200`.

## Configuration Notes
- API base URL: `frontend/src/environments/environment.ts` and `frontend/src/environments/environment.prod.ts`
- Uploads folder: `backend/uploads` (set by `file.upload-dir` in `application.properties`)
