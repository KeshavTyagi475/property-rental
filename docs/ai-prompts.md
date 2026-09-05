# 🤖 AI Prompts

I used AI as a development assistant throughout the project. I used it mainly to break the assignment into smaller implementation steps, review code, troubleshoot errors, and check that the implementation matched the requirements.

I did not blindly accept the generated code. I tested the changes locally, checked the API responses, fixed issues when they appeared, and made the final decisions about what was included.

---

## 1. Understanding the Assignment and Planning the Backend

### Prompt

I asked AI to break the Property Rental & Maintenance assignment into the main backend and frontend goals, identify the required entities and relationships, and suggest an implementation order using Spring Boot, PostgreSQL, Flyway, React and TypeScript.

### What You Got

It broke the assignment into areas such as authentication/RBAC, units, rent, maintenance requests, contractor assignments, lifecycle transitions, search/filtering, dashboard, alerts, and the maintenance timeline.

It also suggested starting with the database and backend business rules before building the frontend.

### What You Corrected

I used the suggested order as a starting point but adjusted it as the implementation progressed. I kept the application as a modular monolith and prioritized the mandatory requirements instead of spending time on stretch features.

---

## 2. Database and Project Structure

### Prompt

I asked AI how to structure the Spring Boot project around the assignment requirements and how to use PostgreSQL with Flyway while keeping Hibernate in validation mode.

### What You Got

The suggested structure separated the application into domain packages such as domain packages such as auth, security, user, unit, rent, maintenance, dashboard, and alert.

The database approach used Flyway for migrations and ddl-auto=validate for Hibernate.

### What You Corrected

I kept the structure fairly simple instead of introducing extra layers or services that were not needed for the assignment.

---

## 3. Authentication and Role-Based Access

### Prompt

I asked AI to implement manager and contractor authentication with Spring Security and make sure authorization was enforced on the server rather than only hiding frontend pages.

### What You Got

The implementation used Spring Security with HTTP sessions and role-based authorization. Manager-only endpoints were protected using Spring Security authorization rules.

### What You Corrected

I tested the endpoints directly as a contractor instead of assuming the frontend restrictions were enough. This caught areas where API access needed to be protected separately from the frontend navigation.

---

## 4. Maintenance Request Lifecycle

### Prompt

I asked AI to implement the maintenance request lifecycle with the required transitions and server-side validation, including the rule that a request cannot become SCHEDULED without a contractor.

### What You Got

The backend implemented:

REPORTED → TRIAGED → SCHEDULED → RESOLVED

and allowed:

RESOLVED → TRIAGED

for reopening.

Invalid transitions were rejected by the backend with an explanatory error.

### What You Corrected

I tested invalid transitions manually through the API. In particular, I verified that TRIAGED → SCHEDULED failed when there was no contractor assigned and that invalid transitions such as SCHEDULED → REPORTED were rejected.

---

## 5. Maintenance Timeline and Assignments

### Prompt

I asked AI how to model contractor assignments and the immutable maintenance timeline while supporting many contractors per request and many requests per contractor.

### What You Got

It suggested using an explicit maintenance_assignments entity instead of relying on a simple JPA many-to-many mapping. The timeline was stored separately so status changes, assignments, unassignments, notes, and creation events could be retained.

### What You Corrected

I tested the assignment rules with both manager and contractor accounts. Contractors could only see requests assigned to them, while managers controlled assignment and unassignment.

I also checked that the timeline was append-only and that there were no edit or delete operations for timeline events.

---

## 6. Maintenance API Response Problem

### Prompt

I asked AI to help clean up the maintenance REST API responses after the maintenance request, assignment, and timeline relationships had become more complex.

### What You Got

The initial implementation exposed some JPA entities directly from REST endpoints.

### What You Corrected

This caused concerns around persistence relationships and API response shape as the feature grew. I changed the implementation to use dedicated response DTOs and mapper classes.

This was one of the decisions I later reversed: the early implementation was simpler, but I changed it once the maintenance API became more complex.

---

## 7. Search, Filtering and Pagination

### Prompt

I asked AI to implement server-side maintenance search with description text search, unit/status/contractor/priority filters, sorting and pagination with a total count.

### What You Got

The implementation used Spring Data specifications to build the database query dynamically and returned a paginated response.

### What You Corrected

I tested the different combinations through the API rather than relying only on the frontend. This confirmed that the filtering and pagination were happening on the server.

---

## 8. Bulk Rent Recording

### Prompt

I asked AI to implement bulk rent recording for a month and return MATCHED, UNDERPAID, OVERPAID, and UNMATCHED results.

### What You Got

The backend accepted multiple unit identifiers and payment amounts and calculated the payment status against each unit's monthly rent.

### What You Corrected

I initially encountered a request-shape mismatch between the frontend and backend. The frontend was sending a unit ID while the backend expected unitNumber.

I changed the frontend bulk-rent request to send the unit number expected by the API.

I also encountered a duplicate-payment database constraint when testing a month that already had a payment. Instead of treating that as a new successful payment, I used a fresh month for the acceptance test and verified the unique unit/month constraint.

---

## 9. Frontend Role Restrictions

### Prompt

I asked AI to build the React frontend with separate manager and contractor navigation and prevent contractors from reaching manager-only pages.

### What You Got

The frontend used React Router route protection and role-aware navigation. Managers could access Dashboard, Units, Maintenance, Rent, and Alerts. Contractors only received the Maintenance area.

### What You Corrected

I did not treat frontend route protection as the actual security boundary. I also tested the backend directly as a contractor and confirmed that manager-only APIs returned 403 Forbidden.

---

## 10. Frontend TypeScript Issue

### Prompt

I asked AI to troubleshoot a TypeScript error in the maintenance request detail page related to the status update handler.

### What You Got

The suggested fix identified an incorrect state update in the error handling path.

### What You Corrected

I removed the incorrect setStatus(request.status) call from the catch block and rebuilt the frontend. The production TypeScript/Vite build then passed.

---

## 11. Rent Alerts and Navigation Badge

### Prompt

I asked AI to implement rent overdue alerts after the grace period and expose them to managers with a dismiss action.

### What You Got

The backend creates rent overdue alerts and the frontend provides an Alerts page for managers.

### What You Corrected

During the final requirements review, I noticed that the assignment also specifically required an alert navigation badge. The Alerts page existed, but the navigation did not yet show the number of active alerts.

I added an active-alert count to the manager navigation and verified that dismissed alerts are not counted.

---

## 12. Final Testing and Verification

### Prompt

I asked AI to help create a final checklist covering the mandatory assignment requirements, especially RBAC, maintenance lifecycle rules, rent calculations, alerts, dashboard data, timeline history, frontend access, and production builds.

### What You Got

The checklist was used to go back through the implemented APIs and frontend flows.

### What You Corrected

I tested the important security boundaries directly with manager and contractor accounts instead of relying only on successful manager tests. I also rebuilt both the backend and frontend before moving toward deployment.

The final frontend production build passed, and the Spring Boot Maven package build passed.

---

## Summary

AI was useful for breaking the project into manageable pieces, generating initial implementations, and troubleshooting issues. I treated the generated code as a starting point rather than assuming it was correct.

The main corrections came from actually running the application and testing the API: authorization gaps, API response design, frontend/backend request mismatches, lifecycle validation, and the missing alert navigation badge were all things that required testing and iteration.
