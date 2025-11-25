package com.chrona.controller;

import com.chrona.domain.OrganizationSettings;
import com.chrona.dto.BootstrapResponse;
import com.chrona.mapper.BootstrapMapper;
import com.chrona.repository.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/v1")
public class BootstrapController {
    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TimeEntryRepository timeEntryRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TimesheetPeriodRepository timesheetPeriodRepository;
    private final OrganizationSettingsRepository settingsRepository;
    private final BootstrapMapper mapper;

    public BootstrapController(ClientRepository clientRepository,
            ProjectRepository projectRepository,
            TaskRepository taskRepository,
            TimeEntryRepository timeEntryRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            TimesheetPeriodRepository timesheetPeriodRepository,
            OrganizationSettingsRepository settingsRepository,
            BootstrapMapper mapper) {
        this.clientRepository = clientRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.timeEntryRepository = timeEntryRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.timesheetPeriodRepository = timesheetPeriodRepository;
        this.settingsRepository = settingsRepository;
        this.mapper = mapper;
    }

    @GetMapping("/bootstrap")
    @Transactional(readOnly = true)
    public BootstrapResponse bootstrap() {
        return mapper.toResponse(
                clientRepository.findAll(),
                projectRepository.findAll(),
                taskRepository.findAll(),
                timeEntryRepository.findAll(),
                userRepository.findAllWithRoles(),
                roleRepository.findAllWithPermissions(),
                timesheetPeriodRepository.findAll(),
                settingsRepository.findById(1).orElse(null));
    }
}
