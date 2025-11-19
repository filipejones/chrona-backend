package com.chrona.config;

import com.chrona.domain.OrganizationSettings;
import com.chrona.domain.Role;
import com.chrona.domain.RolePermission;
import com.chrona.domain.RolePermissionId;
import com.chrona.domain.User;
import com.chrona.repository.OrganizationSettingsRepository;
import com.chrona.repository.RoleRepository;
import com.chrona.repository.UserRepository;
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
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final OrganizationSettingsRepository settingsRepository;
    private final PasswordEncoder passwordEncoder;

    private boolean alreadySetup;

    @Override
    @Transactional
    public void run(String... args) {
        if (alreadySetup) {
            return;
        }

        Role adminRole = ensureRole("ADMIN", List.of(
                "client:create", "client:update", "client:delete",
                "project:create", "project:update",
                "task:create", "task:update", "task:delete",
                "time-entry:create", "time-entry:update", "time-entry:delete",
                "timesheet-period:submit", "timesheet-period:approve", "timesheet-period:reject",
                "settings:update", "role:create", "role:update", "role:delete",
                "user:role"
        ));
        ensureRole("MANAGER", List.of(
                "client:update",
                "project:update", "task:update",
                "time-entry:update",
                "timesheet-period:approve"
        ));
        ensureRole("TIMEKEEPER", List.of(
                "time-entry:create", "time-entry:update",
                "timesheet-period:submit"
        ));

        ensureSettings();
        ensureAdminUser(adminRole);

        alreadySetup = true;
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

    private void ensureAdminUser(Role adminRole) {
        userRepository.findByEmail("admin@chrona.local")
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
}
