CREATE TABLE tickets (
    id UUID PRIMARY KEY,

    title VARCHAR(255) NOT NULL,

    description TEXT,

    status VARCHAR(50) NOT NULL CHECK (status IN (
    'OPEN', 'IN_PROGRESS', 'WAITING', 'RESOLVED', 'CLOSED'
    )),

    priority VARCHAR(50) NOT NULL CHECK (priority IN (
    'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'
    )),

    category VARCHAR(50) NOT NULL CHECK (category IN (
    'HARDWARE', 'SOFTWARE', 'NETWORK'
    )),

    customer_id uuid NOT NULL,
    technician_id uuid,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);