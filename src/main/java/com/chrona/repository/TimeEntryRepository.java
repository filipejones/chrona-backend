package com.chrona.repository;

import com.chrona.domain.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    List<TimeEntry> findByProjectId(Long projectId);

    List<TimeEntry> findByTaskId(Long taskId);
}
