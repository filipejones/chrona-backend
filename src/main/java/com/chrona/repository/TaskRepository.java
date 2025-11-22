package com.chrona.repository;

import com.chrona.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // Added this import for List

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);

    List<Task> findByPhaseId(Long phaseId);
}
