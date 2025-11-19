package com.chrona.dto;

import com.chrona.domain.TimesheetPeriod;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TimesheetPeriodDto(Long id,
                                 Long userId,
                                 String userName,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 String status,
                                 BigDecimal totalHours,
                                 String rejectionReason) {

    public static TimesheetPeriodDto from(TimesheetPeriod period) {
        return new TimesheetPeriodDto(
                period.getId(),
                period.getUser() != null ? period.getUser().getId() : null,
                period.getUser() != null ? period.getUser().getName() : null,
                period.getStartDate(),
                period.getEndDate(),
                period.getStatus(),
                period.getTotalHours(),
                period.getRejectionReason()
        );
    }
}
