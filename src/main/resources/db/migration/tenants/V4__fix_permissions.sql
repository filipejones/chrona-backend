-- Fix permissions for ADMIN and MANAGER roles (case sensitive)

INSERT INTO role_permissions (role_id, permission_id)
SELECT id, 'time-entry:read' FROM roles WHERE name IN ('ADMIN', 'MANAGER')
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT id, 'time-entry:create' FROM roles WHERE name IN ('ADMIN', 'MANAGER')
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT id, 'time-entry:update' FROM roles WHERE name IN ('ADMIN', 'MANAGER')
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT id, 'time-entry:delete' FROM roles WHERE name IN ('ADMIN', 'MANAGER')
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT id, 'time-entry:submit' FROM roles WHERE name IN ('ADMIN', 'MANAGER')
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT id, 'time-entry:approve' FROM roles WHERE name IN ('ADMIN', 'MANAGER')
ON CONFLICT DO NOTHING;
