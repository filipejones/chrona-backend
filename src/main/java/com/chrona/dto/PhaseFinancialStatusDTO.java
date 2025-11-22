package com.chrona.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PhaseFinancialStatusDTO {
    private Long phaseId;
    private BigDecimal budget;
    private BigDecimal totalExpenses;
    private BigDecimal totalLaborCost;
    private BigDecimal burnRate; // Expenses + Labor
    private BigDecimal remainingBudget;
    private double budgetConsumedPercentage;
}
