CREATE TABLE reviews (
    id                  UUID              PRIMARY KEY DEFAULT gen_random_uuid(),

    course_id           UUID              NOT NULL,
    student_id          UUID              NOT NULL,

    rating              INTEGER           NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment             TEXT              NOT NULL,
    title               VARCHAR(255),
    pros                TEXT,
    cons                TEXT,
    recommended      BOOLEAN           NOT NULL DEFAULT TRUE,

    helpful_count       INTEGER           NOT NULL DEFAULT 0,
    report_count        INTEGER           NOT NULL DEFAULT 0,

    status              VARCHAR(20)       NOT NULL DEFAULT 'PENDING'
                            CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'FLAGGED')),
    approved         BOOLEAN           NOT NULL DEFAULT FALSE,
    deleted          BOOLEAN           NOT NULL DEFAULT FALSE,
    flagged          BOOLEAN           NOT NULL DEFAULT FALSE,
    rejection_reason    TEXT,
    admin_note          TEXT,

    edited           BOOLEAN           NOT NULL DEFAULT FALSE,
    edited_at           TIMESTAMPTZ,

    created_at          TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
    deleted_at          TIMESTAMPTZ,

    CONSTRAINT fk_reviews_course_id
        FOREIGN KEY (course_id)  REFERENCES courses(id) ON DELETE CASCADE,

    CONSTRAINT fk_reviews_student_id
        FOREIGN KEY (student_id) REFERENCES users(id)   ON DELETE CASCADE,

    CONSTRAINT uq_reviews_student_course
        UNIQUE (student_id, course_id)
);

CREATE INDEX idx_reviews_course_id   ON reviews (course_id);
CREATE INDEX idx_reviews_student_id  ON reviews (student_id);
CREATE INDEX idx_reviews_rating      ON reviews (rating);
CREATE INDEX idx_reviews_approved    ON reviews (approved);
CREATE INDEX idx_reviews_status      ON reviews (status);