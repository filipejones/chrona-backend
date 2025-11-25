package com.chrona.repository;

import com.chrona.domain.Phase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, Long> {
    List<Phase> findByProjectId(Long projectId);
    List<Phase> findByProjectId(Long projectId, Pageable pageable);
}
