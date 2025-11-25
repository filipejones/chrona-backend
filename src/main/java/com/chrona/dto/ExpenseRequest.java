package com.chrona.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ExpenseRequest {
    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDate date;

    @NotBlank
    private String description;

    private boolean reimbursable;

    @NotNull
    private Long projectId;

    @NotNull
    private Long userId;

    private Long phaseId;
}
