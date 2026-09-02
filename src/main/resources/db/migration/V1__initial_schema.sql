CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_users_role
        CHECK (role IN ('PROPERTY_MANAGER', 'MAINTENANCE_CONTRACTOR'))
);

CREATE TABLE units (
    id BIGSERIAL PRIMARY KEY,
    unit_number VARCHAR(50) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    monthly_rent NUMERIC(12, 2) NOT NULL,
    current_tenant VARCHAR(150),
    is_archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_units_monthly_rent
        CHECK (monthly_rent >= 0)
);

CREATE TABLE rent_payments (
    id BIGSERIAL PRIMARY KEY,
    unit_id BIGINT NOT NULL,
    payment_month DATE NOT NULL,
    amount NUMERIC(12, 2) NOT NULL,
    recorded_by BIGINT NOT NULL,
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_rent_payments_unit
        FOREIGN KEY (unit_id)
        REFERENCES units(id),

    CONSTRAINT fk_rent_payments_recorded_by
        FOREIGN KEY (recorded_by)
        REFERENCES users(id),

    CONSTRAINT chk_rent_payments_amount
        CHECK (amount > 0),

    CONSTRAINT uq_rent_payments_unit_month
        UNIQUE (unit_id, payment_month)
);

CREATE TABLE maintenance_requests (
    id BIGSERIAL PRIMARY KEY,
    unit_id BIGINT NOT NULL,
    description TEXT NOT NULL,
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'REPORTED',
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_maintenance_requests_unit
        FOREIGN KEY (unit_id)
        REFERENCES units(id),

    CONSTRAINT fk_maintenance_requests_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id),

    CONSTRAINT chk_maintenance_requests_priority
        CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),

    CONSTRAINT chk_maintenance_requests_status
        CHECK (status IN ('REPORTED', 'TRIAGED', 'SCHEDULED', 'RESOLVED'))
);

CREATE TABLE maintenance_assignments (
    id BIGSERIAL PRIMARY KEY,
    maintenance_request_id BIGINT NOT NULL,
    contractor_id BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_assignments_request
        FOREIGN KEY (maintenance_request_id)
        REFERENCES maintenance_requests(id),

    CONSTRAINT fk_assignments_contractor
        FOREIGN KEY (contractor_id)
        REFERENCES users(id),

    CONSTRAINT uq_assignments_request_contractor
        UNIQUE (maintenance_request_id, contractor_id)
);

CREATE TABLE maintenance_timeline (
    id BIGSERIAL PRIMARY KEY,
    maintenance_request_id BIGINT NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    old_status VARCHAR(20),
    new_status VARCHAR(20),
    note TEXT,
    contractor_id BIGINT,
    performed_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_timeline_request
        FOREIGN KEY (maintenance_request_id)
        REFERENCES maintenance_requests(id),

    CONSTRAINT fk_timeline_contractor
        FOREIGN KEY (contractor_id)
        REFERENCES users(id),

    CONSTRAINT fk_timeline_performed_by
        FOREIGN KEY (performed_by)
        REFERENCES users(id),

    CONSTRAINT chk_timeline_event_type
        CHECK (
            event_type IN (
                'CREATED',
                'STATUS_CHANGED',
                'ASSIGNED',
                'UNASSIGNED',
                'NOTE_ADDED'
            )
        ),

    CONSTRAINT chk_timeline_status_change
        CHECK (
            event_type <> 'STATUS_CHANGED'
            OR (old_status IS NOT NULL AND new_status IS NOT NULL)
        )
);

CREATE TABLE alerts (
    id BIGSERIAL PRIMARY KEY,
    unit_id BIGINT NOT NULL,
    alert_type VARCHAR(30) NOT NULL,
    alert_month DATE NOT NULL,
    message TEXT NOT NULL,
    dismissed_at TIMESTAMP,
    dismissed_by BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_alerts_unit
        FOREIGN KEY (unit_id)
        REFERENCES units(id),

    CONSTRAINT fk_alerts_dismissed_by
        FOREIGN KEY (dismissed_by)
        REFERENCES users(id),

    CONSTRAINT chk_alerts_type
        CHECK (alert_type IN ('RENT_OVERDUE')),

    CONSTRAINT uq_alerts_unit_type_month
        UNIQUE (unit_id, alert_type, alert_month)
);