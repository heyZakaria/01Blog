# Mind Questions
1- is the failed request always go out from a specific filter ?
2- study in case using session ?
3- custom query jpa



# Deep Dive: Spring Security Framework
## A Complete Understanding from First Principles

---

## 🎯 Learning Objectives

By the end of this guide, you'll understand:
- What Spring Security is and why it exists
- How the filter chain works
- Authentication vs Authorization
- SecurityContext and thread-local storage
- How different authentication mechanisms work
- How to secure REST APIs
- Best practices and common patterns

---

## 📚 Table of Contents

1. [The Security Problem](#the-problem)
2. [What is Spring Security?](#what-is-spring-security)
3. [Core Architecture](#core-architecture)
4. [Filter Chain Deep Dive](#filter-chain)
5. [Authentication Process](#authentication)
6. [Authorization Process](#authorization)
7. [SecurityContext & Thread-Local Storage](#security-context)
8. [Common Authentication Mechanisms](#auth-mechanisms)
9. [REST API Security](#rest-api-security)
10. [Best Practices & Patterns](#best-practices)

---

<a name="the-problem"></a>
## 1. The Security Problem

### The Challenge

You're building a web application. You need to:

1. **Know WHO is making requests** (Authentication)
2. **Control WHAT they can access** (Authorization)
3. **Protect against attacks** (Security)
4. **Handle it consistently** (Framework)

### Without a Framework

**Every controller needs checks:**
```
Request arrives
    ↓
Extract credentials from header/cookie
    ↓
Validate credentials
    ↓
Load user details
    ↓
Check if user has permission
    ↓
If yes → Process request
If no → Return 403 Forbidden
```

**Problems:**
- ❌ Code duplication in every endpoint
- ❌ Easy to forget (security bugs)
- ❌ Inconsistent error handling
- ❌ Hard to test
- ❌ Mixing security with business logic

### What We Need

A framework that:
- ✅ Intercepts ALL requests automatically
- ✅ Handles authentication uniformly
- ✅ Enforces authorization declaratively
- ✅ Protects against common attacks
- ✅ Keeps security separate from business logic

**This is Spring Security!**

---

<a name="what-is-spring-security"></a>
## 2. What is Spring Security?

### Definition

**Spring Security is a comprehensive authentication and authorization framework for Java applications.**

Think of it as: **A security guard system for your application**

### Core Responsibilities

#### 1. Authentication ("Who are you?")
```
User claims to be "john@example.com"
    ↓
Spring Security validates this claim
    ↓
If valid: Creates Authentication object
If invalid: Throws AuthenticationException
```

#### 2. Authorization ("What can you do?")
```
Authenticated user tries to access /admin
    ↓
Spring Security checks permissions
    ↓
If authorized: Allow access
If not: Throws AccessDeniedException (403)
```

#### 3. Protection
```
- CSRF (Cross-Site Request Forgery)
- Session Fixation
- Clickjacking
- XSS (Cross-Site Scripting) headers
- Security headers (X-Frame-Options, etc.)
```

### How It Works (High Level)

```
HTTP Request
    ↓
┌─────────────────────────────────────┐
│ Spring Security Filter Chain        │
│ (15+ filters that process request)  │
└────────────┬────────────────────────┘
             ↓
┌─────────────────────────────────────┐
│ Authentication Filter                │
│ (Extracts credentials, validates)   │
└────────────┬────────────────────────┘
             ↓
┌─────────────────────────────────────┐
│ Authorization Filter                 │
│ (Checks permissions)                │
└────────────┬────────────────────────┘
             ↓
┌─────────────────────────────────────┐
│ Your Controller                      │
│ (Business logic runs if allowed)    │
└─────────────────────────────────────┘
```

---

<a name="core-architecture"></a>
## 3. Core Architecture

### The Main Components

```
┌──────────────────────────────────────────────┐
│           SPRING SECURITY                    │
│                                              │
│  ┌────────────────────────────────────┐    │
│  │   SecurityFilterChain              │    │
│  │   (Chain of security filters)      │    │
│  └──────────┬─────────────────────────┘    │
│             ↓                                │
│  ┌────────────────────────────────────┐    │
│  │   AuthenticationManager            │    │
│  │   (Coordinates authentication)     │    │
│  └──────────┬─────────────────────────┘    │
│             ↓                                │
│  ┌────────────────────────────────────┐    │
│  │   AuthenticationProvider           │    │
│  │   (Does actual authentication)     │    │
│  └──────────┬─────────────────────────┘    │
│             ↓                                │
│  ┌────────────────────────────────────┐    │
│  │   UserDetailsService               │    │
│  │   (Loads user from database)       │    │
│  └────────────────────────────────────┘    │
│             ↓                                │
│  ┌────────────────────────────────────┐    │
│  │   SecurityContext                  │    │
│  │   (Stores authentication)          │    │
│  └────────────────────────────────────┘    │
└──────────────────────────────────────────────┘
```

### Component 1: SecurityFilterChain

**What is it?**
A chain of filters that process every HTTP request.

**Think of it as:** Airport security checkpoints - you pass through multiple checks.

**Default filters (in order):**
```
1. SecurityContextPersistenceFilter
   → Loads SecurityContext from session

2. CsrfFilter
   → Validates CSRF token

3. UsernamePasswordAuthenticationFilter
   → Handles form login

4. BasicAuthenticationFilter
   → Handles HTTP Basic auth

5. BearerTokenAuthenticationFilter
   → Handles JWT/OAuth tokens

6. ExceptionTranslationFilter
   → Converts exceptions to HTTP responses

7. FilterSecurityInterceptor
   → Makes authorization decisions

... and more
```

**How it works:**
```
Request enters
    ↓
Filter 1 processes → Calls next filter
    ↓
Filter 2 processes → Calls next filter
    ↓
Filter 3 processes → Calls next filter
    ↓
... continues ...
    ↓
Reaches your controller
```

**Any filter can stop the chain:**
```
Request → Filter 1 → Filter 2 → ❌ Invalid auth! → 401 Response
                                  (Chain stops here)
```

### Component 2: Authentication

**What is it?**
An object representing an authenticated user.

**Think of it as:** Your security badge after passing security.

**What it contains:**
```
Authentication object
├─ principal: The user (UserDetails or custom object)
├─ credentials: Password/token (often cleared after auth)
├─ authorities: List of permissions (roles)
├─ details: Additional info (IP address, session ID)
└─ authenticated: boolean (is this valid?)
```

**Lifecycle:**
```
Before Authentication:
    Authentication auth = new UsernamePasswordAuthenticationToken(
        username,
        password
    );
    auth.isAuthenticated() → false

After Authentication:
    Authentication auth = authenticationManager.authenticate(auth);
    auth.isAuthenticated() → true
    auth.getAuthorities() → [ROLE_USER, ROLE_ADMIN]
```

### Component 3: SecurityContext

**What is it?**
A holder for the Authentication object.

**Think of it as:** A secure locker where your badge is stored.

**How it works:**
```
SecurityContext
    └─ Authentication
        ├─ Principal (user details)
        ├─ Authorities (roles)
        └─ Credentials
```

**Accessing it:**
```
Get current authentication:
    SecurityContextHolder.getContext().getAuthentication()

Store authentication:
    SecurityContextHolder.getContext().setAuthentication(auth)

Clear authentication:
    SecurityContextHolder.clearContext()
```

### Component 4: UserDetails

**What is it?**
An interface representing a user's security information.

**Think of it as:** Your employee profile in the security database.

**What it provides:**
```
UserDetails
├─ getUsername() → "john@example.com"
├─ getPassword() → "$2a$10$hashed..."
├─ getAuthorities() → [ROLE_USER]
├─ isAccountNonExpired() → true
├─ isAccountNonLocked() → true
├─ isCredentialsNonExpired() → true
└─ isEnabled() → true
```

**Why these flags?**
```
isEnabled = false → Account disabled (soft delete)
isAccountNonLocked = false → Account locked (brute force protection)
isAccountNonExpired = false → Account expired (temporary access)
isCredentialsNonExpired = false → Password expired (force change)
```

### Component 5: UserDetailsService

**What is it?**
An interface for loading user details from your data source.

**Think of it as:** The HR system that looks up your employee record.

**The contract:**
```
Input: Username (or email, or ID)
Output: UserDetails object

If user not found: Throw UsernameNotFoundException
```

**How Spring Security uses it:**
```
1. User submits credentials
2. AuthenticationProvider extracts username
3. Calls UserDetailsService.loadUserByUsername(username)
4. Gets UserDetails back
5. Compares submitted password with UserDetails.getPassword()
6. If match → Authentication successful
7. If not → AuthenticationException
```

### Component 6: AuthenticationManager

**What is it?**
The main interface for authentication.

**Think of it as:** The security supervisor who coordinates authentication.

**What it does:**
```
Input: Authentication (username + password)
Process: Delegates to AuthenticationProvider(s)
Output: Authenticated Authentication object

authenticate(Authentication auth) throws AuthenticationException
```

**How it works:**
```
AuthenticationManager
    └─ Has multiple AuthenticationProviders
        ├─ DaoAuthenticationProvider (username/password)
        ├─ JwtAuthenticationProvider (JWT tokens)
        └─ LdapAuthenticationProvider (LDAP)

Tries each provider in order until one succeeds
```

### Component 7: AuthenticationProvider

**What is it?**
The component that actually performs authentication.

**Think of it as:** The security officer who checks your credentials.

**The contract:**
```
boolean supports(Class<?> authentication)
    → Can I handle this type of authentication?

Authentication authenticate(Authentication auth)
    → Do the actual authentication
```

**Most common: DaoAuthenticationProvider**
```
1. Extract username from Authentication
2. Call UserDetailsService.loadUserByUsername()
3. Get UserDetails
4. Compare passwords using PasswordEncoder
5. If match → Return authenticated Authentication
6. If not → Throw BadCredentialsException
```

### Component 8: PasswordEncoder

**What is it?**
An interface for encoding and verifying passwords.

**Think of it as:** The encryption specialist.

**Why we need it:**
```
❌ Never store: "password123"
✅ Always store: "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
```

**The contract:**
```
String encode(String rawPassword)
    → Hash the password

boolean matches(String rawPassword, String encodedPassword)
    → Verify password against hash
```

**Common implementations:**
```
BCryptPasswordEncoder (recommended)
    - Adaptive hashing
    - Built-in salting
    - Intentionally slow

Pbkdf2PasswordEncoder
    - Key derivation function
    - Configurable iterations

SCryptPasswordEncoder
    - Memory-hard function
    - Resistant to hardware attacks

NoOpPasswordEncoder (⚠️ NEVER use in production!)
    - Stores plaintext
    - Only for testing
```

---

<a name="filter-chain"></a>
## 4. Filter Chain Deep Dive

### What is a Filter?

**A filter intercepts HTTP requests before they reach your controller.**

```
Client → Filter 1 → Filter 2 → Filter 3 → Controller
```

**Filter interface:**
```
void doFilter(request, response, chain)
    ↓
1. Do something before (e.g., validate token)
2. Call chain.doFilter() to continue
3. Do something after (e.g., log response)
```

### Spring Security Filter Chain

**15+ filters work together to secure your application.**

#### Filter 1: SecurityContextPersistenceFilter

**Purpose:** Load/save SecurityContext from session.

**How it works:**
```
Request arrives
    ↓
Load SecurityContext from session (if exists)
    ↓
Store in SecurityContextHolder (thread-local)
    ↓
Call next filter
    ↓
After request completes:
    ↓
Save SecurityContext back to session
    ↓
Clear SecurityContextHolder
```

**Why?**
- HTTP is stateless
- Need to remember authentication across requests
- Session stores the SecurityContext
- Filter loads it for each request

**With JWT (stateless):**
```
This filter is often disabled because:
- JWT contains all auth info
- No server-side session needed
- Each request is independent
```

#### Filter 2: CsrfFilter

**Purpose:** Protect against Cross-Site Request Forgery attacks.

**What is CSRF?**
```
1. User logs into yourbank.com
2. Browser stores session cookie
3. User visits evil.com
4. evil.com has: <form action="yourbank.com/transfer">
5. Form submits automatically
6. Browser sends session cookie (automatic!)
7. Bank thinks it's the user → Transfers money!
```

**How CSRF protection works:**
```
1. Server generates random CSRF token
2. Sends token to client (in HTML or header)
3. Client must include token in all state-changing requests
4. Server validates token matches
5. evil.com can't get the token (same-origin policy)
```

**When to disable:**
```
Disable for REST APIs with JWT:
- No cookies/sessions
- JWT in Authorization header
- Evil site can't access token
- CSRF doesn't apply
```

#### Filter 3: UsernamePasswordAuthenticationFilter

**Purpose:** Handle form-based login (username + password).

**Triggers on:** POST to /login (default)

**How it works:**
```
1. Request: POST /login
   Body: username=john&password=secret

2. Filter extracts credentials

3. Creates Authentication object:
   UsernamePasswordAuthenticationToken(username, password)

4. Calls AuthenticationManager.authenticate()

5. If successful:
   - Stores Authentication in SecurityContext
   - Redirects to success URL
   - Creates session

6. If failed:
   - Redirects to /login?error
   - Shows error message
```

**For REST APIs:**
- Usually disabled
- Use custom JWT filter instead

#### Filter 4: BasicAuthenticationFilter

**Purpose:** Handle HTTP Basic Authentication.

**What is HTTP Basic Auth?**
```
Request header:
Authorization: Basic base64(username:password)

Example:
Authorization: Basic am9objpzZWNyZXQ=
                    ↑
                "john:secret" encoded
```

**How it works:**
```
1. Extract Authorization header
2. Decode Base64 → get username:password
3. Create Authentication object
4. Authenticate via AuthenticationManager
5. If valid → Store in SecurityContext
6. If invalid → 401 Unauthorized
```

**Problems:**
- ❌ Credentials sent with EVERY request
- ❌ Base64 is not encryption (easily decoded)
- ❌ Must use HTTPS
- ❌ Can't logout (browser caches credentials)

**Use cases:**
- Simple APIs
- Internal services
- When combined with HTTPS

#### Filter 5: BearerTokenAuthenticationFilter

**Purpose:** Handle token-based authentication (JWT, OAuth).

**What is Bearer Token?**
```
Request header:
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                     ↑
                  The token
```

**How it works:**
```
1. Extract Authorization header
2. Remove "Bearer " prefix → get token
3. Validate token (signature, expiration)
4. Extract claims (userId, roles)
5. Create Authentication object
6. Store in SecurityContext
7. Continue to next filter
```

**For JWT specifically:**
```
1. Parse JWT
2. Verify signature using secret key
3. Check expiration
4. Extract payload (sub, role, etc.)
5. Create custom Authentication (e.g., JwtAuthenticationToken)
6. Store in SecurityContext
```

#### Filter 6: ExceptionTranslationFilter

**Purpose:** Convert security exceptions to HTTP responses.

**How it works:**
```
Try to process request
    ↓
Catch exceptions:
    ↓
AuthenticationException → 401 Unauthorized
AccessDeniedException → 403 Forbidden
    ↓
Send appropriate HTTP response
```

**Without this filter:**
```
Exception thrown → Stack trace in response → Bad UX
```

**With this filter:**
```
Exception thrown → Clean JSON error response
{
  "error": "Unauthorized",
  "message": "Invalid token",
  "status": 401
}
```

#### Filter 7: FilterSecurityInterceptor

**Purpose:** Make final authorization decision.

**How it works:**
```
Request reaches this filter (last security filter)
    ↓
Check SecurityContext
    ↓
Is user authenticated?
    ↓
Does user have required authorities?
    ↓
Check @PreAuthorize annotations
    ↓
If authorized → Allow request
If not → Throw AccessDeniedException
```

**This is where authorization happens!**

### Custom Filter Example: JWT Authentication

**Your custom filter:**
```
Request arrives
    ↓
Extract "Authorization: Bearer <token>" header
    ↓
Validate token (signature, expiration)
    ↓
Extract userId and role from token
    ↓
Create JwtAuthenticationToken
    ↓
Store in SecurityContext
    ↓
Call chain.doFilter() → Continue to next filter
```

**Where to add it:**
```
Before UsernamePasswordAuthenticationFilter:
    → Your JWT filter runs first
    → Spring's default filters don't interfere
```

### Filter Chain Configuration

**You configure the chain in SecurityConfig:**
```
SecurityFilterChain defines:
1. Which URLs require authentication
2. Which URLs are public
3. Which filters to enable/disable
4. Where to add custom filters
5. CSRF enabled/disabled
6. Session management policy
```

**Common patterns:**
```
Pattern 1: Public + Protected endpoints
    /api/auth/** → permitAll() (public)
    /api/** → authenticated() (protected)

Pattern 2: Role-based
    /api/admin/** → hasRole('ADMIN')
    /api/user/** → hasRole('USER')
    /api/** → authenticated()

Pattern 3: Stateless (REST API)
    csrf → disabled
    sessionManagement → STATELESS
    Custom JWT filter added
```

---

<a name="authentication"></a>
## 5. Authentication Process

### Step-by-Step Flow

#### Scenario: User logs in with username/password

```
┌──────────────────────────────────────────────┐
│ Step 1: User submits credentials            │
└──────────────┬───────────────────────────────┘
               ↓
POST /login
{
  "username": "john@example.com",
  "password": "secret123"
}
               ↓
┌──────────────────────────────────────────────┐
│ Step 2: Authentication Filter intercepts    │
└──────────────┬───────────────────────────────┘
               ↓
Extract username and password from request
               ↓
┌──────────────────────────────────────────────┐
│ Step 3: Create Authentication object        │
└──────────────┬───────────────────────────────┘
               ↓
Authentication auth = new UsernamePasswordAuthenticationToken(
    username,  // "john@example.com"
    password   // "secret123"
);
auth.isAuthenticated() = false  // Not yet verified
               ↓
┌──────────────────────────────────────────────┐
│ Step 4: AuthenticationManager processes     │
└──────────────┬───────────────────────────────┘
               ↓
authenticationManager.authenticate(auth)
               ↓
Finds appropriate AuthenticationProvider
               ↓
┌──────────────────────────────────────────────┐
│ Step 5: AuthenticationProvider checks       │
└──────────────┬───────────────────────────────┘
               ↓
DaoAuthenticationProvider.authenticate()
               ↓
┌──────────────────────────────────────────────┐
│ Step 6: Load user from database             │
└──────────────┬───────────────────────────────┘
               ↓
UserDetails user = userDetailsService.loadUserByUsername("john@example.com")
               ↓
Returns UserDetails:
{
  username: "john@example.com",
  password: "$2a$10$hashed...",
  authorities: ["ROLE_USER"],
  enabled: true,
  accountNonLocked: true,
  ...
}
               ↓
┌──────────────────────────────────────────────┐
│ Step 7: Verify password                     │
└──────────────┬───────────────────────────────┘
               ↓
boolean matches = passwordEncoder.matches(
    "secret123",           // Raw password from request
    "$2a$10$hashed..."     // Hashed password from database
);
               ↓
If matches = true → Continue
If matches = false → Throw BadCredentialsException
               ↓
┌──────────────────────────────────────────────┐
│ Step 8: Create authenticated Authentication │
└──────────────┬───────────────────────────────┘
               ↓
Authentication authenticated = new UsernamePasswordAuthenticationToken(
    userDetails,            // The loaded user
    null,                   // Credentials cleared for security
    userDetails.getAuthorities()  // [ROLE_USER]
);
authenticated.isAuthenticated() = true  // Now verified!
               ↓
┌──────────────────────────────────────────────┐
│ Step 9: Store in SecurityContext            │
└──────────────┬───────────────────────────────┘
               ↓
SecurityContextHolder.getContext().setAuthentication(authenticated)
               ↓
Now accessible anywhere in this request thread!
               ↓
┌──────────────────────────────────────────────┐
│ Step 10: Generate response                  │
└──────────────┬───────────────────────────────┘
               ↓
For session-based:
    - Create session
    - Store SecurityContext in session
    - Return success response

For JWT-based:
    - Generate JWT token
    - Include userId, roles in token
    - Return token to client
               ↓
Response: {
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": { "id": "123", "email": "john@example.com" }
}
```

### Authentication Failures

**Different exceptions, different meanings:**

```
UsernameNotFoundException
    → User doesn't exist
    → Don't reveal this! (security risk)
    → Return generic "Invalid credentials"

BadCredentialsException
    → Wrong password
    → Return generic "Invalid credentials"

AccountExpiredException
    → Account expired
    → Return "Account has expired"

LockedException
    → Account locked (brute force protection)
    → Return "Account is locked"

DisabledException
    → Account disabled
    → Return "Account is disabled"

CredentialsExpiredException
    → Password expired
    → Return "Password has expired, please reset"
```

### Why Generic Messages?

**Security principle: Don't leak information**

```
❌ Bad: "User not found"
    → Attacker knows email exists or not
    → Can enumerate valid emails

✅ Good: "Invalid email or password"
    → Attacker can't tell if email exists
    → Can't enumerate users
```

---

<a name="authorization"></a>
## 6. Authorization Process

### Authentication vs Authorization

```
Authentication: "Who are you?"
    └─ Proves identity
    └─ Login process
    └─ Results in Authentication object

Authorization: "What can you do?"
    └─ Checks permissions
    └─ Happens after authentication
    └─ Uses authorities/roles
```

### How Authorization Works

```
User is authenticated
    ↓
Has Authentication object with authorities
    ↓
User tries to access protected resource
    ↓
Spring Security checks authorization
    ↓
If authorized → Allow access
If not → 403 Forbidden
```

### Authorization Methods

#### Method 1: URL-based (SecurityFilterChain)

**Configure in SecurityConfig:**
```
Declarative: Specify rules in config

Examples:
.requestMatchers("/admin/**").hasRole("ADMIN")
    → /admin/** requires ADMIN role

.requestMatchers("/api/**").authenticated()
    → /api/** requires any authenticated user

.requestMatchers("/public/**").permitAll()
    → /public/** accessible to everyone
```

**How it works:**
```
Request: GET /admin/users
    ↓
FilterSecurityInterceptor checks rules
    ↓
Finds: /admin/** requires ADMIN role
    ↓
Checks: Does user have ROLE_ADMIN?
    ↓
If yes → Allow
If no → 403 Forbidden
```

#### Method 2: Method Security (@PreAuthorize)

**Annotations on methods:**
```
@PreAuthorize("hasRole('ADMIN')")
public List<User> getAllUsers() {
    // Only ADMIN can call this
}
```

**How it works:**
```
Spring creates AOP proxy around method
    ↓
Before method executes:
    ↓
Evaluate @PreAuthorize expression
    ↓
If true → Call actual method
If false → Throw AccessDeniedException
```

**Common expressions:**

```
@PreAuthorize("hasRole('ADMIN')")
    → User must have ROLE_ADMIN

@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    → User must have ADMIN OR MODERATOR

@PreAuthorize("hasAuthority('WRITE_PRIVILEGE')")
    → User must have specific authority

@PreAuthorize("isAuthenticated()")
    → User must be authenticated (any role)

@PreAuthorize("permitAll()")
    → Anyone can access (even anonymous)

@PreAuthorize("#username == authentication.name")
    → User can only access their own data
    → Example: updateProfile(@PathVariable String username)

@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    → Admin can access anyone's data
    → Regular user can only access their own
```

#### Method 3: Programmatic

**Check authorization in code:**
```
Get current authentication:
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

Check role:
    boolean isAdmin = auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

Check specific permission:
    if (!isAdmin) {
        throw new AccessDeniedException("Access denied");
    }
```

**When to use:**
- Complex business logic
- Dynamic permissions
- Database-driven authorization

### Role vs Authority

**Confusion alert!** These terms are used interchangeably, but have subtle differences:

```
Authority: General permission
    → "READ_PRIVILEGE"
    → "WRITE_PRIVILEGE"
    → "DELETE_USER"

Role: Collection of authorities
    → "ROLE_ADMIN" (has all privileges)
    → "ROLE_USER" (has read/write)
    → "ROLE_GUEST" (has read only)
```

**Spring Security convention:**
```
Roles are authorities that start with "ROLE_" prefix

hasRole('ADMIN')
    ↓
Internally checks for: "ROLE_ADMIN" authority

hasAuthority('ROLE_ADMIN')
    ↓
Same result, but explicit
```

**Best practice:**
```
Use roles for general access levels:
    - ROLE_ADMIN
    - ROLE_USER
    - ROLE_GUEST

Use authorities for specific permissions:
    - READ_REPORTS
    - APPROVE_TRANSACTIONS
    - MANAGE_USERS
```

### Authorization with Ownership

**Common requirement: "Users can only edit their own data"**

```
@PreAuthorize("#id == authentication.principal.userId")
@PutMapping("/users/{id}")
public User updateUser(@PathVariable String id, @RequestBody UserDTO dto) {
    // Method only executes if id matches authenticated user's id
}
```

**How it works:**
```
Request: PUT /users/user-123
Token contains: userId = "user-123"

Expression evaluation:
    #id → "user-123" (from path variable)
    authentication.principal.userId → "user-123" (from token)
    "user-123" == "user-123" → true ✓
    
Method executes!
```

**Failed authorization:**
```
Request: PUT /users/user-456
Token contains: userId = "user-123"

Expression evaluation:
    #id → "user-456"
    authentication.principal.userId → "user-123"
    "user-456" == "user-123" → false ✗
    
AccessDeniedException → 403 Forbidden
Method never executes!
```

### Admin Override Pattern

**Admins can access everything, users can access only their own:**

```
@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
@PutMapping("/users/{id}")
public User updateUser(@PathVariable String id, @RequestBody UserDTO dto) {
    // ADMIN: Can update anyone
    // USER: Can only update themselves
}
```

**Evaluation:**
```
For ADMIN:
    hasRole('ADMIN') → true ✓
    (OR is short-circuit, second condition not evaluated)
    Authorized!

For USER updating their own:
    hasRole('ADMIN') → false
    #id == authentication.principal.userId → true ✓
    Authorized!

For USER updating someone else:
    hasRole('ADMIN') → false
    #id == authentication.principal.userId → false
    Not authorized! → 403
```

---

<a name="security-context"></a>
## 7. SecurityContext & Thread-Local Storage

### The Problem

**HTTP is stateless:**
```
Request 1: Who is the user?
Request 2: Who is the user? (Doesn't remember Request 1!)
Request 3: Who is the user? (Doesn't remember either!)
```

**We need a way to:**
1. Store authentication for the current request
2. Access it anywhere in the code
3. Ensure thread-safety (multiple requests simultaneously)
4. Automatically clean up after request

### The Solution: SecurityContext

**SecurityContext is a holder for authentication information.**

```
SecurityContext
    └─ Authentication
        ├─ Principal (user details)
        ├─ Credentials (password/token)
        └─ Authorities (roles/permissions)
```

### How to Access It

```
Anywhere in your code:

// Get SecurityContext
SecurityContext context = SecurityContextHolder.getContext();

// Get Authentication
Authentication auth = context.getAuthentication();

// Get user details
Object principal = auth.getPrincipal();