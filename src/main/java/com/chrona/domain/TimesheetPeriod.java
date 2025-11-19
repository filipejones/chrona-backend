package com.chrona.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "timesheet_periods", uniqueConstraints = {
        @UniqueConstraint(name = "user_period_unique", columnNames = {"user_id", "start_date", "end_date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimesheetPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 20)
    private String status = "DRAFT";

    @Column(name = "total_hours", nullable = false, precision = 5, scale = 2)
    private BigDecimal totalHours;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
}
