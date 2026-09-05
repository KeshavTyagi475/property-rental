# Submission

Links





GitHub repository: https://github.com/KeshavTyagi475/property-rental



Live application: To be added after deployment.

Notes for the reviewer

The application uses seeded demo accounts for the two required roles.

The backend uses PostgreSQL and Flyway migrations. The database must be available before starting the Spring Boot backend.

The frontend runs separately during local development and proxies /api requests to the Spring Boot backend.

Demo credentials

| Role | Username | Password |

|---|---|---|

| Property Manager | manager | manager123 |

| Maintenance Contractor | contractor | contractor123 |

These are development/demo credentials only.

Stack

| Layer | What you used | Why |

|---|---|---|

| Frontend | React, TypeScript, Vite, Tailwind CSS, React Router, Axios | Provides a simple typed browser UI with role-aware navigation and REST API integration. |

| Backend | Java 21, Spring Boot 3.x, Spring Security, Spring Data JPA, Jakarta Validation | Provides REST APIs, server-side authorization, business rules, validation, and persistence. |

| Database | PostgreSQL 17 + Flyway | Provides relational persistence, constraints, and versioned schema migrations. |

| Hosting | To be added after deployment | Deployment platform will be documented once the live application is available. |

Goal checklist

| # | Goal | Status | Notes |

|---|---|---|---|

| 1 | Accounts/roles | Done | Manager and contractor accounts are implemented. Manager-only operations are protected server-side with Spring Security. Contractors are restricted to assigned maintenance requests. |

| 2 | Units | Done | Units include number, address, monthly rent, tenant, editing, archiving, restoring, and preserved history. |

| 3 | Maintenance | Done | Maintenance requests belong to exactly one unit. Managers and contractors can create requests, while description/priority editing and assignment permissions follow the required rules. |

| 4 | Lifecycle | Done | Server validates REPORTED → TRIAGED → SCHEDULED → RESOLVED, requires a contractor before SCHEDULED, rejects invalid transitions, and supports reopening resolved requests to TRIAGED. |

| 5 | Assignment | Done | Requests can have multiple contractors and contractors can have multiple requests. Only managers can add or remove assignments. |

| 6 | Finding | Done | Maintenance search, unit/status/contractor/priority filters, sorting, pagination, and total count are implemented server-side. |

| 7 | Bulk rent | Done | Manager bulk rent recording supports unit identifiers and amounts with MATCHED, UNDERPAID, OVERPAID, and UNMATCHED results. CSV rent roll is implemented. |

| 8 | Dashboard | Done | Dashboard includes open maintenance requests, overdue rent units, requests resolved this week, monthly rent collected, maintenance breakdowns, and resolved counts for the last eight weeks. |

| 9 | Immutable request timeline | Done | Creation, status changes, assignments, unassignments, and notes are recorded in an append-only maintenance timeline. |

| 10 | Rent alerts | Done | Rent overdue alerts are generated after the grace period, shown to managers, can be dismissed, and active alerts are shown in the navigation badge. |

How much time did you actually spend?

I worked on the project incrementally across multiple development and testing sessions. The implementation took longer than the initial estimate because I spent additional time testing the backend security rules, maintenance lifecycle, contractor access, rent calculations, and frontend/backend integration.

I also spent time fixing issues that only became visible when I tested the APIs and the frontend together.

What would you do next, with another 12 hours?

With another 12 hours, I would focus mainly on deployment and polish.

I would:





Complete and verify the production deployment.



Add more automated tests around the maintenance lifecycle and authorization rules.



Improve error handling for duplicate rent payments.



Add more frontend validation and clearer error messages.



Improve the dashboard visualizations.



Do another pass over the UI for consistency and accessibility.



Add more realistic seeded demo data for the reviewer.



What are you least happy with in this codebase, and why?

The area I am least happy with is the amount of automated test coverage.

I manually tested a lot of the important workflows through the API and frontend, especially the role restrictions and maintenance lifecycle, but I would prefer to have more of those checks represented as automated tests.

I also think the frontend could be more polished visually. The main goal was to get the required workflows working correctly and securely first, so I prioritized functionality over extensive UI polish.