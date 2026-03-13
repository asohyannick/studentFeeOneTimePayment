CREATE TABLE student_accounts (
    id                  UUID            NOT NULL DEFAULT gen_random_uuid(),
    student_profile_id  UUID            NOT NULL,
    balance             NUMERIC(19, 2)  NOT NULL DEFAULT 0.00,
    next_due_date       DATE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT pk_student_accounts         PRIMARY KEY (id),
    CONSTRAINT uq_student_accounts_profile UNIQUE (student_profile_id),
    CONSTRAINT fk_student_accounts_profile
        FOREIGN KEY (student_profile_id)
        REFERENCES student_profiles (id)
        ON DELETE CASCADE,
    CONSTRAINT chk_student_accounts_balance
        CHECK (balance >= 0)
);

CREATE TABLE fee_payments (
    id                       UUID            NOT NULL DEFAULT gen_random_uuid(),
    student_account_id       UUID            NOT NULL,
    student_number           VARCHAR(20)     NOT NULL,
    payment_amount           NUMERIC(19, 2)  NOT NULL,
    previous_balance         NUMERIC(19, 2)  NOT NULL,
    incentive_rate           NUMERIC(5, 4)   NOT NULL,
    incentive_amount         NUMERIC(19, 2)  NOT NULL,
    new_balance              NUMERIC(19, 2)  NOT NULL,
    payment_date             DATE            NOT NULL,
    next_due_date            DATE            NOT NULL,
    stripe_payment_intent_id VARCHAR(255),
    stripe_payment_status    VARCHAR(50),
    created_at               TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT pk_fee_payments PRIMARY KEY (id),
    CONSTRAINT fk_fee_payments_account
        FOREIGN KEY (student_account_id)
        REFERENCES student_accounts (id)
        ON DELETE CASCADE,
    CONSTRAINT chk_fee_payments_amount
        CHECK (payment_amount > 0)
);

CREATE INDEX idx_fee_payments_student_number   ON fee_payments (student_number);
CREATE INDEX idx_fee_payments_payment_date     ON fee_payments (payment_date);
CREATE INDEX idx_fee_payments_account_id       ON fee_payments (student_account_id);
CREATE INDEX idx_student_accounts_profile_id   ON student_accounts (student_profile_id);