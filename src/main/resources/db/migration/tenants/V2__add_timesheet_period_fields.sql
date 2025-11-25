-- Add missing columns to timesheet_periods table
ALTER TABLE timesheet_periods
    ADD COLUMN total_hours NUMERIC(5, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN rejection_reason TEXT;
