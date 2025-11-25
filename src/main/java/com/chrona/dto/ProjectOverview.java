package com.chrona.dto;

public record ProjectOverview(
        Long projectId,
        String projectName,
        String clientName,
        String status,
        long activeTasks,
        long archivedTasks,
        int phasesGreen,
        int phasesYellow,
        int phasesRed,
        double totalExpenses,
        double totalBudget,
        double totalBurnPercent
) {}
