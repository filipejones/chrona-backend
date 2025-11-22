package com.chrona.controller;

import com.chrona.domain.Phase;
import com.chrona.domain.Project;
import com.chrona.dto.PhaseFinancialStatusDTO;
import com.chrona.repository.PhaseRepository;
import com.chrona.repository.ProjectRepository;
import com.chrona.service.FinancialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/phases")
@Validated
@RequiredArgsConstructor
public class PhasesController {

    private final PhaseRepository phaseRepository;
    private final ProjectRepository projectRepository;
    private final FinancialService financialService;

    @GetMapping
    public List<Phase> list(@RequestParam(required = false) Long projectId) {
        if (projectId != null) {
            return phaseRepository.findByProjectId(projectId);
        }
        return phaseRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Phase> create(@RequestBody Phase phase) {
        if (phase.getProject() != null && phase.getProject().getId() != null) {
            Project project = projectRepository.findById(phase.getProject().getId()).orElse(null);
            if (project == null) {
                return ResponseEntity.badRequest().build();
            }
            phase.setProject(project);
        }
        // If standard is true, project might be null (system wide phase)

        phase.setCreatedAt(LocalDateTime.now());
        phase.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(phaseRepository.save(phase));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Phase> update(@PathVariable Long id, @RequestBody Phase phaseDetails) {
        return phaseRepository.findById(id)
                .map(phase -> {
                    phase.setName(phaseDetails.getName());
                    phase.setDescription(phaseDetails.getDescription());
                    phase.setStandard(phaseDetails.isStandard());
                    phase.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(phaseRepository.save(phase));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (phaseRepository.existsById(id)) {
            phaseRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/financial-status")
    public ResponseEntity<PhaseFinancialStatusDTO> getFinancialStatus(@PathVariable Long id) {
        return ResponseEntity.ok(financialService.getPhaseFinancialStatus(id));
    }
}
