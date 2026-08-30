CREATE TABLE notifications(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id   UUID NOT NULL,
    type        VARCHAR(50) NOT NULL,
    message     TEXT NOT NULL,
    created_at  TIMESTAMP NOT NULL
);