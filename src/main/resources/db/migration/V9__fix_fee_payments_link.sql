ALTER TABLE fee_payments
DROP CONSTRAINT IF EXISTS fk_fee_payments_account;

ALTER TABLE fee_payments
DROP COLUMN IF EXISTS student_account_id;

ALTER TABLE fee_payments
ADD COLUMN IF NOT EXISTS student_profile_id UUID NOT NULL
    REFERENCES student_profiles(id) ON DELETE RESTRICT;

CREATE INDEX IF NOT EXISTS idx_fee_payments_student_profile_id
    ON fee_payments(student_profile_id);

CREATE INDEX IF NOT EXISTS idx_fee_payments_student_number
    ON fee_payments(student_number);