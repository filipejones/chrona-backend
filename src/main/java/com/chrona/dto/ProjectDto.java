package com.chrona.dto;

import com.chrona.domain.Project;

public record ProjectDto(Long id,
                         String name,
                         Long clientId,
                         String clientName,
                         String status) {

    public static ProjectDto from(Project project) {
        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getClient() != null ? project.getClient().getId() : null,
                project.getClient() != null ? project.getClient().getName() : null,
                project.getStatus()
        );
    }
}
