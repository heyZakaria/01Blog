# 01Blog Platform - Complete Study Guide
## A Production-Ready Social Blogging Backend

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Architecture Overview](#architecture-overview)
4. [Database Schema](#database-schema)
5. [All Entities Explained](#entities)
6. [Complete API Endpoints](#api-endpoints)
7. [Security Implementation](#security)
8. [Design Patterns Used](#design-patterns)
9. [Key Features Deep Dive](#features)
10. [Study Roadmap](#study-roadmap)

---

<a name="project-overview"></a>
## 1. Project Overview

### What We Built

A **complete social blogging platform backend** where users can:
- Register, login, and manage profiles
- Create, read, update, delete posts
- Follow other users
- Like and comment on posts
- Receive notifications for interactions
- View personalized feed from followed users
- Report inappropriate content
- Admins can moderate content and ban users

### Project Stats

- **9 Core Features** (Auth, Users, Posts, Comments, Likes, Follows, Feed, Notifications, Reports)
- **8 Entity Models** (User, Post, Comment, Like, Subscription, Notification, Report)
- **50+ API Endpoints**
- **3-Layer Architecture** (Controller → Service → Repository)
- **JWT Authentication** with Spring Security
- **PostgreSQL Database** with JPA/Hibernate
- **RESTful Design** with proper HTTP methods and status codes

---

<a name="technology-stack"></a>
## 2. Technology Stack

### Backend Framework
```
Spring Boot 3.x
├─ Spring Web (REST API)
├─ Spring Security (Authentication/Authorization)
├─ Spring Data JPA (Database ORM)
└─ Spring Boot Actuator (Health checks)
```

### Database
```
PostgreSQL 15
├─ Relational database
├─ ACID transactions
└─ Foreign key constraints
```

### Security
```
JWT (JSON Web Tokens)
├─ Stateless authentication
├─ HS256 signature algorithm
└─ BCrypt password hashing
```

### Tools & Libraries
```
Maven (Dependency management)
JJWT (JWT generation/validation)
Hibernate (JPA implementation)
Jackson (JSON serialization)
```

---

<a name="architecture-overview"></a>
## 3. Architecture Overview

### Three-Layer Architecture

```
┌─────────────────────────────────────────────┐
│           CLIENT (Postman/Angular)          │
└──────────────────┬──────────────────────────┘
                   │ HTTP Request
                   ↓
┌─────────────────────────────────────────────┐
│        CONTROLLER LAYER (HTTP)              │
│  - Receives HTTP requests                   │
│  - Validates input                          │
│  - Returns HTTP responses                   │
│  - No business logic                        │
└──────────────────┬──────────────────────────┘
                   │ Calls
                   ↓
┌─────────────────────────────────────────────┐
│        SERVICE LAYER (Business Logic)       │
│  - Business rules                           │
│  - Validation                               │
│  - Orchestration                            │
│  - Transaction management                   │
└──────────────────┬──────────────────────────┘
                   │ Calls
                   ↓
┌─────────────────────────────────────────────┐
│        REPOSITORY LAYER (Data Access)       │
│  - CRUD operations                          │
│  - Database queries                         │
│  - No business logic                        │
└──────────────────┬──────────────────────────┘
                   │ JPA/Hibernate
                   ↓
┌─────────────────────────────────────────────┐
│            PostgreSQL DATABASE              │
└─────────────────────────────────────────────┘
```

### Request Flow (Complete)

```
1. HTTP Request arrives
   └─ Example: POST /api/v1/posts with JWT token

2. JwtAuthenticationFilter (Security)
   ├─ Extracts JWT from "Authorization: Bearer <token>"
   ├─ Validates token signature and expiration
   ├─ Extracts userId and role from token
   └─ Stores in SecurityContext

3. Spring Security Proxy (Authorization)
   ├─ Checks @PreAuthorize annotations
   ├─ Verifies user has required role
   └─ Throws AccessDeniedException if not authorized

4. Controller
   ├─ Receives request with @RequestBody
   ├─ Gets userId from SecurityContext
   ├─ Converts JSON to DTO (Jackson)
   └─ Calls Service

5. Service
   ├─ Validates business rules
   ├─ Calls other services if needed
   ├─ Creates/updates entities
   ├─ Calls Repository
   └─ Converts Entity to DTO

6. Repository
   ├─ Generates SQL (Hibernate)
   ├─ Executes via JDBC
   └─ Returns Entity

7. Service continues
   └─ Returns DTO to Controller

8. Controller
   └─ Returns ResponseEntity<DTO>

9. Jackson
   └─ Converts DTO to JSON

10. HTTP Response sent to client
```

---

<a name="database-schema"></a>
## 4. Database Schema

### Entity Relationship Diagram

```
┌──────────────┐
│    users     │
│──────────────│
│ id (PK)      │◄────────┐
│ name         │         │
│ email (UQ)   │         │
│ password     │         │
│ role         │         │
│ banned       │         │
│ created_at   │         │
│ updated_at   │         │
└──────────────┘         │
        ▲                │
        │                │
        │ author_id      │ user_id
        │                │
┌───────┴──────┐    ┌────┴─────────┐
│    posts     │    │subscriptions │
│──────────────│    │──────────────│
│ id (PK)      │    │ id (PK)      │
│ title        │    │ follower_id  │
│ description  │    │ following_id │
│ user_id (FK) │    │ created_at   │
│ created_at   │    └──────────────┘
│ updated_at   │
└──────┬───────┘
       │
       │ post_id
       │
       ├─────────────────┐
       │                 │
┌──────▼──────┐   ┌──────▼──────┐
│  comments   │   │    likes    │
│─────────────│   │─────────────│
│ id (PK)     │   │ id (PK)     │
│ content     │   │ user_id(FK) │
│ post_id(FK) │   │ post_id(FK) │
│ user_id(FK) │   │ created_at  │
│ created_at  │   └─────────────┘
│ updated_at  │
└─────────────┘
```

### Table Relationships

| Relationship | Type | Description |
|--------------|------|-------------|
| User → Posts | 1:N | One user creates many posts |
| User → Comments | 1:N | One user writes many comments |
| Post → Comments | 1:N | One post has many comments |
| User ↔ Posts (Likes) | N:M | Many users like many posts (junction: likes) |
| User ↔ Users (Follow) | N:M | Many users follow many users (junction: subscriptions) |
| User → Notifications | 1:N | One user receives many notifications |
| User → Reports | 1:N | One user makes many reports |

---

<a name="entities"></a>
## 5. All Entities Explained

### User Entity

**Purpose:** Represents a user account

**Fields:**
```
id          : String (UUID) - Primary key
name        : String - Display name
email       : String - Login identifier (unique)
password    : String - BCrypt hashed password
role        : String - "USER" or "ADMIN"
banned      : boolean - Account status
createdAt   : LocalDateTime - Registration timestamp
updatedAt   : LocalDateTime - Last modification timestamp
```

**Key Points:**
- Password is NEVER stored in plaintext (BCrypt hashing)
- Email is unique (database constraint)
- Banned users cannot access the system (checked in filter)
- Timestamps managed automatically by JPA Auditing

---

### Post Entity

**Purpose:** Represents a blog post

**Fields:**
```
id          : String (UUID) - Primary key
title       : String - Post title
description : String (1000 chars) - Post content
author      : User - Post creator (ManyToOne relationship)
createdAt   : LocalDateTime - Creation timestamp
updatedAt   : LocalDateTime - Last edit timestamp
```

**Key Points:**
- Author is a relationship, not just userId
- No likes field (calculated from likes table)
- Timestamps track when post was created/edited

**Relationships:**
- `@ManyToOne` → User (author)
- Can have many Comments
- Can have many Likes

---

### Comment Entity

**Purpose:** Represents a comment on a post

**Fields:**
```
id          : String (UUID) - Primary key
content     : String (500 chars) - Comment text
post        : Post - Which post (ManyToOne)
author      : User - Who commented (ManyToOne)
createdAt   : LocalDateTime - Comment timestamp
updatedAt   : LocalDateTime - Edit timestamp
```

**Key Points:**
- Belongs to both Post AND User
- Can be edited (updatedAt tracks this)
- Ordered by createdAt DESC (newest first)

---

### Like Entity

**Purpose:** Junction table for User-Post many-to-many

**Fields:**
```
id          : String (UUID) - Primary key
user        : User - Who liked (ManyToOne)
post        : Post - What was liked (ManyToOne)
createdAt   : LocalDateTime - When liked
```

**Key Points:**
- Unique constraint on (user_id, post_id) - can't like twice
- No "unlike" entity - delete Like to unlike
- Toggle pattern: exists = liked, doesn't exist = not liked

---

### Subscription Entity

**Purpose:** Junction table for User-User follow relationship

**Fields:**
```
id          : String (UUID) - Primary key
follower    : User - Who follows (ManyToOne)
following   : User - Who is followed (ManyToOne)
createdAt   : LocalDateTime - When followed
```

**Key Points:**
- Self-referencing relationship (User → User)
- Directional: Alice follows Bob ≠ Bob follows Alice
- Unique constraint on (follower_id, following_id)
- Cannot follow yourself (business logic check)

---

### Notification Entity

**Purpose:** Stores user notifications

**Fields:**
```
id            : String (UUID) - Primary key
user          : User - Recipient (ManyToOne)
type          : NotificationType - Enum (NEW_FOLLOWER, POST_LIKE, etc.)
message       : String - Notification text
relatedUser   : User - Who triggered (ManyToOne, optional)
relatedPost   : Post - Related post (ManyToOne, optional)
isRead        : boolean - Read status
createdAt     : LocalDateTime - Notification timestamp
```

**Key Points:**
- Four types: NEW_FOLLOWER, POST_LIKE, POST_COMMENT, NEW_POST
- Related entities are optional (depends on type)
- isRead tracks if user has seen notification
- No notification for self-actions (don't notify when you like your own post)

---

### Report Entity

**Purpose:** Stores user reports (moderation)

**Fields:**
```
id            : String (UUID) - Primary key
reporter      : User - Who reported (ManyToOne)
reportedUser  : User - Who was reported (ManyToOne)
reason        : String - Why reported
status        : ReportStatus - PENDING, REVIEWED, RESOLVED, DISMISSED
adminNotes    : String - Admin comments (optional)
createdAt     : LocalDateTime - Report timestamp
resolvedAt    : LocalDateTime - Resolution timestamp (optional)
```

**Key Points:**
- Workflow: PENDING → REVIEWED → RESOLVED/DISMISSED
- Admin can ban user when resolving
- Cannot report yourself
- Cannot report same user twice

---

<a name="api-endpoints"></a>
## 6. Complete API Endpoints (50+ Endpoints)

### Authentication (Public - No Token Required)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/v1/auth/register` | Register new user | CreateUserRequest | LoginResponse (token + user) |
| POST | `/api/v1/auth/login` | Login existing user | LoginRequest | LoginResponse (token + user) |

---

### Users (Protected - Token Required)

| Method | Endpoint | Description | Auth | Response |
|--------|----------|-------------|------|----------|
| GET | `/api/v1/users` | Get all users | ADMIN only | List\<UserDTO\> |
| GET | `/api/v1/users/{id}` | Get user by ID | Any authenticated | UserDTO (with follower counts) |
| GET | `/api/v1/users/me` | Get current user | Any authenticated | UserDTO |
| POST | `/api/v1/users` | Create user | ADMIN only | UserDTO |
| PUT | `/api/v1/users/{id}` | Update user | Owner or ADMIN | UserDTO |
| DELETE | `/api/v1/users/{id}` | Delete user | Owner or ADMIN | 204 No Content |
| GET | `/api/v1/users/{id}/posts` | Get user's posts | Any authenticated | List\<PostDTO\> |

---

### Posts (Protected - Token Required)

| Method | Endpoint | Description | Auth | Response |
|--------|----------|-------------|------|----------|
| GET | `/api/v1/posts` | Get all posts (global feed) | Any authenticated | List\<PostDTO\> |
| GET | `/api/v1/posts/feed` | Get personalized feed | Any authenticated | List\<PostDTO\> (from followed users) |
| GET | `/api/v1/posts/{id}` | Get single post | Any authenticated | PostDTO |
| POST | `/api/v1/posts` | Create post | Any authenticated | PostDTO |
| PUT | `/api/v1/posts/{id}` | Update post | Owner only | PostDTO |
| DELETE | `/api/v1/posts/{id}` | Delete post | Owner only | 204 No Content |
| POST | `/api/v1/posts/{id}/like` | Toggle like/unlike | Any authenticated | {liked: boolean, likeCount: number} |

---

### Comments (Protected - Token Required)

| Method | Endpoint | Description | Auth | Response |
|--------|----------|-------------|------|----------|
| GET | `/api/v1/posts/{postId}/comments` | Get post comments | Any authenticated | List\<CommentDTO\> |
| POST | `/api/v1/posts/{postId}/comments` | Create comment | Any authenticated | CommentDTO |
| PUT | `/api/v1/posts/{postId}/comments/{id}` | Update comment | Owner only | CommentDTO |
| DELETE | `/api/v1/posts/{postId}/comments/{id}` | Delete comment | Owner only | 204 No Content |

---

### Subscriptions/Follow (Protected - Token Required)

| Method | Endpoint | Description | Auth | Response |
|--------|----------|-------------|------|----------|
| POST | `/api/v1/users/{id}/follow` | Toggle follow/unfollow | Any authenticated | {following: boolean, followersCount: number} |
| GET | `/api/v1/users/me/following` | Get who I follow | Any authenticated | List\<UserDTO\> |
| GET | `/api/v1/users/me/followers` | Get my followers | Any authenticated | List\<UserDTO\> |
| GET | `/api/v1/users/{id}/following` | Get who user follows | Any authenticated | List\<UserDTO\> |
| GET | `/api/v1/users/{id}/followers` | Get user's followers | Any authenticated | List\<UserDTO\> |

---

### Notifications (Protected - Token Required)

| Method | Endpoint | Description | Auth | Response |
|--------|----------|-------------|------|----------|
| GET | `/api/v1/notifications` | Get all notifications | Any authenticated | List\<NotificationDTO\> |
| GET | `/api/v1/notifications/unread` | Get unread only | Any authenticated | List\<NotificationDTO\> |
| GET | `/api/v1/notifications/unread/count` | Get unread count (badge) | Any authenticated | {count: number} |
| PUT | `/api/v1/notifications/{id}/read` | Mark as read | Any authenticated | 204 No Content |
| PUT | `/api/v1/notifications/read-all` | Mark all as read | Any authenticated | 204 No Content |
| DELETE | `/api/v1/notifications/{id}` | Delete notification | Any authenticated | 204 No Content |
| DELETE | `/api/v1/notifications` | Delete all notifications | Any authenticated | 204 No Content |

---

### Reports (Mixed Authorization)

| Method | Endpoint | Description | Auth | Response |
|--------|----------|-------------|------|----------|
| POST | `/api/v1/users/{id}/report` | Report a user | Any authenticated | ReportDTO |

---

### Admin Panel (Protected - ADMIN Only)

| Method | Endpoint | Description | Auth | Response |
|--------|----------|-------------|------|----------|
| GET | `/api/v1/admin/reports` | Get all reports | ADMIN only | List\<ReportDTO\> |
| GET | `/api/v1/admin/reports/status/{status}` | Filter by status | ADMIN only | List\<ReportDTO\> |
| PUT | `/api/v1/admin/reports/{id}/resolve` | Resolve report | ADMIN only | ReportDTO |
| DELETE | `/api/v1/admin/reports/{id}` | Delete report | ADMIN only | 204 No Content |
| GET | `/api/v1/admin/users` | Get all users | ADMIN only | List\<UserDTO\> |
| PUT | `/api/v1/admin/users/{id}/ban` | Ban/unban user | ADMIN only | {userId, banned, message} |
| DELETE | `/api/v1/admin/users/{id}` | Delete user | ADMIN only | 204 No Content |
| GET | `/api/v1/admin/analytics` | Dashboard stats | ADMIN only | {pendingReports, totalUsers} |

---

<a name="security"></a>
## 7. Security Implementation

### JWT Authentication Flow

```
1. User Registers/Logs In
   ↓
2. Server validates credentials
   ↓
3. Server generates JWT token
   └─ Header: {"alg": "HS256", "typ": "JWT"}
   └─ Payload: {"sub": "user-123", "role": "USER", "exp": 1234567890}
   └─ Signature: HMAC_SHA256(header + payload, SECRET_KEY)
   ↓
4. Token sent to client
   └─ Response: {"token": "eyJhbGc...", "user": {...}}
   ↓
5. Client stores token (localStorage)
   ↓
6. Client sends token with every request
   └─ Header: Authorization: Bearer eyJhbGc...
   ↓
7. JwtAuthenticationFilter validates token
   ├─ Verify signature (tamper-proof)
   ├─ Check expiration
   ├─ Extract userId and role
   └─ Store in SecurityContext
   ↓
8. Request reaches controller
   └─ userId available via SecurityContext
```

### Password Security

**Hashing Algorithm:** BCrypt
- Automatically generates random salt
- Intentionally slow (prevents brute force)
- One-way function (can't decrypt, only verify)
- Adaptive (can increase rounds over time)

**Process:**
```
Registration:
plaintext: "password123"
    ↓ BCrypt.encode()
stored: "$2a$10$N9qo8uLOickgx2ZMRZoMye..."

Login:
user enters: "password123"
    ↓ BCrypt.matches(input, stored)
comparison: true/false
```

### Authorization Levels

**Public Endpoints:**
- `/api/v1/auth/**` - No token required

**Authenticated Endpoints:**
- All other endpoints require valid JWT token

**Role-Based:**
- `@PreAuthorize("hasRole('ADMIN')")` - Admin only
- `@PreAuthorize("hasRole('USER')")` - Any authenticated user
- `@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")` - Admin or owner

**Ownership Validation:**
- Posts: Only author can update/delete
- Comments: Only author can update/delete
- Users: Can only update/delete own account (except admin)

### Security Features Implemented

✅ **JWT Authentication** (stateless, scalable)
✅ **Password Hashing** (BCrypt with salt)
✅ **Role-Based Access Control** (USER vs ADMIN)
✅ **Ownership Validation** (can only edit own content)
✅ **Banned User Blocking** (filter checks on every request)
✅ **Generic Error Messages** (prevent information disclosure)
✅ **CSRF Disabled** (not needed for JWT in headers)
✅ **Stateless Sessions** (no server-side session storage)
✅ **Token Expiration** (24 hour default)
✅ **Secure Headers** (Spring Security defaults)

---

<a name="design-patterns"></a>
## 8. Design Patterns Used

### 1. Three-Layer Architecture (Separation of Concerns)

**Pattern:** Layered Architecture
**Why:** Each layer has single responsibility

```
Controller → HTTP concerns only
Service → Business logic only
Repository → Data access only
```

**Benefits:**
- Easy to test (mock layers)
- Easy to maintain (changes isolated)
- Easy to understand (clear boundaries)

---

### 2. DTO Pattern (Data Transfer Object)

**Pattern:** Separate internal and external representations

```
CreatePostRequest (input) → Post (entity) → PostDTO (output)
```

**Why:**
- Security (don't expose password)
- Flexibility (API shape ≠ database shape)
- Validation (different rules for input/output)

---

### 3. Repository Pattern

**Pattern:** Abstract data access

```
Service calls → Repository interface
Repository generates SQL automatically
```

**Why:**
- Testable (can mock repository)
- Flexible (can swap database)
- Clean (hides SQL complexity)

---

### 4. Dependency Injection (IoC)

**Pattern:** Don't create dependencies, inject them

```java
@Service
public class PostService {
    private final PostRepository repository;
    
    // Spring injects repository automatically
    public PostService(PostRepository repository) {
        this.repository = repository;
    }
}
```

**Why:**
- Loose coupling
- Testable (can inject mocks)
- Spring manages lifecycle

---

### 5. Builder Pattern

**Pattern:** Fluent object construction

**Used in:** JWT generation, test setup

```java
Jwts.builder()
    .setSubject(userId)
    .claim("role", role)
    .setExpiration(date)
    .signWith(key)
    .compact();
```

**Why:**
- Readable
- Flexible (optional parameters)
- Immutable

---

### 6. Strategy Pattern

**Pattern:** Different authentication strategies

**Used in:** Spring Security authentication

```
Could support:
- JWT Authentication (what we built)
- OAuth2 (future: Login with Google)
- Basic Auth (alternative)
```

---

### 7. Template Method Pattern

**Pattern:** Define algorithm structure, customize steps

**Used in:** Spring Boot's filter chain

```
OncePerRequestFilter.doFilterInternal()
    ↓
Your implementation of validation logic
    ↓
Continue filter chain
```

---

### 8. Observer Pattern

**Pattern:** Notify interested parties of events

**Used in:** Notifications system

```
Event: User creates post
    ↓
Observers: All followers
    ↓
Action: Create notification for each follower
```

---

<a name="features"></a>
## 9. Key Features Deep Dive

### Feature 1: Authentication & Authorization

**What it does:**
- Users register and login
- JWT tokens issued
- Tokens validate on every request
- Role-based permissions (USER vs ADMIN)

**Key Files:**
- `JwtUtil.java` - Token generation/validation
- `JwtAuthenticationFilter.java` - Intercepts requests
- `SecurityConfig.java` - Spring Security configuration
- `AuthService.java` - Login/register logic

**Security Concepts:**
- Stateless (no sessions)
- Token-based (JWT in headers)
- Signature verification (tamper-proof)
- Expiration (24 hours default)

---

### Feature 2: Posts with Comments & Likes

**What it does:**
- Users create posts
- Other users like and comment
- Like count and comment count displayed
- Feed shows posts from followed users

**Key Relationships:**
- Post → User (author)
- Post → Comments (1:N)
- Post ← Users (likes, N:M via likes table)

**N+1 Problem Solution:**
```java
@Query("SELECT p FROM Post p JOIN FETCH p.author")
// One query fetches posts WITH authors
// Instead of 1 + N separate queries
```

---

### Feature 3: Subscriptions (Follow System)

**What it does:**
- Users follow other users
- See follower/following counts
- Feed shows posts from followed users
- Notifications when followed

**Key Concepts:**
- Self-referencing relationship (User → User)
- Directional (follow ≠ mutual)
- Junction table (subscriptions)
- Toggle pattern (follow/unfollow same endpoint)

---

### Feature 4: Notifications

**What it does:**
- Auto-create on events:
  - Someone follows you
  - Someone likes your post
  - Someone comments on your post
  - Someone you follow posts
- Read/unread tracking
- Unread count (for badge)

**Key Implementation:**
- Created in service layer (not controller)
- Related entities stored (user, post)
- Smart filtering (no self-notifications)

---

### Feature 5: Reports & Admin Panel

**What it does:**
- Users report inappropriate content
- Admins view all reports
- Admins can ban users
- Ban blocks system access

**Workflow:**
```
PENDING → REVIEWED → RESOLVED/DISMISSED
```

**Admin Powers:**
- View all reports
- Ban/unban users
- Delete posts
- View analytics

---

<a name="study-roadmap"></a>
## 10.  Roadmap Roadmap Roadmap

### Phase 1: Understand Core Concepts

** Spring Boot Basics**
- How Spring Boot works
- Dependency Injection
- Component scanning
- Auto-configuration

** REST API Concepts**
- HTTP methods (GET, POST, PUT, DELETE)
- Status codes (200, 201, 401, 403, 404)
- Request/Response cycle
- JSON serialization

** Three-Layer Architecture**
- Controller responsibilities
- Service responsibilities
- Repository responsibilities
- DTO pattern

---

### Phase 2: Security Deep Dive

** JWT Fundamentals**
- JWT structure (header, payload, signature)
- Signing algorithms (HS256)
- Token validation
- Expiration handling

** Spring Security**
- Filter chain
- SecurityContext
- Authentication vs Authorization
- @PreAuthorize annotations

** Password Security**
- BCrypt algorithm
- Salt generation
- Rainbow table attacks
- Best practices

---

### Phase 3: Database & JPA

** JPA Basics**
- Entities vs DTOs
- @Entity, @Table, @Id, @Column
- JPA Auditing (@CreatedDate, @LastModifiedDate)

** Relationships**
- @OneToMany, @ManyToOne
- @ManyToMany (junction tables)
- FetchType.LAZY vs EAGER
- Cascade operations

** Queries**
- Derived query methods (findByEmail)
- @Query with JPQL
- JOIN FETCH (N+1 solution)
- Counting and aggregation

---

### Phase 4: Advanced Patterns

** Design Patterns**
- Review all patterns used
- When to apply each
- Trade-offs

** Error Handling**
- Custom exceptions
- Global exception handler
- Consistent error responses

** Testing**
- Unit tests (Mockito)
- Integration tests (Spring Boot Test)
- Test structure (AAA pattern)

---

## 📊 Study Checklist

Use this to track what you've learned:

### Core Concepts
- [ ] Understand Spring Boot auto-configuration
- [ ] Understand Dependency Injection
- [ ] Understand Bean lifecycle
- [ ] Understand Component scanning

### REST API
- [ ] Know all HTTP methods
- [ ] Know all status codes
- [ ] Understand request/response flow
- [ ] Understand JSON serialization

### Security
- [ ] Understand JWT structure
- [ ] Understand token validation
- [ ] Understand BCrypt hashing
- [ ] Understand Spring Security filter chain
- [ ] Understand role-based authorization

### Database
- [ ] Understand JPA entities
- [ ] Understand relationships (@OneToMany, etc.)
- [ ] Understand FetchType
- [ ] Understand N+1 problem
- [ ] Understand JOIN FETCH

### Architecture
- [ ] Understand three-layer separation
- [ ] Understand DTO pattern
- [ ] Understand Repository pattern
- [ ] Understand Service orchestration

### Features
- [ ] Understand authentication flow
- [ ] Understand authorization checks
- [ ] Understand follow system
- [ ] Understand notification system
- [ ] Understand feed generation
- [ ] Understand reporting system

---

## 🎯 Next Steps

### To Deepen Understanding:
1. **Read the deep dive artifacts** on JPA and Spring Security
2. **Trace request flows** for each feature
3. **Draw diagrams** of relationships and flows
4. **Write tests** for features you want to understand better

### To Expand Project:
1. **Add media upload** (file handling)
2. **Add pagination** (performance)
3. **Add analytics** (data aggregation)
4. **Add API documentation** (Swagger)
5. **Build Angular frontend** (complete fullstack)

### To Practice:
1. **Modify existing features** (change business rules)
2. **Add new features** (bookmarks, shares, etc.)
3. **Optimize queries** (analyze with EXPLAIN)
4. **Write more tests** (increase coverage)

---
