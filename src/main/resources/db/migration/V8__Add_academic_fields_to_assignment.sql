CREATE TABLE assignment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    teacher_id UUID NOT NULL,
    student_id UUID NOT NULL,
    name VARCHAR(255),
    instructions TEXT,
    available_from TIMESTAMP,
    deadline TIMESTAMP,
    total_points DOUBLE PRECISION,
    passing_score NUMERIC,
    max_attempts INT DEFAULT 1,
    category VARCHAR(255),
    is_group_work BOOLEAN,
    attachment_url VARCHAR(255),
    grading_type VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_teacher
        FOREIGN KEY (teacher_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_student
        FOREIGN KEY (student_id)
        REFERENCES student_profiles(id)
        ON DELETE CASCADE
);