package com.chrona.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProjectRequest {
    @NotBlank
    private String name;

    @NotNull
    private Long clientId;

    private String status;
}
