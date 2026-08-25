ALTER TABLE users
    ADD COLUMN active boolean DEFAULT true,
    ADD COLUMN created_at timestamp DEFAULT now() NOT NULL;