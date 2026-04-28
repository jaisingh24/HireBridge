# HireBridge — Job Portal Backend

> A production-grade, role-based Job Portal REST API built with Java and Spring Boot. Supports three distinct user roles — Candidate, Recruiter, and Admin — with JWT authentication, dynamic job search, resume upload, and a full application lifecycle management system.

---

## 📌 Table of Contents

- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [System Architecture](#system-architecture)
- [Database Schema](#database-schema)
- [API Endpoints](#api-endpoints)
- [Application Status Lifecycle](#application-status-lifecycle)
- [Security Implementation](#security-implementation)
- [File Upload — Resume Handling](#file-upload--resume-handling)
- [Dynamic Job Search & Filtering](#dynamic-job-search--filtering)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [Deployment](#deployment)
- [Key Metrics](#key-metrics)
- [Future Improvements](#future-improvements)
- [Author](#author)

---

## Project Overview

**HireBridge** is a fully functional backend system for a job recruitment platform — similar to Naukri or LinkedIn Jobs — built entirely with Java and Spring Boot. It provides a secure, scalable REST API where:

- **Candidates** can register, build profiles, upload resumes, search jobs, apply, and track application status
- **Recruiters** can post job listings, manage applicants, and move candidates through a hiring pipeline
- **Admins** can approve companies, manage users, and monitor platform analytics

Every design decision prioritizes clean architecture, security, and real-world production patterns.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (JJWT) |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8.0 |
| File Storage | Cloudinary (free tier) / Local FileSystem |
| Build Tool | Maven |
| API Testing | Postman |
| Deployment | Railway.app |
| Version Control | Git + GitHub |

---

## Features

### Candidate
- Register and login with JWT-based authentication
- Build and update profile (skills, experience, education, location)
- Upload resume as PDF (stored on Cloudinary)
- Search jobs with 6+ dynamic filters (location, salary, skills, job type, experience)
- Paginated job listings with sort support
- Apply to jobs with one click
- Track real-time application status across 5 stages
- Withdraw application before screening

### Recruiter
- Register under a Company account
- Create, edit, and close job listings
- View all applicants for each job posting
- Move candidate through hiring pipeline (Screening → Interview → Offered / Rejected)
- Download candidate resume via secure time-limited URL
- Add notes against each application

### Admin
- Approve or reject company registrations
- Ban/unban users
- View platform-wide analytics (total jobs, applications, active users)
- Manage all job listings across the platform

### System
- Role-based access control (RBAC) with `@PreAuthorize`
- BCrypt password hashing
- Global exception handling with consistent error responses
- Input validation with Bean Validation (`@Valid`)
- Full audit trail for every application status change
- Paginated responses on all list endpoints

---

## System Architecture

```
Client (Postman / Frontend)
         │
         ▼
┌─────────────────────────┐
│   JWT Auth Filter        │  ← Validates token on every request
│   Spring Security        │  ← Role-based route protection
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│   Controller Layer       │  ← REST Controllers (AuthController,
│                          │    JobController, ApplicationController,
│                          │    ProfileController, AdminController)
└────────────┬────────────┘
             │
             ▼
┌─────────────────────────┐
│   Service Layer          │  ← Business logic, validations,
│                          │    file handling, status transitions
└────────────┬────────────┘
             │
        ┌────┴────┐
        ▼         ▼
┌──────────┐  ┌──────────┐
│  MySQL   │  │Cloudinary│
│ Database │  │(Resumes) │
└──────────┘  └──────────┘
```

---

## Database Schema

### `users`
```sql
id            BIGINT PRIMARY KEY AUTO_INCREMENT
name          VARCHAR(100) NOT NULL
email         VARCHAR(100) UNIQUE NOT NULL
password      VARCHAR(255) NOT NULL          -- BCrypt hashed
role          ENUM('CANDIDATE','RECRUITER','ADMIN')
phone         VARCHAR(15)
is_active     BOOLEAN DEFAULT TRUE
created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

### `candidate_profiles`
```sql
id                BIGINT PRIMARY KEY AUTO_INCREMENT
user_id           BIGINT FK → users(id)
summary           TEXT
skills            TEXT                        -- comma-separated
experience_years  INT
education         VARCHAR(255)
location          VARCHAR(100)
resume_url        VARCHAR(500)               -- Cloudinary URL
profile_complete  BOOLEAN DEFAULT FALSE
```

### `companies`
```sql
id           BIGINT PRIMARY KEY AUTO_INCREMENT
name         VARCHAR(200) NOT NULL
description  TEXT
website      VARCHAR(255)
location     VARCHAR(100)
logo_url     VARCHAR(500)
is_approved  BOOLEAN DEFAULT FALSE
created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

### `recruiter_profiles`
```sql
id           BIGINT PRIMARY KEY AUTO_INCREMENT
user_id      BIGINT FK → users(id)
company_id   BIGINT FK → companies(id)
designation  VARCHAR(100)
```

### `jobs`
```sql
id               BIGINT PRIMARY KEY AUTO_INCREMENT
title            VARCHAR(200) NOT NULL
description      TEXT NOT NULL
company_id       BIGINT FK → companies(id)
recruiter_id     BIGINT FK → users(id)
location         VARCHAR(100)
job_type         ENUM('FULL_TIME','PART_TIME','INTERNSHIP','CONTRACT')
experience_min   INT
experience_max   INT
salary_min       BIGINT
salary_max       BIGINT
skills_required  TEXT
status           ENUM('DRAFT','OPEN','CLOSED') DEFAULT 'OPEN'
created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
expires_at       TIMESTAMP
```

### `applications`
```sql
id            BIGINT PRIMARY KEY AUTO_INCREMENT
candidate_id  BIGINT FK → users(id)
job_id        BIGINT FK → jobs(id)
status        ENUM('APPLIED','SCREENING','INTERVIEW',
                   'OFFERED','REJECTED','WITHDRAWN')
              DEFAULT 'APPLIED'
applied_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
updated_at    TIMESTAMP
recruiter_notes TEXT
UNIQUE(candidate_id, job_id)               -- one application per job
```

### `application_status_history`
```sql
id              BIGINT PRIMARY KEY AUTO_INCREMENT
application_id  BIGINT FK → applications(id)
old_status      VARCHAR(50)
new_status      VARCHAR(50)
changed_by      BIGINT FK → users(id)
changed_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
remarks         TEXT
```

### Entity Relationships
```
users ──────────── candidate_profiles   (1:1)
users ──────────── recruiter_profiles   (1:1)
recruiter_profiles ─── companies        (Many:1)
companies ──────── jobs                 (1:Many)
users ──────────── applications         (1:Many) [as candidate]
jobs ───────────── applications         (1:Many)
applications ───── application_status_history (1:Many)
```

---

## API Endpoints

### Auth — Public
```
POST   /api/auth/register          Register as Candidate or Recruiter
POST   /api/auth/login             Login and receive JWT token
```

### Candidate Endpoints
```
GET    /api/profile/me             View own profile
PUT    /api/profile/update         Update profile details
POST   /api/profile/resume         Upload resume (PDF)

GET    /api/jobs                   Search & filter jobs (see filters below)
GET    /api/jobs/{id}              View single job details

POST   /api/jobs/{id}/apply        Apply to a job
GET    /api/applications/my        View all my applications + status
DELETE /api/applications/{id}/withdraw   Withdraw an application
```

### Recruiter Endpoints
```
POST   /api/jobs                         Create new job listing
PUT    /api/jobs/{id}                    Edit job listing
PATCH  /api/jobs/{id}/close              Close a job listing
GET    /api/jobs/my                      View own job listings

GET    /api/jobs/{id}/applicants         View all applicants for a job
PATCH  /api/applications/{id}/status    Move application to next stage
GET    /api/applications/{id}/resume    Get candidate resume URL
POST   /api/applications/{id}/notes     Add recruiter notes
```

### Admin Endpoints
```
GET    /api/admin/companies/pending      List unapproved companies
PATCH  /api/admin/companies/{id}/approve   Approve a company
PATCH  /api/admin/companies/{id}/reject    Reject a company
PATCH  /api/admin/users/{id}/ban           Ban a user
PATCH  /api/admin/users/{id}/unban         Unban a user
GET    /api/admin/stats                    Platform analytics
GET    /api/admin/jobs                     View all jobs
```

### Job Search Query Parameters
```
GET /api/jobs?location=Jaipur
             &skills=Java,Spring
             &jobType=FULL_TIME
             &salaryMin=500000
             &salaryMax=1500000
             &experienceMin=0
             &experienceMax=2
             &page=0
             &size=10
             &sortBy=created_at
             &direction=DESC
```

All parameters are optional. Only provided filters are applied dynamically.

---

## Application Status Lifecycle

```
                    ┌─────────┐
                    │ APPLIED │ ◄── Candidate applies
                    └────┬────┘
                         │ Recruiter reviews
                         ▼
                   ┌──────────┐
                   │SCREENING │
                   └────┬─────┘
                        │ Shortlisted
                        ▼
                  ┌───────────┐
                  │ INTERVIEW │
                  └─────┬─────┘
                        │
              ┌─────────┴──────────┐
              ▼                    ▼
          ┌────────┐          ┌──────────┐
          │OFFERED │          │ REJECTED │
          └────────┘          └──────────┘

  WITHDRAWN ◄── Candidate can withdraw from APPLIED stage only
```

Every transition is recorded in `application_status_history` with timestamp, actor, and optional remarks. This gives a full audit trail of the hiring process.

---

## Security Implementation

### JWT Flow
```
1. User submits email + password to POST /api/auth/login
2. Spring Security validates credentials
3. Server generates JWT token (signed with secret key, expires in 24h)
4. Client stores token and sends it in every request header:
   Authorization: Bearer <token>
5. JwtAuthFilter intercepts every request, validates token,
   extracts userId and role, sets SecurityContext
6. @PreAuthorize annotations enforce role-based access per endpoint
```

### Password Security
- All passwords hashed with **BCrypt** (strength factor 12) before storing
- Plain passwords never stored or logged anywhere

### Role-Based Access Control
```java
@PreAuthorize("hasRole('RECRUITER')")   // Recruiter-only endpoints
@PreAuthorize("hasRole('CANDIDATE')")   // Candidate-only endpoints
@PreAuthorize("hasRole('ADMIN')")       // Admin-only endpoints
```

---

## File Upload — Resume Handling

### Using Cloudinary (Recommended)
```
1. Candidate sends PDF file to POST /api/profile/resume
2. FileStorageService validates file:
   - Must be PDF
   - Max size 5MB
3. File uploaded to Cloudinary under folder: hirebridge/resumes/
4. Cloudinary returns a permanent secure_url
5. URL saved to candidate_profiles.resume_url
6. Recruiter requests resume → backend returns Cloudinary URL directly
```

### Using Local Storage (Development)
```
1. Same validation applied
2. File saved to: uploads/resumes/{userId}_{timestamp}.pdf
3. Served via Spring's static resource handler
4. Path stored in DB as relative URL
```

---

## Dynamic Job Search & Filtering

Job search uses **JPA Specifications** to build queries dynamically. Only the filters the user provides are included in the SQL WHERE clause — no hardcoded conditions.

```java
// Example of what gets generated for:
// ?location=Jaipur&skills=Java&salaryMin=600000&jobType=FULL_TIME

SELECT * FROM jobs
WHERE location LIKE '%Jaipur%'
  AND skills_required LIKE '%Java%'
  AND salary_min >= 600000
  AND job_type = 'FULL_TIME'
  AND status = 'OPEN'
  AND expires_at > NOW()
ORDER BY created_at DESC
LIMIT 10 OFFSET 0;
```

Results are always paginated with metadata:
```json
{
  "content": [...],
  "currentPage": 0,
  "totalPages": 8,
  "totalElements": 76,
  "pageSize": 10,
  "hasNext": true,
  "hasPrevious": false
}
```

---

## Project Structure

```
hirebridge/
├── src/main/java/com/hirebridge/
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── JobController.java
│   │   ├── ApplicationController.java
│   │   ├── ProfileController.java
│   │   └── AdminController.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── JobService.java
│   │   ├── ApplicationService.java
│   │   ├── ProfileService.java
│   │   ├── FileStorageService.java
│   │   └── AdminService.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── JobRepository.java
│   │   ├── ApplicationRepository.java
│   │   ├── CandidateProfileRepository.java
│   │   └── CompanyRepository.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Job.java
│   │   ├── Application.java
│   │   ├── ApplicationStatusHistory.java
│   │   ├── CandidateProfile.java
│   │   ├── RecruiterProfile.java
│   │   └── Company.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   ├── JobCreateRequest.java
│   │   │   └── ApplicationStatusRequest.java
│   │   └── response/
│   │       ├── AuthResponse.java
│   │       ├── JobResponse.java
│   │       ├── ApplicationResponse.java
│   │       └── PagedResponse.java
│   ├── security/
│   │   ├── JwtUtil.java
│   │   ├── JwtAuthFilter.java
│   │   └── SecurityConfig.java
│   ├── specification/
│   │   └── JobSpecification.java
│   ├── config/
│   │   └── CloudinaryConfig.java
│   └── exception/
│       ├── GlobalExceptionHandler.java
│       ├── ResourceNotFoundException.java
│       ├── UnauthorizedException.java
│       └── DuplicateApplicationException.java
├── src/main/resources/
│   ├── application.yml
│   └── application-prod.yml
├── uploads/                        ← local resume storage (dev only)
├── pom.xml
└── README.md
```

---

## Getting Started

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven 3.8+
- Git

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/hirebridge.git
cd hirebridge
```

### 2. Create MySQL Database
```sql
CREATE DATABASE hirebridge_db;
```

### 3. Configure Environment Variables
Create a `.env` file or set the following in `application.yml` (see Environment Variables section below).

### 4. Run the Application
```bash
mvn spring-boot:run
```

The server starts at `http://localhost:8080`

### 5. Test with Postman
Import the Postman collection from `/postman/Hirebridge.postman_collection.json` and start testing all endpoints.

---

## Environment Variables

```yaml
# application.yml

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hirebridge_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

jwt:
  secret: ${JWT_SECRET}           # min 256-bit secret key
  expiration: 86400000            # 24 hours in milliseconds

cloudinary:
  cloud-name: ${CLOUDINARY_CLOUD_NAME}
  api-key: ${CLOUDINARY_API_KEY}
  api-secret: ${CLOUDINARY_API_SECRET}

file:
  upload-dir: uploads/resumes/    # for local storage in dev
  max-size: 5242880               # 5MB in bytes
```

---

## Deployment

### Free Deployment Stack (Zero Cost)

**Database — PlanetScale**
```
1. Create free account at planetscale.com
2. Create database: hirebridge_db
3. Copy connection string into application-prod.yml
```

**App — Railway.app**
```
1. Push code to GitHub
2. Connect Railway to your GitHub repo
3. Add environment variables in Railway dashboard
4. Railway auto-detects Spring Boot and deploys
5. Get a live URL: hirebridge.up.railway.app
```

**File Storage — Cloudinary**
```
1. Create free account at cloudinary.com
2. Copy Cloud Name, API Key, API Secret
3. Add to Railway environment variables
```

Your full production setup — free, deployed, live URL ready for resume.

---

## Key Metrics

| Metric | Value |
|---|---|
| Total REST Endpoints | 20+ |
| User Roles | 3 (Candidate, Recruiter, Admin) |
| Database Tables | 7 |
| Job Filter Parameters | 6+ |
| Application Stages | 5 |
| Max Resume Size | 5MB |
| JWT Token Expiry | 24 hours |
| Response Time (GET) | < 200ms |
| Pagination | All list endpoints |
| Audit Trail | Full status history |

---

## Future Improvements

- [ ] Email notifications on application status change (JavaMailSender)
- [ ] Redis caching for job search results
- [ ] Elasticsearch integration for full-text job search
- [ ] Rate limiting on auth endpoints
- [ ] Swagger / OpenAPI documentation
- [ ] Unit and integration tests (JUnit 5 + Mockito)
- [ ] Admin analytics dashboard with charts
- [ ] Saved jobs / bookmarks feature for candidates
- [ ] Recruiter can schedule interviews with calendar link

---

## Author

**Jai Singh Katiyar**
- 📧 jaikatiyar24@gmail.com
- 🔗 [LinkedIn](https://linkedin.com/in/jai-singhkatiyar)
- 💻 [GitHub](https://github.com/yourusername)
- 📍 Jaipur, Rajasthan, India

---

> Built with ❤️ using Java & Spring Boot
