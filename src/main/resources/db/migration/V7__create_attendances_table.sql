CREATE TABLE attendances (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),

    student_id          UUID            NOT NULL,
    course_id           UUID            NOT NULL,
    marked_by_id        UUID            NOT NULL,

    attendance_date     DATE            NOT NULL,
    class_start_time    TIME,
    class_end_time      TIME,
    check_in_time       TIME,
    check_out_time      TIME,
    status              VARCHAR(20)     NOT NULL DEFAULT 'ABSENT'
                            CHECK (status IN ('PRESENT','ABSENT','LATE','EXCUSED','HALF_DAY')),
    academic_year       VARCHAR(20)     NOT NULL,
    semester            VARCHAR(50),
    current_class       VARCHAR(50),
    subject             VARCHAR(255),

    late_minutes        INTEGER,
    absence_reason      TEXT,
    absence_type        VARCHAR(20)
                            CHECK (absence_type IN ('MEDICAL','PERSONAL','FAMILY','RELIGIOUS','WEATHER','UNKNOWN','OTHER')),
    excused_by_parent   BOOLEAN         NOT NULL DEFAULT FALSE,
    excused_by_admin    BOOLEAN         NOT NULL DEFAULT FALSE,
    excuse_note         TEXT,

    session_type        VARCHAR(20)
                            CHECK (session_type IN ('MORNING','AFTERNOON','EVENING','FULL_DAY')),
    session_number      INTEGER,
    room_number         VARCHAR(20),
    building            VARCHAR(100),
    day_of_week         VARCHAR(20)
                            CHECK (day_of_week IN ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),

    method              VARCHAR(20)     NOT NULL DEFAULT 'MANUAL'
                            CHECK (method IN ('MANUAL','QR_CODE','BIOMETRIC','ONLINE','CARD_SCAN')),
    verified            BOOLEAN         NOT NULL DEFAULT FALSE,
    verification_note   TEXT,

    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    deleted_at          TIMESTAMPTZ,

    remarks             TEXT,

    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_attendances_student_id
        FOREIGN KEY (student_id)   REFERENCES users(id)    ON DELETE CASCADE,

    CONSTRAINT fk_attendances_course_id
        FOREIGN KEY (course_id)    REFERENCES courses(id)  ON DELETE CASCADE,

    CONSTRAINT fk_attendances_marked_by_id
        FOREIGN KEY (marked_by_id) REFERENCES users(id)    ON DELETE RESTRICT,

    CONSTRAINT uk_attendances_student_course_date
        UNIQUE (student_id, course_id, attendance_date)
);

CREATE INDEX idx_attendances_student_id  ON attendances (student_id);
CREATE INDEX idx_attendances_course_id   ON attendances (course_id);
CREATE INDEX idx_attendances_marked_by_id ON attendances (marked_by_id);
CREATE INDEX idx_attendances_date        ON attendances (attendance_date);
CREATE INDEX idx_attendances_status      ON attendances (status);
CREATE INDEX idx_attendances_academic_year ON attendances (academic_year);