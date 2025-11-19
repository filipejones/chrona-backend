package com.chrona.dto;

import com.chrona.domain.Task;

public record TaskDto(Long id,
                      Long projectId,
                      String projectName,
                      Long parentId,
                      String name,
                      Boolean billable,
                      String status) {

    public static TaskDto from(Task task) {
        return new TaskDto(
                task.getId(),
                task.getProject() != null ? task.getProject().getId() : null,
                task.getProject() != null ? task.getProject().getName() : null,
                task.getParent() != null ? task.getParent().getId() : null,
                task.getName(),
                task.getBillable(),
                task.getStatus()
        );
    }
}
