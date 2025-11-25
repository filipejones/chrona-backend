package com.chrona.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@lombok.Setter
public class TaskRequest {
    @NotBlank
    private String name;

    @NotNull
    private Long projectId;

    private Long parentId;
    private Boolean billable;
    private String status;
    private Long phaseId;
}
