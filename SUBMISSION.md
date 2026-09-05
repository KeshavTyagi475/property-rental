# 🏠 Property Rental – Submission

## 🔗 Links
- **GitHub Repository**: [KeshavTyagi475/property-rental](https://github.com/KeshavTyagi475/property-rental)
- **Live Application**: [Live application:](https://property-rental-frontend.onrender.com)
- **Backend API**: [Backend](https://property-rental-ehdx.onrender.com)

---

## 📝 Reviewer Notes
- Seeded demo accounts are provided for both roles.
- **Backend**: PostgreSQL + Flyway migrations (database must be available before starting Spring Boot).
- **Frontend**: Runs separately during local development, proxies `/api` requests to backend.

---

## 🔑 Demo Credentials

| Role              | Username   | Password     |
|-------------------|------------|--------------|
| Property Manager  | manager    | manager123   |
| Contractor        | contractor | contractor123 |

⚠️ These are **development/demo credentials only**.

---

## 🛠️ Tech Stack

| Layer      | Tools Used                                                                 | Purpose                                                                 |
|------------|----------------------------------------------------------------------------|-------------------------------------------------------------------------|
| **Frontend** | React, TypeScript, Vite, Tailwind CSS, React Router, Axios                | Typed UI, role-aware navigation, REST API integration                   |
| **Backend**  | Java 21, Spring Boot 3.x, Spring Security, Spring Data JPA, Jakarta Validation | REST APIs, authorization, business rules, validation, persistence       |
| **Database** | PostgreSQL 17 + Flyway                                                   | Relational persistence, constraints, versioned schema migrations        |
| **Hosting**  | Render (Static Site + Web Service + PostgreSQL)                          | Deployment platform will be documented once live                        |

---
##Deployment note:
The application is deployed on Render using a React/Vite static site,
a Spring Boot web service, and PostgreSQL 17. The PostgreSQL free tier
is intended for the assignment/demo deployment and is subject to
Render's current free-tier limitations and expiration policy.
---

## 🎯 Goal Checklist

| #  | Goal                        | Status | Notes                                                                 |
|----|-----------------------------|--------|----------------------------------------------------------------------|
| 1  | Accounts/roles              | ✅ Done | Manager & contractor roles, server-side restrictions enforced        |
| 2  | Units                       | ✅ Done | Full CRUD, archiving/restoring, preserved history                     |
| 3  | Maintenance                 | ✅ Done | Requests tied to units, permissions enforced                          |
| 4  | Lifecycle                   | ✅ Done | Valid transitions, reopening supported                                |
| 5  | Assignment                  | ✅ Done | Multi-contractor support, manager-only assignment control             |
| 6  | Finding                     | ✅ Done | Search, filters, sorting, pagination, total count                     |
| 7  | Bulk rent                   | ✅ Done | CSV rent roll, MATCHED/UNDERPAID/OVERPAID/UNMATCHED results           |
| 8  | Dashboard                   | ✅ Done | Metrics: open requests, overdue rent, weekly/monthly breakdowns       |
| 9  | Immutable request timeline  | ✅ Done | Append-only timeline for all request changes                          |
| 10 | Rent alerts                 | ✅ Done | Overdue alerts, dismissible, badge notifications                      |

---

## ⏱️ Development Effort
- Built incrementally across multiple sessions.
- Extra time spent testing backend security, lifecycle rules, rent calculations, and integration.
- Debugging required after combined API + frontend testing.

---

## 🚀 Next Steps (12 Hours Plan)
With another 12 hours, I would:
- Complete production deployment.
- Add automated tests for lifecycle & authorization.
- Improve duplicate rent payment error handling.
- Strengthen frontend validation & error messages.
- Enhance dashboard visualizations.
- Improve UI consistency & accessibility.
- Seed more realistic demo data.

---

## 😕 Least Satisfied Area
- **Automated test coverage**: Too much reliance on manual testing for workflows.
- **Frontend polish**: Functionality prioritized over visual/UI refinement.
