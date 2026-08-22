CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50)

    CONSTRAINT chk_user_role CHECK (role IN ('ADMIN', 'CLIENT', 'TECHNICIAN'))
);