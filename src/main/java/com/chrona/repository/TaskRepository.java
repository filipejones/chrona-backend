package com.chrona.repository;

import com.chrona.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.domain.Pageable;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByProjectId(Long projectId, Pageable pageable);

    List<Task> findByPhaseId(Long phaseId);
}
