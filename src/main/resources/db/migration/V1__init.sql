CREATE TABLE signups (
    id UUID PRIMARY KEY,
    profile_type VARCHAR(20) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    document VARCHAR(50),
    country VARCHAR(100),
    lang VARCHAR(2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    stripe_payment_intent_id VARCHAR(255),
    founding_fee_amount_cents BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    paid_at TIMESTAMP
);

CREATE INDEX idx_signups_email ON signups (email);
CREATE UNIQUE INDEX idx_signups_stripe_payment_intent_id ON signups (stripe_payment_intent_id) WHERE stripe_payment_intent_id IS NOT NULL;
