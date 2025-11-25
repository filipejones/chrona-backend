package com.chrona.controller;

import com.chrona.domain.Project;
import com.chrona.domain.Phase;
import com.chrona.domain.Task;
import com.chrona.dto.TaskRequest;
import com.chrona.repository.PhaseRepository;
import com.chrona.repository.ProjectRepository;
import com.chrona.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tasks")
@Validated
public class TasksController {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final PhaseRepository phaseRepository;

    public TasksController(TaskRepository taskRepository, ProjectRepository projectRepository, PhaseRepository phaseRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.phaseRepository = phaseRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TaskRequest request) {
        final Optional<Project> projectOpt = projectRepository.findById(request.getProjectId());
        if (projectOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Projeto não encontrado para o projectId informado."));
        }

        Optional<Task> parentOpt = Optional.empty();
        if (request.getParentId() != null) {
            parentOpt = taskRepository.findById(request.getParentId());
            if (parentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Tarefa pai não encontrada para o parentId informado."));
            }
            if (!parentOpt.get().getProject().getId().equals(projectOpt.get().getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "A tarefa pai precisa pertencer ao mesmo projeto."));
            }
        }

        Phase phase = null;
        if (request.getPhaseId() != null) {
            phase = phaseRepository.findById(request.getPhaseId())
                    .filter(p -> p.getProject().getId().equals(projectOpt.get().getId()))
                    .orElse(null);
            if (phase == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "A etapa precisa pertencer ao mesmo projeto."));
            }
        }

        Task task = Task.builder()
                .name(request.getName())
                .project(projectOpt.get())
                .phase(phase)
                .status(request.getStatus() != null ? request.getStatus() : "Ativo")
                .billable(request.getBillable() != null ? request.getBillable() : Boolean.TRUE)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        parentOpt.ifPresent(task::setParent);
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        Optional<Project> projectOpt = projectRepository.findById(request.getProjectId());
        if (projectOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Projeto não encontrado para o projectId informado."));
        }

        Optional<Task> parentOpt = Optional.empty();
        if (request.getParentId() != null) {
            parentOpt = taskRepository.findById(request.getParentId());
            if (parentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Tarefa pai não encontrada para o parentId informado."));
            }
            if (!parentOpt.get().getProject().getId().equals(projectOpt.get().getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "A tarefa pai precisa pertencer ao mesmo projeto."));
            }
        }
        final Task resolvedParent = parentOpt.orElse(null);

        Phase phase = null;
        if (request.getPhaseId() != null) {
            phase = phaseRepository.findById(request.getPhaseId())
                    .filter(p -> p.getProject().getId().equals(projectOpt.get().getId()))
                    .orElse(null);
            if (phase == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "A etapa precisa pertencer ao mesmo projeto."));
            }
        }

        final Phase resolvedPhase = phase;

        return taskRepository.findById(id)
                .map(existing -> {
                    existing.setProject(projectOpt.get());
                    if (request.getParentId() != null) {
                        existing.setParent(resolvedParent);
                    } else {
                        existing.setParent(null);
                    }
                    existing.setPhase(resolvedPhase);
                    existing.setName(request.getName());
                    existing.setBillable(request.getBillable() != null ? request.getBillable() : existing.getBillable());
                    existing.setStatus(request.getStatus() != null ? request.getStatus() : existing.getStatus());
                    existing.setUpdatedAt(OffsetDateTime.now());
                    return ResponseEntity.ok(taskRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return taskRepository.findById(id)
                .map(existing -> {
                    taskRepository.delete(existing);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
