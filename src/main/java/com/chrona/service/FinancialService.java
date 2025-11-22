package com.chrona.service;

import com.chrona.domain.Expense;
import com.chrona.domain.Phase;
import com.chrona.domain.Task;
import com.chrona.domain.TimeEntry;
import com.chrona.dto.PhaseFinancialStatusDTO;
import com.chrona.repository.ExpenseRepository;
import com.chrona.repository.PhaseRepository;
import com.chrona.repository.TaskRepository;
import com.chrona.repository.TimeEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialService {

    private final PhaseRepository phaseRepository;
    private final ExpenseRepository expenseRepository;
    private final TaskRepository taskRepository;
    private final TimeEntryRepository timeEntryRepository;

    @Transactional(readOnly = true)
    public PhaseFinancialStatusDTO getPhaseFinancialStatus(Long phaseId) {
        Phase phase = phaseRepository.findById(phaseId)
                .orElseThrow(() -> new RuntimeException("Phase not found"));

        BigDecimal budget = phase.getBudget() != null ? phase.getBudget() : BigDecimal.ZERO;

        // 1. Calculate Total Expenses linked to this Phase
        List<Expense> expenses = expenseRepository.findByPhaseId(phaseId);
        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Calculate Labor Cost
        // Find all tasks in this phase
        List<Task> tasks = taskRepository.findByPhaseId(phaseId);
        BigDecimal totalLaborCost = BigDecimal.ZERO;

        for (Task task : tasks) {
            List<TimeEntry> timeEntries = timeEntryRepository.findByTaskId(task.getId());
            for (TimeEntry entry : timeEntries) {
                if (entry.getUser().getHourlyRate() != null) {
                    long minutes = Duration.between(entry.getStartTime(), entry.getEndTime()).toMinutes();
                    BigDecimal hours = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2,
                            RoundingMode.HALF_UP);
                    BigDecimal cost = hours.multiply(entry.getUser().getHourlyRate());
                    totalLaborCost = totalLaborCost.add(cost);
                }
            }
        }

        BigDecimal burnRate = totalExpenses.add(totalLaborCost);
        BigDecimal remainingBudget = budget.subtract(burnRate);

        double percentage = 0.0;
        if (budget.compareTo(BigDecimal.ZERO) > 0) {
            percentage = burnRate.divide(budget, 4, RoundingMode.HALF_UP).doubleValue() * 100;
        }

        return PhaseFinancialStatusDTO.builder()
                .phaseId(phase.getId())
                .budget(budget)
                .totalExpenses(totalExpenses)
                .totalLaborCost(totalLaborCost)
                .burnRate(burnRate)
                .remainingBudget(remainingBudget)
                .budgetConsumedPercentage(percentage)
                .build();
    }
}
