package com.chrona.controller;

import com.chrona.domain.Expense;
import com.chrona.domain.Project;
import com.chrona.domain.User;
import com.chrona.domain.Phase;
import com.chrona.dto.ExpenseRequest;
import com.chrona.repository.ExpenseRepository;
import com.chrona.repository.ProjectRepository;
import com.chrona.repository.UserRepository;
import com.chrona.repository.PhaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@Validated
public class ExpensesController {

    private final ExpenseRepository expenseRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final PhaseRepository phaseRepository;

    public ExpensesController(ExpenseRepository expenseRepository, ProjectRepository projectRepository,
            UserRepository userRepository, PhaseRepository phaseRepository) {
        this.expenseRepository = expenseRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.phaseRepository = phaseRepository;
    }

    @GetMapping
    public List<Expense> list(@RequestParam(required = false) Long projectId,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        if (projectId != null) {
            return expenseRepository.findByProjectId(projectId, pageable);
        }
        return expenseRepository.findAll(pageable).getContent();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ExpenseRequest request) {
        Project project = projectRepository.findById(request.getProjectId()).orElse(null);
        if (project == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Projeto não encontrado.");
        }

        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não encontrado.");
        }

        Phase phase = null;
        if (request.getPhaseId() != null) {
            phase = phaseRepository.findById(request.getPhaseId()).orElse(null);
            if (phase == null || phase.getProject() == null || !phase.getProject().getId().equals(project.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Etapa inválida para o projeto informado.");
            }
        }

        Expense expense = Expense.builder()
                .amount(request.getAmount())
                .date(request.getDate())
                .description(request.getDescription())
                .reimbursable(request.isReimbursable())
                .project(project)
                .phase(phase)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(expenseRepository.save(expense));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (expenseRepository.existsById(id)) {
            expenseRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
