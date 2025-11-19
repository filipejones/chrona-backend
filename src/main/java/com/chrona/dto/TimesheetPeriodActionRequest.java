package com.chrona.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TimesheetPeriodActionRequest {
    @NotBlank
    private String reason;
}
