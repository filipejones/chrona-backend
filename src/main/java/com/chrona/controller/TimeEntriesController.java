package com.chrona.controller;

import com.chrona.domain.Project;
import com.chrona.domain.Task;
import com.chrona.domain.TimeEntry;
import com.chrona.domain.User;
import com.chrona.dto.TimeEntryRequest;
import com.chrona.dto.TimeEntryUpdateRequest;
import com.chrona.repository.ProjectRepository;
import com.chrona.repository.TaskRepository;
import com.chrona.repository.TimeEntryRepository;
import com.chrona.repository.UserRepository;
import com.chrona.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@RestController
// Legacy endpoints kept under /api/v1/legacy/time-entries to avoid clashing with the new time entry controller
@RequestMapping("/api/v1/legacy/time-entries")
@Validated
public class TimeEntriesController {
    private final TimeEntryRepository repository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public TimeEntriesController(TimeEntryRepository repository,
                                 UserRepository userRepository,
                                 ProjectRepository projectRepository,
                                 TaskRepository taskRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @PostMapping
    public ResponseEntity<TimeEntry> create(@Valid @RequestBody TimeEntryRequest request) {
        User user = findCurrentUser();
        Project project = projectRepository.findById(request.getProjectId()).orElse(null);
        Task task = taskRepository.findById(request.getTaskId()).orElse(null);
        if (user == null || project == null || task == null) {
            return ResponseEntity.<TimeEntry>badRequest().build();
        }
        TimeEntry entry = TimeEntry.builder()
                .user(user)
                .project(project)
                .task(task)
                .workDate(request.getWorkDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .description(request.getDescription())
                .notes(request.getNotes())
                .tags(request.getTags())
                .status("DRAFT")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        return ResponseEntity.status(201).body(repository.save(entry));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable Long id,
                                            @Valid @RequestBody TimeEntryUpdateRequest request) {
        return repository.findById(id)
                .map(existing -> {
                    Project project = projectRepository.findById(request.getProjectId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Projeto inválido"));
                    Task task = taskRepository.findById(request.getTaskId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tarefa inválida"));
                    existing.setProject(project);
                    existing.setTask(task);
                    existing.setWorkDate(request.getWorkDate());
                    existing.setStartTime(request.getStartTime());
                    existing.setEndTime(request.getEndTime());
                    existing.setDescription(request.getDescription());
                    existing.setNotes(request.getNotes());
                    existing.setTags(request.getTags());
                    if (request.getStatus() != null) {
                        existing.setStatus(request.getStatus());
                    }
                    existing.setUpdatedAt(OffsetDateTime.now());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lançamento não encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private User findCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email == null) {
            return null;
        }
        return userRepository.findByEmail(email).orElse(null);
    }
}
