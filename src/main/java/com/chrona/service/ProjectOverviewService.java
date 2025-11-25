package com.chrona.service;

import com.chrona.domain.Phase;
import com.chrona.domain.Project;
import com.chrona.domain.Task;
import com.chrona.dto.ProjectOverview;
import com.chrona.repository.ExpenseRepository;
import com.chrona.repository.PhaseRepository;
import com.chrona.repository.ProjectRepository;
import com.chrona.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProjectOverviewService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final PhaseRepository phaseRepository;
    private final ExpenseRepository expenseRepository;

    public ProjectOverviewService(ProjectRepository projectRepository,
                                  TaskRepository taskRepository,
                                  PhaseRepository phaseRepository,
                                  ExpenseRepository expenseRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.phaseRepository = phaseRepository;
        this.expenseRepository = expenseRepository;
    }

    public ProjectOverview getOverview(Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return null;
        }

        List<Task> tasks = taskRepository.findByProjectId(projectId);
        long activeTasks = tasks.stream().filter(t -> "Ativo".equalsIgnoreCase(t.getStatus())).count();
        long archivedTasks = tasks.stream().filter(t -> "Arquivado".equalsIgnoreCase(t.getStatus())).count();

        List<Phase> phases = phaseRepository.findByProjectId(projectId);
        int phasesGreen = 0, phasesYellow = 0, phasesRed = 0;
        double totalBudget = 0;
        for (Phase phase : phases) {
            if (phase.getBudget() != null) {
                totalBudget += phase.getBudget().doubleValue();
            }
            // burn status placeholders (needs financial service if available)
            // For now, categorize by budget only if provided, else neutral.
        }

        // TODO: If FinancialService exists, compute burn rate with labor cost; for now, sum expenses per phase.
        double totalExpenses = expenseRepository.findByProjectId(projectId)
                .stream()
                .map(e -> e.getAmount() != null ? e.getAmount() : BigDecimal.ZERO)
                .mapToDouble(BigDecimal::doubleValue)
                .sum();

        double totalBurnPercent = totalBudget > 0 ? (totalExpenses / totalBudget) * 100.0 : 0.0;

        return new ProjectOverview(
                project.getId(),
                project.getName(),
                project.getClient() != null ? project.getClient().getName() : null,
                project.getStatus(),
                activeTasks,
                archivedTasks,
                phasesGreen,
                phasesYellow,
                phasesRed,
                totalExpenses,
                totalBudget,
                totalBurnPercent
        );
    }
}
