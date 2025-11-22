CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id BIGINT,
    organization_name VARCHAR(255),
    hourly_rate NUMERIC(19, 2),
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(255),
    address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(255),
    client_id BIGINT,
    CONSTRAINT fk_project_client FOREIGN KEY (client_id) REFERENCES clients(id)
);

CREATE TABLE IF NOT EXISTS phases (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(255),
    standard BOOLEAN DEFAULT FALSE,
    budget NUMERIC(19, 2),
    project_id BIGINT,
    CONSTRAINT fk_phase_project FOREIGN KEY (project_id) REFERENCES projects(id)
);

CREATE TABLE IF NOT EXISTS expenses (
    id BIGSERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    date DATE NOT NULL,
    reimbursable BOOLEAN DEFAULT FALSE,
    project_id BIGINT,
    user_id BIGINT,
    phase_id BIGINT,
    CONSTRAINT fk_expense_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_expense_phase FOREIGN KEY (phase_id) REFERENCES phases(id)
);

CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(255),
    due_date DATE,
    priority VARCHAR(255),
    billable BOOLEAN DEFAULT TRUE,
    project_id BIGINT,
    assignee_id BIGINT,
    phase_id BIGINT,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_task_assignee FOREIGN KEY (assignee_id) REFERENCES users(id),
    CONSTRAINT fk_task_phase FOREIGN KEY (phase_id) REFERENCES phases(id)
);

CREATE TABLE IF NOT EXISTS time_entries (
    id BIGSERIAL PRIMARY KEY,
    description TEXT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    duration BIGINT,
    task_id BIGINT,
    user_id BIGINT,
    project_id BIGINT,
    CONSTRAINT fk_time_entry_task FOREIGN KEY (task_id) REFERENCES tasks(id),
    CONSTRAINT fk_time_entry_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_time_entry_project FOREIGN KEY (project_id) REFERENCES projects(id)
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
    company_name VARCHAR(255),
    default_currency VARCHAR(255),
    timezone VARCHAR(255)
);
