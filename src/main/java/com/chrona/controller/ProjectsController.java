package com.chrona.controller;

import com.chrona.domain.Client;
import com.chrona.domain.Project;
import com.chrona.domain.Task;
import com.chrona.dto.ProjectRequest;
import com.chrona.dto.ProjectOverview;
import com.chrona.service.ProjectOverviewService;
import com.chrona.repository.ClientRepository;
import com.chrona.repository.ProjectRepository;
import com.chrona.repository.TaskRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@Validated
public class ProjectsController {
    private static final Logger log = LoggerFactory.getLogger(ProjectsController.class);
    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final TaskRepository taskRepository;
    private final ProjectOverviewService projectOverviewService;

    public ProjectsController(ProjectRepository projectRepository, ClientRepository clientRepository, TaskRepository taskRepository, ProjectOverviewService projectOverviewService) {
        this.projectRepository = projectRepository;
        this.clientRepository = clientRepository;
        this.taskRepository = taskRepository;
        this.projectOverviewService = projectOverviewService;
    }

    @PostMapping
    public ResponseEntity<Project> create(@Valid @RequestBody ProjectRequest request) {
        Client client = clientRepository.findById(request.getClientId()).orElse(null);
        if (client == null) {
            return ResponseEntity.badRequest().build();
        }

        Project project = Project.builder()
                .name(request.getName())
                .client(client)
                .status(request.getStatus() != null ? request.getStatus() : "Ativo")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        return ResponseEntity.ok(projectRepository.save(project));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return projectRepository.findById(id)
                .map(existing -> {
                    existing.setName(request.getName());
                    existing.setStatus(request.getStatus() != null ? request.getStatus() : existing.getStatus());
                    clientRepository.findById(request.getClientId())
                            .ifPresent(existing::setClient);
                    existing.setUpdatedAt(OffsetDateTime.now());
                    return ResponseEntity.ok(projectRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity<List<Task>> listTasksByProject(@PathVariable Long id,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return projectRepository.findById(id)
                .map(project -> ResponseEntity.ok(taskRepository.findByProjectId(project.getId(), pageable)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/overview")
    public ResponseEntity<ProjectOverview> getOverview(@PathVariable Long id) {
        try {
            ProjectOverview overview = projectOverviewService.getOverview(id);
            if (overview == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(overview);
        } catch (Exception ex) {
            log.error("Failed to build project overview for id={}", id, ex);
            return ResponseEntity.status(500).build();
        }
    }
}
