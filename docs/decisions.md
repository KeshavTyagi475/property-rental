# Decisions

These are the decisions that materially shaped the implementation. Each records the chosen approach, the alternative we rejected, and the reasoning behind it.

Table of contents
- [Decision 1 — Authentication model](#decision-1-—-authentication-model)
- [Decision 2 — Application architecture](#decision-2-—-application-architecture)
- [Decision 3 — Maintenance contractor assignments](#decision-3-—-maintenance-contractor-assignments)
- [Decision 4 — Database schema management](#decision-4-—-database-schema-management)
- [Decision 5 — Maintenance search and pagination](#decision-5-—-maintenance-search-and-pagination)
- [Decision 6 — Authorization boundary](#decision-6-—-authorization-boundary)
- [Decision 7 — Maintenance status transitions](#decision-7-—-maintenance-status-transitions)
- [Decision 8 — Timeline implementation](#decision-8-—-timeline-implementation)
- [Decision 9 — Frontend state management](#decision-9-—-frontend-state-management)
- [Decision 10 — Maintenance API response design](#decision-10-—-maintenance-api-response-design)
- [Decision 11 — Frontend request editing](#decision-11-—-frontend-request-editing)
- [Decision 12 — Rent payment uniqueness](#decision-12-—-rent-payment-uniqueness)
- [Decision 13 — Authentication UI and backend authorization](#decision-13-—-authentication-ui-and-backend-authorization)
- [Decision 14 — A decision we later reversed: maintenance request response model](#decision-14-—-a-decision-we-later-reversed-maintenance-request-response-model)

## Decision 1 — Authentication model

Chose: HTTP session/cookie authentication with Spring Security.

Rejected: JWT-based authentication.

Why: This is a browser-based application and the assignment does not require a stateless API. Server-side sessions keep authentication state on the server and make role-based authorization straightfor[...]

## Decision 2 — Application architecture

Chose: A lean modular monolith using Spring Boot with domain-oriented packages.

Rejected: Microservices.

Why: The application is small enough that splitting it into services would add deployment, networking, and operational complexity without providing a meaningful benefit. A modular monolith keeps trans[...]

## Decision 3 — Maintenance contractor assignments

Chose: An explicit maintenance_assignments entity for the many-to-many relationship between maintenance requests and contractors.

Rejected: A direct JPA @ManyToMany relationship.

Why: Assignments have their own lifecycle and metadata, including assigned_at. An explicit entity also makes assignment and unassignment timeline events easier to model and leaves room for future assi[...]

## Decision 4 — Database schema management

Chose: Flyway migrations with Hibernate configured to validate the schema using ddl-auto=validate.

Rejected: Letting Hibernate automatically create or update the database schema.

Why: The assignment requires a real PostgreSQL database and historical data. Versioned Flyway migrations make schema changes explicit, reproducible, and reviewable, while Hibernate validation catches [...]

## Decision 5 — Maintenance search and pagination

Chose: Server-side search, filtering, sorting, and pagination.

Rejected: Loading all maintenance requests and filtering them in React.

Why: The assignment explicitly requires server-side finding behavior. Keeping the query logic in the backend also means the API returns only the requested page and total count, which scales better tha[...]

## Decision 6 — Authorization boundary

Chose: Enforce authorization on the backend and mirror the restrictions in the frontend.

Rejected: Relying only on hidden frontend navigation and route restrictions.

Why: Frontend restrictions improve the user experience but are not a security boundary. Spring Security protects manager-only APIs and contractor maintenance access so a user cannot bypass the UI by c[...]

## Decision 7 — Maintenance status transitions

Chose: Explicit server-side lifecycle validation for REPORTED → TRIAGED → SCHEDULED → RESOLVED, with RESOLVED → TRIAGED for reopening.

Rejected: Allowing the frontend to control which status transitions are valid.

Why: Lifecycle rules are business rules and must be enforced consistently regardless of which client calls the API. The backend also enforces the rule that a request cannot enter SCHEDULED without an [...]

## Decision 8 — Timeline implementation

Chose: An append-only maintenance_timeline table recording creation, status changes, assignments, unassignments, and notes.

Rejected: Storing only the current maintenance request state and relying on application logs for history.

Why: The assignment requires an immutable request timeline that can be displayed to users. A dedicated table makes the history queryable and persistent and prevents historical events from being lost w[...]

## Decision 9 — Frontend state management

Chose: React component state and authentication context with API requests through Axios.

Rejected: Redux or another global state-management library.

Why: The application has a relatively small amount of shared client state. Adding Redux would introduce additional abstractions without solving a significant problem for the current scope.

## Decision 10 — Maintenance API response design

Chose: Dedicated response DTOs and mapper classes instead of returning JPA entities directly from maintenance endpoints.

Rejected: Exposing the persistence entities directly as the REST response model.

Why: The entity relationships include lazy-loaded associations and bidirectional relationships. DTOs prevent accidental recursive serialization, keep the API contract explicit, and avoid exposing pers[...]

## Decision 11 — Frontend request editing

Chose: Allow description and priority edits separately from contractor assignment.

Rejected: Allowing a general request update to modify assignments.

Why: The assignment specifies that managers control assignments while both managers and contractors can edit description and priority. Separating assignment endpoints from request updates makes that a[...]

## Decision 12 — Rent payment uniqueness

Chose: Enforce one rent payment per unit per payment month with a database unique constraint.

Rejected: Relying only on frontend validation or an application-level duplicate check.

Why: The uniqueness rule is a data-integrity invariant. A database constraint guarantees it even if multiple requests arrive concurrently or a client bypasses the frontend.

## Decision 13 — Authentication UI and backend authorization

Chose: Use the frontend to hide unauthorized navigation while independently enforcing the same restrictions through Spring Security.

Rejected: Treating frontend route protection as sufficient authorization.

Why: This provides a clearer user experience while maintaining server-side security. Contractors are redirected away from manager-only pages, but direct API access is still rejected by the backend.

## Decision 14 — A decision we later reversed: maintenance request response model

Chose initially: Return maintenance persistence entities directly from some REST endpoints.

Rejected initially: Dedicated response DTOs were considered unnecessary for the early CRUD implementation.

Why initially: The first implementation was focused on getting the maintenance workflow working quickly with the simplest response path.

Later reversed: We replaced the direct entity responses with dedicated maintenance response DTOs and mapper classes.

Why it changed: As assignments, timelines, contractor visibility, and frontend detail views were added, returning entities created unnecessary serialization and API-shape concerns. DTOs gave us a stab[...]
``` ````

Next steps: I can commit this formatted version back to the repository (create a branch and open a PR) if you'd like—tell me where to commit (branch name) and I'll apply it.
