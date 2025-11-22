package com.chrona.config;

import com.chrona.domain.OrganizationSettings;
import com.chrona.domain.Role;
import com.chrona.domain.RolePermission;
import com.chrona.domain.RolePermissionId;
import com.chrona.domain.User;
import com.chrona.repository.OrganizationSettingsRepository;
import com.chrona.repository.RoleRepository;
import com.chrona.repository.UserRepository;
import com.chrona.service.TenantService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@org.springframework.context.annotation.Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final OrganizationSettingsRepository settingsRepository;
    private final PasswordEncoder passwordEncoder;

    private final com.chrona.repository.ClientRepository clientRepository;
    private final com.chrona.repository.ProjectRepository projectRepository;
    private final com.chrona.repository.TaskRepository taskRepository;
    private final com.chrona.repository.TimeEntryRepository timeEntryRepository;
    private final com.chrona.repository.PhaseRepository phaseRepository;
    private final com.chrona.repository.ExpenseRepository expenseRepository;

    private boolean alreadySetup;

    private final TenantService tenantService;

    @Override
    @Transactional
    public void run(String... args) {
        if (alreadySetup) {
            return;
        }

        // 1. Ensure Default Tenant exists
        String defaultTenantId = "demo";
        if (!tenantService.findById(defaultTenantId).isPresent()) {
            log.info("Criando tenant padrão de desenvolvimento: {}", defaultTenantId);
            tenantService.createTenant(defaultTenantId, "Demo Organization");
        }

        // 2. Set Tenant Context
        com.chrona.multitenancy.TenantContext.setCurrentTenant(defaultTenantId);

        try {
            // 3. Create Roles and Users within the Tenant Schema
            Role adminRole = ensureRole("ADMIN", List.of(
                    "client:create", "client:update", "client:delete",
                    "project:create", "project:update",
                    "task:create", "task:update", "task:delete",
                    "time-entry:create", "time-entry:update", "time-entry:delete",
                    "timesheet-period:submit", "timesheet-period:approve", "timesheet-period:reject",
                    "settings:update", "role:create", "role:update", "role:delete",
                    "user:role"));
            ensureRole("MANAGER", List.of(
                    "client:update",
                    "project:update", "task:update",
                    "time-entry:update",
                    "timesheet-period:approve"));
            ensureRole("TIMEKEEPER", List.of(
                    "time-entry:create", "time-entry:update",
                    "timesheet-period:submit"));

            ensureSettings();
            User adminUser = ensureAdminUser(adminRole);
            User clientUser = ensureClientUser(roleRepository.findByName("MANAGER").get());

            ensureSampleData(adminUser, clientUser);

            alreadySetup = true;
        } finally {
            // 4. Clear Context
            com.chrona.multitenancy.TenantContext.clear();
        }
    }

    private Role ensureRole(String name, List<String> permissions) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    log.info("Criando papel {} com {} permissões", name, permissions.size());
                    Role role = Role.builder().name(name).build();
                    Set<RolePermission> permissionEntities = permissions.stream()
                            .map(permission -> RolePermission.builder()
                                    .id(new RolePermissionId(null, permission))
                                    .role(role)
                                    .build())
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                    role.setPermissions(permissionEntities);
                    return roleRepository.save(role);
                });
    }

    private void ensureSettings() {
        if (!settingsRepository.existsById(1)) {
            log.info("Inicializando configurações organizacionais padrão");
            settingsRepository.save(OrganizationSettings.builder()
                    .id(1)
                    .backdatingDays(7)
                    .build());
        }
    }

    private User ensureAdminUser(Role adminRole) {
        return userRepository.findByEmail("admin@chrona.local")
                .orElseGet(() -> {
                    log.info("Criando usuário administrador padrão");
                    User user = User.builder()
                            .name("Chrona Admin")
                            .email("admin@chrona.local")
                            .passwordHash(passwordEncoder.encode("ChangeMe123!"))
                            .role(adminRole)
                            .build();
                    return userRepository.save(user);
                });
    }

    private User ensureClientUser(Role managerRole) {
        return userRepository.findByEmail("client@chrona.local")
                .orElseGet(() -> {
                    log.info("Criando usuário cliente padrão");
                    User user = User.builder()
                            .name("Chrona Client")
                            .email("client@chrona.local")
                            .passwordHash(passwordEncoder.encode("ChangeMe123!"))
                            .role(managerRole)
                            .build();
                    return userRepository.save(user);
                });
    }

    private void ensureSampleData(User admin, User client) {
        if (clientRepository.count() > 0) {
            return;
        }
        log.info("Gerando dados de exemplo...");

        // Clients
        com.chrona.domain.Client acme = clientRepository.save(com.chrona.domain.Client.builder()
                .name("Acme Corp")
                .contactName("Wile E. Coyote")
                .contactEmail("coyote@acme.com")
                .status("Ativo")
                .createdAt(java.time.OffsetDateTime.now())
                .updatedAt(java.time.OffsetDateTime.now())
                .build());

        com.chrona.domain.Client globex = clientRepository.save(com.chrona.domain.Client.builder()
                .name("Globex Corporation")
                .contactName("Hank Scorpio")
                .contactEmail("hank@globex.com")
                .status("Ativo")
                .createdAt(java.time.OffsetDateTime.now())
                .updatedAt(java.time.OffsetDateTime.now())
                .build());

        // Projects
        com.chrona.domain.Project websiteRedesign = projectRepository.save(com.chrona.domain.Project.builder()
                .name("Website Redesign")
                .client(acme)
                .status("Ativo")
                .createdAt(java.time.OffsetDateTime.now())
                .updatedAt(java.time.OffsetDateTime.now())
                .build());

        com.chrona.domain.Project mobileApp = projectRepository.save(com.chrona.domain.Project.builder()
                .name("Mobile App MVP")
                .client(globex)
                .status("Ativo")
                .createdAt(java.time.OffsetDateTime.now())
                .updatedAt(java.time.OffsetDateTime.now())
                .build());

        // Phases
        com.chrona.domain.Phase discovery = phaseRepository.save(com.chrona.domain.Phase.builder()
                .name("Discovery")
                .project(websiteRedesign)
                .budget(java.math.BigDecimal.valueOf(40))
                .standard(false)
                .build());

        com.chrona.domain.Phase development = phaseRepository.save(com.chrona.domain.Phase.builder()
                .name("Development")
                .project(websiteRedesign)
                .budget(java.math.BigDecimal.valueOf(120))
                .standard(false)
                .build());

        // Tasks
        com.chrona.domain.Task requirements = taskRepository.save(com.chrona.domain.Task.builder()
                .name("Gather Requirements")
                .project(websiteRedesign)
                .phase(discovery)
                .billable(true)
                .status("Ativo")
                .createdAt(java.time.OffsetDateTime.now())
                .updatedAt(java.time.OffsetDateTime.now())
                .build());

        com.chrona.domain.Task frontendDev = taskRepository.save(com.chrona.domain.Task.builder()
                .name("Frontend Implementation")
                .project(websiteRedesign)
                .phase(development)
                .billable(true)
                .status("Ativo")
                .createdAt(java.time.OffsetDateTime.now())
                .updatedAt(java.time.OffsetDateTime.now())
                .build());

        // Time Entries (Last 7 days)
        java.time.LocalDate today = java.time.LocalDate.now();
        for (int i = 0; i < 5; i++) {
            java.time.LocalDate date = today.minusDays(i);
            timeEntryRepository.save(com.chrona.domain.TimeEntry.builder()
                    .user(admin)
                    .project(websiteRedesign)
                    .task(requirements)
                    .workDate(date)
                    .startTime(java.time.LocalTime.of(9, 0))
                    .endTime(java.time.LocalTime.of(13, 0))
                    .description("Working on requirements analysis")
                    .status("Submetido")
                    .createdAt(java.time.OffsetDateTime.now())
                    .updatedAt(java.time.OffsetDateTime.now())
                    .build());
        }

        // Expenses
        expenseRepository.save(com.chrona.domain.Expense.builder()
                .amount(java.math.BigDecimal.valueOf(150.00))
                .date(java.time.LocalDate.now().minusDays(2))
                .description("Hosting Subscription")
                .reimbursable(true)
                .project(websiteRedesign)
                .phase(development)
                .user(admin)
                .build());

        expenseRepository.save(com.chrona.domain.Expense.builder()
                .amount(java.math.BigDecimal.valueOf(45.50))
                .date(java.time.LocalDate.now().minusDays(1))
                .description("Team Lunch")
                .reimbursable(false)
                .project(websiteRedesign)
                .user(client)
                .build());
    }
}
