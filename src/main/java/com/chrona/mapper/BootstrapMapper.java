package com.chrona.mapper;

import com.chrona.domain.*;
import com.chrona.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BootstrapMapper {
    public BootstrapResponse toResponse(List<Client> clients,
                                        List<Project> projects,
                                        List<Task> tasks,
                                        List<TimeEntry> timeEntries,
                                        List<User> users,
                                        List<Role> roles,
                                        List<TimesheetPeriod> periods,
                                        OrganizationSettings settings) {
        return new BootstrapResponse(
                clients.stream().map(ClientDto::from).toList(),
                projects.stream().map(ProjectDto::from).toList(),
                tasks.stream().map(TaskDto::from).toList(),
                timeEntries.stream().map(TimeEntryDto::from).toList(),
                users.stream().map(UserDto::from).toList(),
                roles.stream().map(RoleDto::from).toList(),
                periods.stream().map(TimesheetPeriodDto::from).toList(),
                OrganizationSettingsDto.from(settings)
        );
    }
}
