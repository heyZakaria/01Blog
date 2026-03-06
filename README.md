# 01Blog

01Blog is a full-stack social blogging platform for students to share learning progress, follow peers, and engage in discussions.

## Features

- 9 Core Features (Auth, Users, Posts, Comments, Likes, Follows, Feed, Notifications, Reports)
- 8 Entity Models (User, Post, Comment, Like, Subscription, Notification, Report)
- 50+ API Endpoints
- 3-Layer Architecture (Controller → Service → Repository)
- JWT Authentication with Spring Security
- PostgreSQL Database with JPA/Hibernate
- RESTful Design with proper HTTP methods and status codes

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
