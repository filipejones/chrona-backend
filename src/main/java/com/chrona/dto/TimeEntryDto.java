package com.chrona.dto;

import com.chrona.domain.TimeEntry;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record TimeEntryDto(Long id,
                           Long userId,
                           String userName,
                           Long projectId,
                           Long taskId,
                           LocalDate workDate,
                           LocalTime startTime,
                           LocalTime endTime,
                           String description,
                           String notes,
                           List<String> tags,
                           String status) {

    public static TimeEntryDto from(TimeEntry entry) {
        return new TimeEntryDto(
                entry.getId(),
                entry.getUser() != null ? entry.getUser().getId() : null,
                entry.getUser() != null ? entry.getUser().getName() : null,
                entry.getProject() != null ? entry.getProject().getId() : null,
                entry.getTask() != null ? entry.getTask().getId() : null,
                entry.getWorkDate(),
                entry.getStartTime(),
                entry.getEndTime(),
                entry.getDescription(),
                entry.getNotes(),
                entry.getTags(),
                entry.getStatus()
        );
    }
}
