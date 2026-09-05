# Plan

How did you break the work into sessions?

I broke the work into smaller sessions instead of trying to build the whole application at once.

I started with the project setup and database connection, then moved into the backend features one by one. After the main backend functionality was working, I added the frontend and connected it to th[...]

The rough order was:

- Project and Spring Boot setup
- PostgreSQL and Flyway database setup
- Authentication and user roles
- Units
- Rent payments
- Maintenance requests
- Contractor assignments
- Maintenance lifecycle and timeline
- Search, filtering, sorting and pagination
- Bulk rent and rent roll
- Dashboard
- Rent alerts
- Frontend pages and role-based navigation
- Security and API testing
- Documentation and GitHub setup

I also made small commits as I finished each meaningful part so I could keep track of what changed.

What order did you build in, and why that order?

I started with the backend and database because most of the important rules for this assignment need to be enforced on the server.

After setting up PostgreSQL and Flyway, I added authentication and roles first. This gave me the security foundation for the rest of the APIs.

I built units and rent next because they are relatively independent and also provide the data needed by the maintenance and dashboard features.

Maintenance was the biggest part, so I built it in stages. First I created requests, then editing, contractor assignments, status transitions, the timeline, and finally search and filtering.

Once the backend functionality was mostly complete, I built the React frontend around the APIs. This made it easier to test the actual backend rules independently before depending on the UI.

I finished with dashboard data, alerts, frontend access restrictions, and the final security testing.

What did you estimate versus what it actually took?

My initial estimate was that the basic backend and database setup would take a smaller portion of the work and that the frontend would be fairly straightforward once the APIs were ready.

In practice, the implementation and testing took longer than expected, especially around maintenance authorization, lifecycle rules, timeline history, and making sure contractors could only access req[...]

The frontend also took more time than I originally expected because I needed separate manager and contractor experiences and had to make sure the UI restrictions matched the backend security rules.

I also spent more time testing API responses and fixing small issues between the frontend and backend than I had planned.

What did you cut when you ran short?

I focused on the mandatory requirements first and avoided adding unnecessary features.

I did not spend time building the optional stretch features because the core assignment already had a fairly large scope. I also kept the architecture as a modular monolith instead of introducing micr[...]

I avoided adding Redux and other libraries that were not necessary for the current frontend.

The main goal was to make the required workflows work correctly, especially authentication, RBAC, maintenance lifecycle, contractor assignments, rent recording, alerts, dashboard data, and the immutab[...]
