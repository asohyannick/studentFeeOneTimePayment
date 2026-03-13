CREATE TYPE user_role AS ENUM (
    -- ─── Management ──────────────────────────────────────────────────
    'ADMIN',
    'SUPER_ADMIN',
    'PRINCIPAL',
    'VICE_PRINCIPAL',
    'SCHOOL_DIRECTOR',
    'SECRETARY',
    'RECEPTIONIST',

    -- ─── Teaching Staff ──────────────────────────────────────────────
    'TEACHER',
    'PROFESSOR',
    'HEAD_OF_DEPARTMENT',
    'CLASS_TEACHER',
    'SUBSTITUTE_TEACHER',
    'TEACHING_ASSISTANT',
    'TUTOR',
    'COUNSELOR',
    'LIBRARIAN',

    -- ─── Support Staff ───────────────────────────────────────────────
    'ACCOUNTANT',
    'CASHIER',
    'IT_STAFF',
    'SECURITY',
    'NURSE',
    'JANITOR',
    'DRIVER',
    'CANTEEN_STAFF',

    -- ─── Students & Parents ──────────────────────────────────────────
    'STUDENT',
    'PARENT',
    'GUARDIAN',

    -- ─── External ────────────────────────────────────────────────────
    'AUDITOR',
    'EXAMINER',
    'GUEST'
);

-- ─── Create Users Table ──────────────────────────────────────────────
CREATE TABLE users (
    id                         UUID         NOT NULL DEFAULT gen_random_uuid(),
    first_name                 VARCHAR(50)  NOT NULL,
    last_name                  VARCHAR(50)  NOT NULL,
    email                      VARCHAR(255) NOT NULL,
    password                   VARCHAR(255) NOT NULL,
    role                       user_role    NOT NULL DEFAULT 'STUDENT',
    account_verified           BOOLEAN      NOT NULL DEFAULT FALSE,
    account_active             BOOLEAN      NOT NULL DEFAULT FALSE,
    account_blocked            BOOLEAN      NOT NULL DEFAULT FALSE,
    magic_link_token           VARCHAR(255),
    magic_link_expiration_date TIMESTAMPTZ,
    failed_login_attempts      INTEGER      NOT NULL DEFAULT 0,
    last_login_at              TIMESTAMPTZ,
    otp_code                   VARCHAR(255),
    otp_expiration_date        TIMESTAMPTZ,
    locked_until               TIMESTAMPTZ,
    created_at                 TIMESTAMPTZ  NOT NULL,
    updated_at                 TIMESTAMPTZ  NOT NULL,

    -- ─── Constraints ─────────────────────────────────────────────────
    CONSTRAINT pk_users          PRIMARY KEY (id),
    CONSTRAINT uk_users_email    UNIQUE (email)
);

-- ─── Indexes ─────────────────────────────────────────────────────────
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_role  ON users (role);

-- ─── Comments ────────────────────────────────────────────────────────
COMMENT ON TABLE  users                            IS 'Stores all system users across all roles';
COMMENT ON COLUMN users.id                         IS 'Unique identifier for the user';
COMMENT ON COLUMN users.email                      IS 'Unique email address used for authentication';
COMMENT ON COLUMN users.role                       IS 'Role assigned to the user in the school system';
COMMENT ON COLUMN users.account_verified           IS 'Whether the user verified their email via OTP';
COMMENT ON COLUMN users.account_active             IS 'Whether the account is currently active';
COMMENT ON COLUMN users.account_blocked            IS 'Whether the account has been blocked by an admin';
COMMENT ON COLUMN users.otp_code                   IS 'Hashed OTP code for email verification';
COMMENT ON COLUMN users.otp_expiration_date        IS 'Expiry timestamp of the OTP code';
COMMENT ON COLUMN users.magic_link_token           IS 'Token used for passwordless magic link login';
COMMENT ON COLUMN users.magic_link_expiration_date IS 'Expiry timestamp of the magic link token';
COMMENT ON COLUMN users.failed_login_attempts      IS 'Number of consecutive failed login attempts';
COMMENT ON COLUMN users.locked_until               IS 'Timestamp until which the account is locked';
COMMENT ON COLUMN users.last_login_at              IS 'Timestamp of the last successful login';
COMMENT ON COLUMN users.created_at                 IS 'Timestamp when the record was created';
COMMENT ON COLUMN users.updated_at                 IS 'Timestamp when the record was last updated';