# Chrona - Developer Onboarding & AI Handoff Guide

**Welcome to Chrona!**
This document serves as the definitive guide for any AI agent or developer joining the project. It outlines the system architecture, development workflows, current status, and strategic goals. **Read this carefully before starting any task.**

---

## 1. Project Mission
**Chrona** is a SaaS project management and financial tracking tool designed specifically for architecture firms.
**Goal:** Empower firms to track time, manage project phases, and analyze financial health (profitability, costs, burn rates) in real-time.

---

## 2. Technology Stack

### Backend (`chrona-backend`)
- **Language:** Java 17
- **Framework:** Spring Boot 3.1.5
- **Database:** PostgreSQL 15 (Multi-tenant via Schemas)
- **ORM:** Hibernate / Spring Data JPA
- **Migration:** Flyway
- **Security:** Spring Security + JWT
- **Build Tool:** Maven

### Frontend (`chrona-frontend`)
- **Framework:** Angular 17+
- **Styling:** TailwindCSS (Utility-first)
- **State Management:** Angular Signals (No NgRx unless necessary)
- **HTTP:** RxJS `HttpClient` with Interceptors

### Infrastructure
- **Containerization:** Docker & Docker Compose
- **Local Dev:** `docker-compose.yml` orchestrates DB, Backend, and Frontend.

---

## 3. Architecture & Key Patterns

### 3.1. Multi-Tenancy (CRITICAL)
We use a **Schema-per-Tenant** strategy. This is the most complex part of the system.
- **Tenant Identification:** Every request MUST have an `X-Tenant-ID` header.
- **Tenant Context:**
  - `TenantFilter`: Intercepts the request *before* Spring Security.
  - `TenantContext`: ThreadLocal storage for the current Tenant ID.
  - `TenantConnectionProvider`: Hibernate uses this to switch the PostgreSQL `SEARCH_PATH` to the correct schema (e.g., `tenant_demo`, `tenant_acme`).
- **Public vs. Tenant Data:**
  - `public` schema: Stores `tenants` table (registry of all tenants).
  - `tenant_xyz` schemas: Store application data (users, projects, tasks, etc.).

### 3.2. Security & Authentication
- **Flow:**
  1. Frontend sends credentials + `X-Tenant-ID` to `/auth/login`.
  2. Backend validates user *within that specific tenant schema*.
  3. Returns JWT.
  4. Frontend attaches JWT + `X-Tenant-ID` to all subsequent requests via `AuthInterceptor`.
- **Filter Chain Order (Fixed):**
  `TenantFilter` -> `SecurityContextHolderFilter` -> `JwtAuthenticationFilter` -> ...
  *Why?* We need to know *which* database schema to look in before we can try to find the user for authentication.

### 3.3. Frontend Architecture
- **Standalone Components:** All new components must be `standalone: true`.
- **Signals:** Use Signals for local state and computed values.
- **Services:** Logic resides in services (`AuthService`, `UserService`), not components.

---

## 4. Development Workflow

### 4.1. Prerequisites
- Docker & Docker Compose
- Java 17 SDK (optional if using Docker)
- Node.js 18+ (optional if using Docker)

### 4.2. Running Locally
We use Docker Compose for a consistent environment.

```bash
# Start all services (Database, Backend, Frontend)
docker compose up -d --build

# View Logs
docker logs -f chrona-backend
docker logs -f chrona-frontend
```

### 4.3. Database Management
- **Flyway** handles migrations.
- When adding a new table, create a migration in `src/main/resources/db/migration`.
- **Note:** Migrations run automatically on startup for the `public` schema and all tenant schemas.

---

## 5. Current State & Active Issues (2025-11-23)

### ✅ What's Working
- **Frontend Auth:** `X-Tenant-ID` is correctly handled in `AuthInterceptor`.
- **Backend Config:** `TenantFilter` is correctly registered in `SecurityConfig`.
- **Linting:** Backend code is clean of null-safety warnings.

### 🚧 Critical Blocker: Backend Build Loop
The backend container is currently failing to start with `mvn spring-boot:run`.

**Error:**
```
Error: Could not find or load main class com.chrona.ChronaBackendApplication
Caused by: java.lang.ClassNotFoundException: com.chrona.ChronaBackendApplication
```

**Context:**
- We switched to `Dockerfile.dev` to enable hot-reloading (using `mvn spring-boot:run`).
- We removed the `pom.xml` volume mount to fix a plugin version issue.
- Dependencies download successfully, but execution fails.

**Hypothesis:**
- `mvn spring-boot:run` might not be triggering a compilation phase when run on a fresh volume without a prior `package` or `compile` step.
- The classpath might be misconfigured in the Docker container.

### 📋 Next Steps (Task Roadmap)
1.  **Fix Backend Startup:**
    -   Try changing `CMD` to `["./mvnw", "spring-boot:run"]` (exec form) or explicitly run `compile` before run.
    -   Verify `target/classes` exists inside the container.
2.  **Verify `/bootstrap`:** Once backend is up, confirm the `/bootstrap` endpoint returns data without "relation users does not exist" error.
3.  **Tenant Provisioning:** Implement the "Create Tenant" flow.
4.  **Financial Features:** Implement "User Hourly Rate" and "Project Budget" logic.

---

## 6. Guidelines for AI Agents
- **Context is King:** Always check `task.md` and this document before starting.
- **Don't Break the Build:** If you change `pom.xml` or `Dockerfile`, verify the build locally (or via Docker logs) immediately.
- **Multi-Tenancy First:** Always ask: "Does this change respect the current tenant context?"
- **Communication:** Update this document if you make architectural changes.

---
*Last Updated: 2025-11-23*
