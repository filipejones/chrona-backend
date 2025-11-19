package com.chrona.dto;

import java.util.List;

public record BootstrapResponse(List<ClientDto> clients,
                                List<ProjectDto> projects,
                                List<TaskDto> tasks,
                                List<TimeEntryDto> timeEntries,
                                List<UserDto> users,
                                List<RoleDto> roles,
                                List<TimesheetPeriodDto> periods,
                                OrganizationSettingsDto settings) {
}
