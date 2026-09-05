# Database Schema

The application uses PostgreSQL with Flyway migrations. Hibernate is configured with
`ddl-auto=validate`, so the application validates the schema rather than creating or
modifying it.

## Tables

### users

Stores application users and their roles.

Important fields:

- `id`
- `username`
- `password`
- `role`
- `created_at`

Roles are:

- `PROPERTY_MANAGER`
- `MAINTENANCE_CONTRACTOR`

### units

Stores rental units.

Important fields:

- `id`
- `unit_number`
- `address`
- `monthly_rent`
- `current_tenant`
- `archived`
- `created_at`
- `updated_at`

The unit number identifies the rental unit.

### rent_payments

Stores rent payments for units.

Important fields:

- `id`
- `unit_id`
- `payment_month`
- `amount`
- `created_at`

A database unique constraint prevents more than one payment for the same unit
and payment month.

```text
(unit_id, payment_month) UNIQUE