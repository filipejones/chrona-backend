CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id BIGINT NOT NULL,
    permission_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id BIGINT,
    organization_name VARCHAR(255),
    hourly_rate NUMERIC(19, 2),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Ativo',
    contact_name VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    address TEXT,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    client_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Ativo',
    description TEXT,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_project_client FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE IF NOT EXISTS phases (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    budget NUMERIC(19, 2),
    standard BOOLEAN NOT NULL DEFAULT FALSE,
    project_id BIGINT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_phase_project FOREIGN KEY (project_id) REFERENCES projects(id)
);

CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    parent_id BIGINT,
    phase_id BIGINT,
    name VARCHAR(255) NOT NULL,
    is_billable BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(20) NOT NULL DEFAULT 'Ativo',
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_task_parent FOREIGN KEY (parent_id) REFERENCES tasks(id),
    CONSTRAINT fk_task_phase FOREIGN KEY (phase_id) REFERENCES phases(id)
);

CREATE TABLE IF NOT EXISTS time_entries (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    task_id BIGINT NOT NULL,
    work_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    description TEXT NOT NULL,
    notes TEXT,
    status VARCHAR(20) DEFAULT 'DRAFT',
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_time_entry_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_time_entry_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_time_entry_task FOREIGN KEY (task_id) REFERENCES tasks(id)
);

CREATE TABLE IF NOT EXISTS time_entry_tags (
    time_entry_id BIGINT NOT NULL,
    tag VARCHAR(255),
    CONSTRAINT fk_time_entry_tags_entry FOREIGN KEY (time_entry_id) REFERENCES time_entries(id)
);

CREATE TABLE IF NOT EXISTS expenses (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(19, 2) NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(255) NOT NULL,
    reimbursable BOOLEAN NOT NULL,
    project_id BIGINT NOT NULL,
    phase_id BIGINT,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_expense_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_expense_phase FOREIGN KEY (phase_id) REFERENCES phases(id),
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS timesheet_periods (
    id BIGSERIAL PRIMARY KEY,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(255),
    user_id BIGINT,
    CONSTRAINT fk_timesheet_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS organization_settings (
    id BIGSERIAL PRIMARY KEY,
    backdating_days INTEGER DEFAULT 7
);
