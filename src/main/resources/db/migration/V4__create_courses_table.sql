CREATE TABLE courses (
    id                          UUID             PRIMARY KEY DEFAULT gen_random_uuid(),

    teacher_id                  UUID             NOT NULL,

    course_name                 VARCHAR(255)     NOT NULL,
    course_code                 VARCHAR(20)      NOT NULL UNIQUE,
    course_url                  VARCHAR(500),
    description                 TEXT,
    short_description           VARCHAR(500),
    credit_hours                INTEGER          NOT NULL,
    course_objectives           TEXT,
    prerequisites               VARCHAR(500),
    textbook                    VARCHAR(255),
    language                    VARCHAR(50),

    department                  VARCHAR(100)     NOT NULL,
    faculty                     VARCHAR(100),
    course_level                VARCHAR(100),
    semester                    VARCHAR(50),
    academic_year               VARCHAR(20)      NOT NULL,
    start_date                  DATE,
    end_date                    DATE,
    duration_weeks              INTEGER,
    grade_level                 VARCHAR(50)      NOT NULL DEFAULT 'INTERMEDIATE'
                                    CHECK (grade_level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),
    exam_type                   VARCHAR(50)      NOT NULL DEFAULT 'WRITTEN'
                                    CHECK (exam_type IN ('WRITTEN', 'PRACTICAL', 'BOTH')),

    instructor_name             VARCHAR(100),
    instructor_email            VARCHAR(100),
    instructor_phone            VARCHAR(20),
    co_instructor_name          VARCHAR(100),
    room_number                 VARCHAR(20),
    building                    VARCHAR(100),
    schedule_day                VARCHAR(20)      NOT NULL DEFAULT 'TUESDAY'
                                    CHECK (schedule_day IN ('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY')),
    schedule_start_time         TIME,
    schedule_end_time           TIME,
    schedule_type               VARCHAR(20)      NOT NULL DEFAULT 'ONLINE'
                                    CHECK (schedule_type IN ('ONLINE', 'OFFLINE', 'IN_PERSON', 'HYBRID')),
    meeting_link                VARCHAR(500),

    is_active                   BOOLEAN          NOT NULL DEFAULT TRUE,
    is_deleted                  BOOLEAN          NOT NULL DEFAULT FALSE,
    is_mandatory                BOOLEAN          NOT NULL DEFAULT FALSE,
    is_elective                 BOOLEAN          NOT NULL DEFAULT FALSE,
    allow_late_enrollment       BOOLEAN          NOT NULL DEFAULT FALSE,
    max_capacity                INTEGER          NOT NULL,
    current_enrollment_count    INTEGER          NOT NULL DEFAULT 0,
    min_enrollment_count        INTEGER,
    pass_mark                   DOUBLE PRECISION NOT NULL,
    max_score                   DOUBLE PRECISION NOT NULL DEFAULT 100.0,
    course_status               VARCHAR(20)      NOT NULL DEFAULT 'UPCOMING'
                                    CHECK (course_status IN ('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED')),
    notes                       TEXT,

    created_at                  TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    updated_at                  TIMESTAMPTZ      NOT NULL DEFAULT NOW(),
    deleted_at                  TIMESTAMPTZ,

    CONSTRAINT fk_courses_teacher_id
        FOREIGN KEY (teacher_id)
        REFERENCES users (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE INDEX idx_courses_teacher_id         ON courses (teacher_id);
CREATE INDEX idx_courses_course_code        ON courses (course_code);
CREATE INDEX idx_courses_department         ON courses (department);
CREATE INDEX idx_courses_academic_year      ON courses (academic_year);
CREATE INDEX idx_courses_course_status      ON courses (course_status);
CREATE INDEX idx_courses_is_active          ON courses (is_active);
CREATE INDEX idx_courses_is_deleted         ON courses (is_deleted);
CREATE INDEX idx_courses_instructor_name    ON courses (instructor_name);
CREATE INDEX idx_courses_schedule_day       ON courses (schedule_day);