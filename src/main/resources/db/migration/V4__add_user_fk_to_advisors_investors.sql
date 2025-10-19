CREATE EXTENSION IF NOT EXISTS "pgcrypto";

ALTER TABLE advisors
  ADD COLUMN user_id UUID NOT NULL,
  ADD CONSTRAINT fk_advisors_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  ADD CONSTRAINT uq_advisors_user UNIQUE (user_id);

ALTER TABLE investors
  ADD COLUMN user_id UUID NOT NULL,
  ADD CONSTRAINT fk_investors_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  ADD CONSTRAINT uq_investors_user UNIQUE (user_id);

INSERT INTO roles (id, name) VALUES (gen_random_uuid(), 'ROLE_INVESTOR')
    ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (id, name) VALUES (gen_random_uuid(), 'ROLE_ADVISOR')
    ON CONFLICT (name) DO NOTHING;
