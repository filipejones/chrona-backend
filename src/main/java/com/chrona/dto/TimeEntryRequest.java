package com.chrona.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
public class TimeEntryRequest {
    @NotNull
    private Long projectId;

    @NotNull
    private Long taskId;

    @NotNull
    private LocalDate workDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    @NotBlank
    private String description;

    private String notes;
    private List<String> tags;
}
