package com.chrona.repository;

import com.chrona.domain.TimesheetPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimesheetPeriodRepository extends JpaRepository<TimesheetPeriod, Long> {
}
