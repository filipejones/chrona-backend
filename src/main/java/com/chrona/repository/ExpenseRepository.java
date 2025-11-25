package com.chrona.repository;

import com.chrona.domain.Expense;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByProjectId(Long projectId);
    List<Expense> findByProjectId(Long projectId, Pageable pageable);

    List<Expense> findByPhaseId(Long phaseId);
    List<Expense> findByPhaseId(Long phaseId, Pageable pageable);
}
