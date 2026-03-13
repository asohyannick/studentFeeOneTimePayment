CREATE TYPE gender_type AS ENUM (
    'MALE',
    'FEMALE',
    'OTHER',
    'PREFER_NOT_TO_SAY'
);

CREATE TYPE marital_status_type AS ENUM (
    'SINGLE',
    'MARRIED',
    'DIVORCED',
    'WIDOWED',
    'SEPARATED'
);

CREATE TYPE blood_group_type AS ENUM (
    'A_POSITIVE',
    'A_NEGATIVE',
    'B_POSITIVE',
    'B_NEGATIVE',
    'AB_POSITIVE',
    'AB_NEGATIVE',
    'O_POSITIVE',
    'O_NEGATIVE'
);

CREATE TYPE nationality_status_type AS ENUM (
    'CITIZEN',
    'PERMANENT_RESIDENT',
    'TEMPORARY_RESIDENT',
    'REFUGEE',
    'STATELESS',
    'FOREIGN_NATIONAL'
);

CREATE TYPE enrollment_status_type AS ENUM (
    'ACTIVE',
    'INACTIVE',
    'SUSPENDED',
    'EXPELLED',
    'GRADUATED',
    'TRANSFERRED',
    'DEFERRED',
    'WITHDRAWN'
);

-- ─── Table ────────────────────────────────────────────────────────────────────

CREATE TABLE student_profiles (

    -- Identity
    id                              UUID            NOT NULL DEFAULT gen_random_uuid(),
    student_id                      UUID            NOT NULL,
    student_number                  VARCHAR(20)     NOT NULL,
    profile_picture_url             VARCHAR(500),

    -- Identification Documents
    national_id                     VARCHAR(50)     UNIQUE,
    passport_number                 VARCHAR(50)     UNIQUE,
    birth_certificate_number        VARCHAR(50)     UNIQUE,

    -- Personal Information
    date_of_birth                   DATE            NOT NULL,
    gender                          gender_type     NOT NULL,
    marital_status                  marital_status_type,
    blood_group                     blood_group_type,
    nationality                     VARCHAR(100),
    nationality_status              nationality_status_type,
    religion                        VARCHAR(50),
    ethnicity                       VARCHAR(50),
    native_language                 VARCHAR(50),

    -- Contact Information
    phone_number                    VARCHAR(20),
    alternative_phone_number        VARCHAR(20),
    personal_email                  VARCHAR(255)    UNIQUE,

    -- Address Information
    street_address                  VARCHAR(255),
    city                            VARCHAR(100),
    state_or_province               VARCHAR(100),
    postal_code                     VARCHAR(20),
    country                         VARCHAR(100),
    permanent_address               VARCHAR(500),

    -- Academic Information
    current_class                   VARCHAR(50),
    current_section                 VARCHAR(10),
    academic_year                   VARCHAR(20),
    admission_date                  DATE,
    graduation_date                 DATE,
    previous_school                 VARCHAR(255),
    previous_school_address         VARCHAR(500),
    gpa                             DOUBLE PRECISION,
    enrollment_status               enrollment_status_type DEFAULT 'ACTIVE',

    -- Parent Information
    father_name                     VARCHAR(100),
    father_phone                    VARCHAR(20),
    father_email                    VARCHAR(255),
    father_occupation               VARCHAR(100),

    mother_name                     VARCHAR(100),
    mother_phone                    VARCHAR(20),
    mother_email                    VARCHAR(255),
    mother_occupation               VARCHAR(100),

    -- Guardian Information
    guardian_name                   VARCHAR(100),
    guardian_phone                  VARCHAR(20),
    guardian_email                  VARCHAR(255),
    guardian_relationship           VARCHAR(50),
    guardian_address                VARCHAR(500),

    -- Emergency Contact
    emergency_contact_name          VARCHAR(100),
    emergency_contact_phone         VARCHAR(20),
    emergency_contact_email         VARCHAR(255),
    emergency_contact_relationship  VARCHAR(50),

    -- Medical Information
    medical_conditions              VARCHAR(1000),
    allergies                       VARCHAR(500),
    medications                     VARCHAR(500),
    disability                      VARCHAR(500),
    special_needs                   VARCHAR(500),

    -- Financial Information
    scholarship_name                VARCHAR(255),
    scholarship_percentage          DOUBLE PRECISION,
    is_fee_defaulter                BOOLEAN         NOT NULL DEFAULT FALSE,
    discount_percentage             DOUBLE PRECISION,

    -- Transport Information
    uses_school_transport           BOOLEAN         NOT NULL DEFAULT FALSE,
    transport_route                 VARCHAR(255),
    bus_stop                        VARCHAR(255),

    -- Hostel Information
    is_boarder                      BOOLEAN         NOT NULL DEFAULT FALSE,
    hostel_name                     VARCHAR(100),
    room_number                     VARCHAR(20),

    -- Additional Information
    hobbies                         VARCHAR(500),
    extracurricular_activities      VARCHAR(500),
    notes                           VARCHAR(1000),

    -- Soft Delete
    is_deleted                      BOOLEAN         NOT NULL DEFAULT FALSE,
    deleted_at                      TIMESTAMPTZ,

    -- Audit
    created_at                      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    updated_at                      TIMESTAMPTZ     NOT NULL DEFAULT now(),

    -- ─── Constraints ─────────────────────────────────────────────────────────
    CONSTRAINT pk_student_profiles
        PRIMARY KEY (id),

    CONSTRAINT uq_student_profiles_student_id
        UNIQUE (student_id),

    CONSTRAINT uq_student_profiles_student_number
        UNIQUE (student_number),

    CONSTRAINT fk_student_profiles_user
        FOREIGN KEY (student_id)
        REFERENCES users (id)
        ON DELETE CASCADE,

    CONSTRAINT chk_student_profiles_gpa
        CHECK (gpa IS NULL OR (gpa >= 0.0 AND gpa <= 4.0)),

    CONSTRAINT chk_student_profiles_scholarship_percentage
        CHECK (scholarship_percentage IS NULL OR (scholarship_percentage >= 0.0 AND scholarship_percentage <= 100.0)),

    CONSTRAINT chk_student_profiles_discount_percentage
        CHECK (discount_percentage IS NULL OR (discount_percentage >= 0.0 AND discount_percentage <= 100.0)),

    CONSTRAINT chk_student_profiles_graduation_after_admission
        CHECK (graduation_date IS NULL OR admission_date IS NULL OR graduation_date >= admission_date)
);

-- ─── Indexes ──────────────────────────────────────────────────────────────────

CREATE INDEX idx_student_profiles_student_id
    ON student_profiles (student_id);

CREATE INDEX idx_student_profiles_student_number
    ON student_profiles (student_number);

CREATE INDEX idx_student_profiles_national_id
    ON student_profiles (national_id);

CREATE INDEX idx_student_profiles_current_class
    ON student_profiles (current_class);

CREATE INDEX idx_student_profiles_academic_year
    ON student_profiles (academic_year);

CREATE INDEX idx_student_profiles_enrollment_status
    ON student_profiles (enrollment_status);

CREATE INDEX idx_student_profiles_is_deleted
    ON student_profiles (is_deleted);

CREATE INDEX idx_student_profiles_is_fee_defaulter
    ON student_profiles (is_fee_defaulter);

CREATE INDEX idx_student_profiles_is_boarder
    ON student_profiles (is_boarder);

CREATE INDEX idx_student_profiles_uses_school_transport
    ON student_profiles (uses_school_transport);

CREATE INDEX idx_student_profiles_class_year
    ON student_profiles (current_class, academic_year);

CREATE INDEX idx_student_profiles_city_country
    ON student_profiles (city, country);