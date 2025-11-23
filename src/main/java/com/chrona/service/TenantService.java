package com.chrona.service;

import com.chrona.domain.Tenant;
import com.chrona.repository.TenantRepository;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
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
                "task:create", "task:update", "task:delete",
                "time-entry:create", "time-entry:update", "time-entry:delete",
                "timesheet-period:submit", "timesheet-period:approve", "timesheet-period:reject",
                "settings:update", "role:create", "role:update", "role:delete",
                "user:role"));

        ensureRole("MANAGER", java.util.List.of(
                "client:update",
                "project:update", "task:update",
                "time-entry:update",
                "timesheet-period:approve"));

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

    private com.chrona.domain.Role ensureRole(String name, java.util.List<String> permissions) {
        return roleRepository.findByName(name).orElseGet(() -> {
            com.chrona.domain.Role role = com.chrona.domain.Role.builder()
                    .name(name)
                    .build();
            role = roleRepository.save(role);
            return role;
        });
    }

    public java.util.Optional<Tenant> findById(String id) {
        return tenantRepository.findById(id);
    }

    public java.util.List<Tenant> findAll() {
        return tenantRepository.findAll();
    }
}
