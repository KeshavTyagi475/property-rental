# Architecture

What are the moving pieces, and how do they talk to each other?

The system is a lean modular monolith with a React frontend and a Spring Boot REST backend.

Frontend

The frontend is built with React, TypeScript, Vite, Tailwind CSS, and React Router.

Main responsibilities:





Authentication and session-aware UI



Manager and contractor navigation



Unit management



Maintenance request management



Contractor assignment



Maintenance lifecycle updates



Maintenance search and filtering



Rent recording and rent-roll display



Dashboard and rent-alert views

The frontend communicates with the backend through REST APIs under /api.

Axios is used as the HTTP client. Requests include credentials so the server-side HTTP session cookie is sent with API requests.

Backend

The backend is a Java 21 Spring Boot application.

It is organized into domain modules such as:





auth



security



user



unit



rent



maintenance



dashboard



alert

The main request flow follows:


Controller → Service → Repository → PostgreSQL

Controllers expose REST endpoints and handle HTTP concerns. Services contain business rules and authorization-sensitive operations. Repositories use Spring Data JPA to access PostgreSQL.

Spring Security provides authentication and server-side role enforcement. Manager-only operations are protected with Spring Security method authorization as well as frontend route restrictions.

### Database

PostgreSQL stores the application data.

Flyway owns database schema migrations. Hibernate is configured with `ddl-auto=validate`, so Hibernate validates the schema rather than creating or modifying it.

Important relationships include:

- Units have rent payments.
- Maintenance requests belong to exactly one unit.
- Maintenance requests can have many contractor assignments.
- Contractors can be assigned to many maintenance requests through the explicit `maintenance_assignments` join entity.
- Maintenance requests have an append-only timeline.
- Rent alerts are associated with units and months.

## Where does each piece run?

During local development:

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
  
The Vite development server serves the React application and proxies `/api` requests to the Spring Boot application.

The Spring Boot application runs as a packaged Java application and connects to PostgreSQL using environment-provided database credentials.

For deployment, the frontend and backend can be hosted as application services with PostgreSQL as the persistent database. Secrets such as database credentials are supplied through environment variables rather than committed to Git.

## What is the request path for one representative user action, end to end?

Consider a manager assigning a contractor to a maintenance request.

1. The manager logs into the React application.
2. The frontend sends `POST /api/auth/login` with the manager credentials.
3. Spring Security authenticates the manager and stores the authenticated security context in the HTTP session.
4. The manager opens a maintenance request in the React UI.
5. The frontend sends a request to the maintenance assignment endpoint with the contractor ID.
6. Spring Security checks that the authenticated user has the `PROPERTY_MANAGER` role.
7. `MaintenanceRequestController` receives the HTTP request.
8. `MaintenanceRequestService` validates the request and contractor, creates the `MaintenanceAssignment`, and records an assignment event in the maintenance timeline.
9. `MaintenanceAssignmentRepository` persists the assignment and the timeline repository persists the immutable timeline event.
10. PostgreSQL stores the changes.
11. The backend maps the entities to response DTOs.
12. The frontend receives the response and updates the maintenance request view.

The same Controller → Service → Repository path is used for other domain operations, with business rules enforced on the server rather than relying on the frontend.

## What did you decide not to build, and why?

### JWT authentication

We used HTTP session/cookie authentication instead of JWT because this is a browser-based application and the assignment does not require a stateless API. Server-side sessions also make role enforcement straightforward.

### Microservices

We did not split the application into microservices. The assignment is small enough that a modular monolith provides simpler development, deployment, debugging, and transaction handling.

### Client-side maintenance filtering

We did not load all maintenance requests into the browser and filter them there. Search, filtering, sorting, and pagination are performed by the backend so the API can return the requested page and total count.

### Separate assignment microservice or implicit many-to-many mapping

We used an explicit `maintenance_assignments` entity rather than hiding the relationship behind a JPA many-to-many mapping. This gives the system a place to store assignment metadata such as `assigned_at` and supports assignment/unassignment timeline events.

### Hibernate-managed schema creation

We did not use Hibernate to create the production schema. Flyway migrations provide explicit, versioned database changes, while Hibernate only validates the resulting schema.

### Client-side authorization as the security boundary

The frontend hides manager-only navigation from contractors, but this is only a usability layer. Actual authorization is enforced on the backend with Spring Security so a contractor cannot bypass the UI and call manager-only APIs directly.

### GraphQL and Spring Data REST

We used explicit REST controllers because the assignment calls for a conventional REST API and explicit business logic. This keeps the API surface and authorization rules visible in the application code.

### H2 or an in-memory production database

We use PostgreSQL because persistence, relational constraints, migrations, rent-payment uniqueness, assignments, and historical maintenance data are important parts of the application.

### Redux

We did not introduce Redux because the current application state is small enough to manage with React state, context, and server requests without adding global state-management complexity.

### WebFlux

We use Spring MVC rather than reactive WebFlux because the application is a conventional CRUD/business application and does not have a requirement for reactive processing.

