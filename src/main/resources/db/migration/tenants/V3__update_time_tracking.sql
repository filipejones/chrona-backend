-- Add estimated_hours to tasks
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS estimated_hours NUMERIC(19, 2) DEFAULT 0;

-- Update time_entries table
ALTER TABLE time_entries ALTER COLUMN end_time DROP NOT NULL;
ALTER TABLE time_entries ADD COLUMN IF NOT EXISTS rejection_reason TEXT;
ALTER TABLE time_entries ADD COLUMN IF NOT EXISTS duration_minutes INTEGER;

-- Ensure unique active timer per user
CREATE UNIQUE INDEX IF NOT EXISTS unique_active_timer_per_user ON time_entries (user_id) WHERE end_time IS NULL;

-- Add permissions (assuming roles 'Admin' and 'Manager' exist, if not this might do nothing or fail if I force it. 
-- Safest is to insert if roles exist)

INSERT INTO role_permissions (role_id, permission_id)
SELECT id, 'time-entry:create' FROM roles WHERE name IN ('Admin', 'Manager')
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT id, 'time-entry:read' FROM roles WHERE name IN ('Admin', 'Manager')
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT id, 'time-entry:update' FROM roles WHERE name IN ('Admin', 'Manager')
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT id, 'time-entry:delete' FROM roles WHERE name IN ('Admin', 'Manager')
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT id, 'time-entry:submit' FROM roles WHERE name IN ('Admin', 'Manager')
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT id, 'time-entry:approve' FROM roles WHERE name IN ('Admin', 'Manager')
ON CONFLICT DO NOTHING;
