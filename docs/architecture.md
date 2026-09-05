# Architecture

What are the moving pieces, and how do they talk to each other?

The system is a lean modular monolith with a React frontend and a Spring Boot REST backend.

---

## Frontend

The frontend is built with **React**, **TypeScript**, **Vite**, **Tailwind CSS**, and **React Router**.

### Main Responsibilities

- Authentication and session-aware UI
- Manager and contractor navigation
- Unit management
- Maintenance request management
- Contractor assignment
- Maintenance lifecycle updates
- Maintenance search and filtering
- Rent recording and rent-roll display
- Dashboard and rent-alert views

### Communication

The frontend communicates with the backend through REST APIs under `/api`.

- **HTTP Client**: Axios
- **Session Management**: Requests include credentials so the server-side HTTP session cookie is sent with API requests

---

## Backend

The backend is a **Java 21 Spring Boot** application organized into domain modules:

- `auth`
- `security`
- `user`
- `unit`
- `rent`
- `maintenance`
- `dashboard`
- `alert`

### Request Flow

```
Controller → Service → Repository → PostgreSQL
```

- **Controllers**: Expose REST endpoints and handle HTTP concerns
- **Services**: Contain business rules and authorization-sensitive operations
- **Repositories**: Use Spring Data JPA to access PostgreSQL
- **Security**: Spring Security provides authentication and server-side role enforcement
  - Manager-only operations are protected with Spring Security method authorization
  - Frontend route restrictions provide additional UX protection

---

## Database

**PostgreSQL** stores the application data.

### Schema Management

- **Flyway** owns database schema migrations
- **Hibernate** is configured with `ddl-auto=validate` (validates schema rather than creating/modifying it)

### Key Relationships

| Relationship | Description |
|---|---|
| Units ↔ Rent Payments | One-to-many |
| Units ↔ Maintenance Requests | One-to-many |
| Maintenance Requests ↔ Contractors | Many-to-many via `maintenance_assignments` |
| Maintenance Requests ↔ Timeline | One-to-many (append-only) |
| Units ↔ Rent Alerts | One-to-many |

---

## Deployment Architecture

### Local Development

```
Browser
   │
   │ HTTP
   ▼
React/Vite frontend :5173
   │
   │ /api proxy
   ▼
Spring Boot backend :8080
   │
   │ JDBC / JPA
   ▼
PostgreSQL :5432
```

- **Vite dev server**: Serves React application and proxies `/api` requests to Spring Boot
- **Spring Boot**: Packaged Java application connecting to PostgreSQL using environment-provided credentials
- **PostgreSQL**: Application data persistence

### Production Deployment

- Frontend and backend hosted as separate application services
- PostgreSQL as persistent database
- Secrets (database credentials) supplied through environment variables

---

## Request Flow Example: Manager Assigns Contractor to Maintenance Request

1. Manager logs into React application
2. Frontend sends `POST /api/auth/login` with manager credentials
3. Spring Security authenticates manager, stores context in HTTP session
4. Manager opens maintenance request in React UI
5. Frontend sends request to maintenance assignment endpoint with contractor ID
6. Spring Security checks for `PROPERTY_MANAGER` role
7. `MaintenanceRequestController` receives HTTP request
8. `MaintenanceRequestService` validates request and contractor:
   - Creates `MaintenanceAssignment`
   - Records assignment event in maintenance timeline
9. `MaintenanceAssignmentRepository` persists assignment
10. Timeline repository persists immutable timeline event
11. PostgreSQL stores changes
12. Backend maps entities to response DTOs
13. Frontend receives response and updates maintenance request view

**Note**: The same Controller → Service → Repository pattern applies to all domain operations, with business rules enforced on the server rather than relying on the frontend.

---

## Architectural Decisions

### ✅ HTTP Session/Cookie Authentication (not JWT)

**Why**: Browser-based application without stateless API requirement. Server-side sessions enable simpler role enforcement and session management.

### ✅ Modular Monolith (not Microservices)

**Why**: Assignment scope is small enough that a monolith provides simpler development, deployment, debugging, and transactional consistency.

### ✅ Server-Side Search & Filtering (not Client-Side)

**Why**: Backend performs search, filtering, sorting, and pagination so the API returns only the requested page and total count without loading all requests into the browser.

### ✅ Explicit `maintenance_assignments` Entity (not Implicit Many-to-Many)

**Why**: Provides a place to store assignment metadata (e.g., `assigned_at` timestamp) and maintains clear visibility of the data model.

### ✅ Flyway Migrations (not Hibernate-Managed Schema)

**Why**: Explicit, versioned database changes with Hibernate validating the resulting schema provide better production control and auditability.

### ✅ Server-Side Authorization (not Client-Side Only)

**Why**: Frontend hides manager-only navigation for UX, but actual authorization is enforced server-side with Spring Security. Contractors cannot bypass frontend restrictions.

### ✅ Explicit REST Controllers (not GraphQL or Spring Data REST)

**Why**: Assignment requires conventional REST API. Explicit controllers keep API surface and authorization rules visible in the application code.

### ✅ PostgreSQL (not H2 or In-Memory Database)

**Why**: Persistence, relational constraints, migrations, rent-payment uniqueness, assignments, and historical maintenance data are core to the application.

### ✅ React Context API (not Redux)

**Why**: Current application state is small enough to manage with React state and context without global state-management overhead.

### ✅ Spring MVC (not WebFlux)

**Why**: Conventional CRUD/business application without reactive processing requirements.
