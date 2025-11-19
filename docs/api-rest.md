# Documentação da API REST — Chrona Backend

## 1. Visão geral
O backend Chrona é uma API REST criada com Java 17+ e Spring Boot 3+, seguindo boas práticas de segurança, modularidade e testabilidade. A autenticação é baseada em JWT e oferece controles granulares de permissões via papéis (roles). O objetivo desta documentação é registrar a arquitetura recomendada, o esquema relacional em PostgreSQL e os endpoints expostos por `/api/v1`.

## 2. Arquitetura e tecnologias recomendadas
- **Linguagem e framework:** Java 17+ com Spring Boot 3+ e Spring Web MVC para expor os endpoints REST, Spring Data JPA/Hibernate para persistência e lombok para reduzir boilerplate.
- **Banco de dados:** PostgreSQL 15+, aproveitando tipos como `TEXT`, `TIMESTAMP WITH TIME ZONE`, `NUMERIC` e arrays nativos (`TEXT[]`).
- **Autenticação e autorização:** Spring Security com JWT; o token é emitido no `/auth/login` e deve ser enviado no header `Authorization: Bearer <token>` para todos os demais endpoints.
- **ORM:** Spring Data JPA com Hibernate facilita o mapeamento objeto-relacional, gerencia transações e habilita auditoria (timestamps de criação/atualização).
- **Controladores e serviços:** separar camadas, com `@RestController` para endpoints, `@Service` para regras de negócio e `@Repository` para acesso ao banco.
- **Validação e exceções:** `@Valid` nas requisições, tratamento global com `@ControllerAdvice` e DTOs de entrada/saída.

## 3. Esquema do banco de dados (PostgreSQL 15+)
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE role_permissions (
    role_id INT NOT NULL,
    permission_id VARCHAR(100) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Ativo',
    contact_name VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(50),
    address TEXT,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    client_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Ativo',
    FOREIGN KEY (client_id) REFERENCES clients(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tasks (
    id SERIAL PRIMARY KEY,
    project_id INT NOT NULL,
    parent_id INT,
    name VARCHAR(255) NOT NULL,
    is_billable BOOLEAN NOT NULL DEFAULT true,
    status VARCHAR(20) NOT NULL DEFAULT 'Ativo',
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (parent_id) REFERENCES tasks(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE time_entries (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    project_id INT NOT NULL,
    task_id INT NOT NULL,
    work_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    description TEXT NOT NULL,
    notes TEXT,
    tags TEXT[],
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE timesheet_periods (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    total_hours NUMERIC(5, 2) NOT NULL,
    rejection_reason TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE(user_id, start_date, end_date)
);

CREATE TABLE organization_settings (
    id INT PRIMARY KEY DEFAULT 1,
    backdating_days INT NOT NULL DEFAULT 7,
    CONSTRAINT single_row_check CHECK (id = 1)
);
```

## 4. Especificação dos endpoints REST (`/api/v1`)
Todos os endpoints abaixo, exceto `POST /auth/login`, exigem autenticação via JWT.

- **GET `/api/v1/bootstrap`** – Retorna todo o conjunto inicial de dados (`clients`, `projects`, `tasks`, `timeEntries`, `users`, `roles`, `periods`, `settings`) para otimizar o carregamento do frontend.
- **POST `/api/v1/clients`** – Cria um cliente; corpo com dados essenciais.
- **PUT `/api/v1/clients/{id}`** – Atualiza um cliente existente.
- **DELETE `/api/v1/clients/{id}`** – Remove um cliente (considerar soft delete se necessário).
- **POST `/api/v1/projects`** – Cria um projeto vinculado a um cliente.
- **PUT `/api/v1/projects/{id}`** – Atualiza dados do projeto.
- **POST `/api/v1/tasks`** – Cria uma tarefa (pode ter `parentId` para aninhamento).
- **PUT `/api/v1/tasks/{id}`** – Atualiza uma tarefa (status, nome, faturável etc.).
- **DELETE `/api/v1/tasks/{id}`** – Exclui uma tarefa.
- **POST `/api/v1/time-entries`** – Cria um lançamento de horas; corpo omite `id` e `status`.
- **PUT `/api/v1/time-entries/{id}`** – Atualiza um lançamento existente; corpo inclui o objeto completo.
- **DELETE `/api/v1/time-entries/{id}`** – Remove um lançamento; responde com `204 No Content`.
- **POST `/api/v1/timesheet-periods/{id}/submit`** – Marca o período como `SUBMITTED`; retorna o período atualizado.
- **POST `/api/v1/timesheet-periods/{id}/approve`** – Marca o período como `APPROVED`; retorna o objeto atualizado.
- **POST `/api/v1/timesheet-periods/{id}/reject`** – Marca o período como `REJECTED`; corpo `{ "reason": "string" }`.
- **PUT `/api/v1/settings`** – Atualiza as configurações da organização (ex: `backdatingDays`) e retorna o estado salvo.
- **PUT `/api/v1/users/{userId}/role`** – Altera o papel de um usuário; corpo `{ "roleId": number }`.
- **POST `/api/v1/roles`** – Cria um novo papel.
- **PUT `/api/v1/roles/{id}`** – Atualiza informações do papel.
- **DELETE `/api/v1/roles/{id}`** – Remove um papel (considerar cascateamento nas permissões).

### Autorização e permissões
- As ações críticas (criar/atualizar/excluir) devem validar permissões via roles e role_permissions, evitando que usuários de menor privilégio alterem recursos restritos.

### Boas práticas recomendadas
- Use DTOs para separar entidades persistentes das requisições e respostas.
- Trate erros com mensagens claras e padronize o envelope de resposta.
- Logue eventos de auditoria relevantes (criação/alteração/exclusão de dados sensíveis).
