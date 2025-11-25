package com.chrona.dto;

import com.chrona.domain.Task;

public record TaskDto(Long id,
        Long projectId,
        String projectName,
        Long parentId,
        String name,
        Boolean billable,
        String status,
        java.math.BigDecimal estimatedHours,
        java.math.BigDecimal usedHours) {

    public static TaskDto from(Task task) {
        return new TaskDto(
                task.getId(),
                task.getProject() != null ? task.getProject().getId() : null,
                task.getProject() != null ? task.getProject().getName() : null,
                task.getParent() != null ? task.getParent().getId() : null,
                task.getName(),
                task.getBillable(),
                task.getStatus(),
                task.getEstimatedHours(),
                java.math.BigDecimal.ZERO // Frontend can calculate or separate service method can populate
        );
    }
}
