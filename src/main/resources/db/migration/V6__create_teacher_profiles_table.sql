CREATE TABLE teacher_profiles (
    id                          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),

    teacher_id                  UUID            NOT NULL UNIQUE,

    employee_id                 VARCHAR(20)     UNIQUE,
    profile_picture_url         VARCHAR(500),
    phone_number                VARCHAR(20),
    alternative_phone_number    VARCHAR(20),
    personal_email              VARCHAR(100),
    street_address              VARCHAR(255),
    city                        VARCHAR(100),
    state_or_province           VARCHAR(100),
    postal_code                 VARCHAR(20),
    country                     VARCHAR(100),
    date_of_birth               DATE,
    gender                      VARCHAR(20),
    marital_status              VARCHAR(20),
    nationality                 VARCHAR(100),
    national_id                 VARCHAR(50)     UNIQUE,
    passport_number             VARCHAR(50),
    religion                    VARCHAR(50),
    ethnicity                   VARCHAR(50),
    native_language             VARCHAR(50),
    emergency_contact_name      VARCHAR(100),
    emergency_contact_phone     VARCHAR(20),
    emergency_contact_relationship VARCHAR(50),

    department                  VARCHAR(100)    NOT NULL,
    faculty                     VARCHAR(100),
    specialization              VARCHAR(255),
    highest_qualification       VARCHAR(100),
    qualification_details       TEXT,
    years_of_experience         INTEGER,
    previous_employer           VARCHAR(255),
    skills                      TEXT,
    certifications              TEXT,
    publications                TEXT,
    awards                      TEXT,
    bio                         TEXT,
    linked_in_profile           VARCHAR(500),
    research_interests          TEXT,
    teacher_type                VARCHAR(20)     DEFAULT 'FULL_TIME'
                                    CHECK (teacher_type IN ('FULL_TIME','PART_TIME','CONTRACT','VISITING','SUBSTITUTE')),

    subjects_taught             TEXT,
    classes_assigned            TEXT,
    academic_year               VARCHAR(20),
    current_schedule            VARCHAR(255),
    preferred_schedule_day      VARCHAR(20),
    max_weekly_hours            INTEGER,
    current_weekly_hours        INTEGER,
    office_room                 VARCHAR(20),
    office_hours                VARCHAR(100),
    classroom_building          VARCHAR(100),

    -- ─── Employment ───────────────────────────────────────────────────────
    join_date                   DATE,
    contract_start_date         DATE,
    contract_end_date           DATE,
    employment_type             VARCHAR(20)     DEFAULT 'FULL_TIME'
                                    CHECK (employment_type IN ('FULL_TIME','PART_TIME','CONTRACT','INTERN','VOLUNTEER')),
    employment_status           VARCHAR(20)     DEFAULT 'ACTIVE'
                                    CHECK (employment_status IN ('ACTIVE','ON_LEAVE','SUSPENDED','RESIGNED','TERMINATED','RETIRED')),
    salary                      DOUBLE PRECISION,
    salary_grade                VARCHAR(50),
    bank_name                   VARCHAR(100),
    bank_account_number         VARCHAR(50),
    tax_identification_number   VARCHAR(50),
    social_security_number      VARCHAR(50),
    annual_leave_days           INTEGER         DEFAULT 30,
    used_leave_days             INTEGER         DEFAULT 0,
    notes                       TEXT,

    is_active                   BOOLEAN         NOT NULL DEFAULT TRUE,
    is_deleted                  BOOLEAN         NOT NULL DEFAULT FALSE,
    is_verified                 BOOLEAN         NOT NULL DEFAULT FALSE,
    is_blocked                  BOOLEAN         NOT NULL DEFAULT FALSE,
    is_on_leave                 BOOLEAN         NOT NULL DEFAULT FALSE,

    created_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    deleted_at                  TIMESTAMPTZ,
    blocked_at                  TIMESTAMPTZ,

    CONSTRAINT fk_teacher_profiles_teacher_id
        FOREIGN KEY (teacher_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_teacher_profiles_teacher_id   ON teacher_profiles (teacher_id);
CREATE INDEX idx_teacher_profiles_department   ON teacher_profiles (department);
CREATE INDEX idx_teacher_profiles_is_active    ON teacher_profiles (is_active);
CREATE INDEX idx_teacher_profiles_is_deleted   ON teacher_profiles (is_deleted);
CREATE INDEX idx_teacher_profiles_employee_id  ON teacher_profiles (employee_id);