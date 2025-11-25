package com.chrona.service;

import com.chrona.domain.Tenant;
import com.chrona.repository.TenantRepository;
import com.chrona.domain.Role;
import com.chrona.domain.RolePermission;
import com.chrona.domain.RolePermissionId;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@SuppressWarnings("null")
public class TenantService {

    private final TenantRepository tenantRepository;
    private final DataSource dataSource;
    private final com.chrona.repository.UserRepository userRepository;
    private final com.chrona.repository.RoleRepository roleRepository;
    private final com.chrona.repository.OrganizationSettingsRepository settingsRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public TenantService(TenantRepository tenantRepository,
            DataSource dataSource,
            com.chrona.repository.UserRepository userRepository,
            com.chrona.repository.RoleRepository roleRepository,
            com.chrona.repository.OrganizationSettingsRepository settingsRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.tenantRepository = tenantRepository;
        this.dataSource = dataSource;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.settingsRepository = settingsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Tenant createTenant(String tenantId, String name) {
        String schemaName = "tenant_" + tenantId;

        // Create schema
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .defaultSchema(schemaName)
                .locations("classpath:db/migration/tenants")
                .load();

        flyway.migrate();

        // Save tenant metadata
        Tenant tenant = Tenant.builder()
                .id(tenantId)
                .name(name)
                .schemaName(schemaName)
                .build();

        return tenantRepository.save(tenant);
    }

    @org.springframework.transaction.annotation.Transactional
    public Tenant registerTenant(com.chrona.dto.TenantRegistrationDto dto) {
        // 1. Generate Tenant ID (slug)
        String tenantId = dto.getOrganizationName().toLowerCase().replaceAll("[^a-z0-9]", "");
        if (tenantRepository.existsById(tenantId)) {
            throw new RuntimeException("Organization name already taken (ID: " + tenantId + ")");
        }

        // 2. Create Tenant and Schema
        Tenant tenant = createTenant(tenantId, dto.getOrganizationName());

        // 3. Initialize Data in new Schema
        try {
            com.chrona.multitenancy.TenantContext.setCurrentTenant(tenantId);
            initializeTenantData(dto);
        } finally {
            com.chrona.multitenancy.TenantContext.clear();
        }

        return tenant;
    }

    private void initializeTenantData(com.chrona.dto.TenantRegistrationDto dto) {
        // Create Roles
        com.chrona.domain.Role adminRole = ensureRole("ADMIN", java.util.List.of(
                "client:create", "client:update", "client:delete",
                "project:create", "project:update",
                "task:create", "task:update", "task:delete", "task:edit", "task:archive",
                "time-entry:create", "time-entry:update", "time-entry:delete",
                "timesheet-period:submit", "timesheet-period:approve", "timesheet-period:reject",
                "settings:update", "role:create", "role:update", "role:delete",
                "user:role",
                "phase:create", "phase:update", "phase:delete",
                "expense:create", "expense:delete"));

        ensureRole("MANAGER", java.util.List.of(
                "client:update",
                "project:update", "task:update", "task:edit", "task:archive",
                "time-entry:update",
                "timesheet-period:approve",
                "phase:create", "phase:update", "phase:delete",
                "expense:create", "expense:delete"));

        ensureRole("TIMEKEEPER", java.util.List.of(
                "time-entry:create", "time-entry:update",
                "timesheet-period:submit"));

        // Create Settings
        if (settingsRepository.count() == 0) {
            settingsRepository.save(com.chrona.domain.OrganizationSettings.builder()
                    .id(1)
                    .backdatingDays(5)
                    .build());
        }

        // Create Admin User
        if (userRepository.findByEmail(dto.getAdminEmail()).isEmpty()) {
            userRepository.save(com.chrona.domain.User.builder()
                    .name(dto.getAdminName())
                    .email(dto.getAdminEmail())
                    .passwordHash(passwordEncoder.encode(dto.getPassword()))
                    .role(adminRole)

                    .hourlyRate(java.math.BigDecimal.ZERO)
                    .build());
        }
    }

    private Role ensureRole(String name, List<String> permissions) {
        Role role = roleRepository.findByName(name).orElseGet(() -> roleRepository.save(Role.builder().name(name).build()));

        // garante permissões (mesmo para roles já existentes)
        Set<RolePermission> current = role.getPermissions() != null ? role.getPermissions() : new LinkedHashSet<>();
        boolean changed = false;
        for (String perm : permissions) {
            boolean exists = current.stream().anyMatch(rp -> rp.getId() != null && perm.equals(rp.getId().getPermissionId()));
            if (!exists) {
                RolePermission rp = RolePermission.builder()
                        .id(new RolePermissionId(role.getId(), perm))
                        .role(role)
                        .build();
                current.add(rp);
                changed = true;
            }
        }
        if (changed) {
            role.setPermissions(current);
            role = roleRepository.save(role);
        }
        return role;
    }

    public java.util.Optional<Tenant> findById(String id) {
        return tenantRepository.findById(id);
    }

    public java.util.List<Tenant> findAll() {
        return tenantRepository.findAll();
    }

    /**
     * Resolve o tenant pelo e-mail (varre todos os tenants e procura o usuário).
     * Retorna o ID do tenant ou vazio se não encontrar.
     * Lança IllegalStateException se o e-mail existir em mais de um tenant.
     */
    public java.util.Optional<String> resolveTenantByEmail(String email) {
        String foundTenant = null;
        for (Tenant tenant : tenantRepository.findAll()) {
            System.out.println("resolveTenantByEmail scanning tenant=" + tenant.getId());
            try (java.sql.Connection conn = dataSource.getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement("select 1 from users where email = ? limit 1")) {
                conn.setSchema(tenant.getSchemaName());
                ps.setString(1, email);
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        if (foundTenant != null && !foundTenant.equals(tenant.getId())) {
                            throw new IllegalStateException("E-mail presente em múltiplos tenants");
                        }
                        foundTenant = tenant.getId();
                    }
                }
            } catch (java.sql.SQLException e) {
                throw new RuntimeException("Erro ao resolver tenant por e-mail", e);
            }
        }
        return java.util.Optional.ofNullable(foundTenant);
    }
}
