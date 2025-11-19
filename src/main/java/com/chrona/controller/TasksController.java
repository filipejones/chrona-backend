package com.chrona.controller;

import com.chrona.domain.Project;
import com.chrona.domain.Task;
import com.chrona.dto.TaskRequest;
import com.chrona.repository.ProjectRepository;
import com.chrona.repository.TaskRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/tasks")
@Validated
public class TasksController {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TasksController(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @PostMapping
    public ResponseEntity<Task> create(@Valid @RequestBody TaskRequest request) {
        Project project = projectRepository.findById(request.getProjectId()).orElse(null);
        if (project == null) {
            return ResponseEntity.badRequest().build();
        }

        Task task = Task.builder()
                .name(request.getName())
                .project(project)
                .status(request.getStatus() != null ? request.getStatus() : "Ativo")
                .billable(request.getBillable() != null ? request.getBillable() : Boolean.TRUE)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        if (request.getParentId() != null) {
            taskRepository.findById(request.getParentId()).ifPresent(task::setParent);
        }
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return taskRepository.findById(id)
                .map(existing -> {
                    projectRepository.findById(request.getProjectId()).ifPresent(existing::setProject);
                    if (request.getParentId() != null) {
                        taskRepository.findById(request.getParentId()).ifPresent(existing::setParent);
                    } else {
                        existing.setParent(null);
                    }
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
