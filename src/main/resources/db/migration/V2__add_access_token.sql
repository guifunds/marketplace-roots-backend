ALTER TABLE signups ADD COLUMN access_token UUID;
UPDATE signups SET access_token = gen_random_uuid() WHERE access_token IS NULL;
ALTER TABLE signups ALTER COLUMN access_token SET NOT NULL;
CREATE UNIQUE INDEX idx_signups_access_token ON signups (access_token);
