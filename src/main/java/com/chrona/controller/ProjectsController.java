package com.chrona.controller;

import com.chrona.domain.Client;
import com.chrona.domain.Project;
import com.chrona.dto.ProjectRequest;
import com.chrona.repository.ClientRepository;
import com.chrona.repository.ProjectRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/projects")
@Validated
public class ProjectsController {
    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;

    public ProjectsController(ProjectRepository projectRepository, ClientRepository clientRepository) {
        this.projectRepository = projectRepository;
        this.clientRepository = clientRepository;
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
}
