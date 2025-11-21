package com.chrona.controller;

import com.chrona.domain.Expense;
import com.chrona.domain.Project;
import com.chrona.domain.User;
import com.chrona.repository.ExpenseRepository;
import com.chrona.repository.ProjectRepository;
import com.chrona.repository.UserRepository;
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

    public ExpensesController(ExpenseRepository expenseRepository, ProjectRepository projectRepository,
            UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Expense> list(@RequestParam(required = false) Long projectId) {
        if (projectId != null) {
            return expenseRepository.findByProjectId(projectId);
        }
        return expenseRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Expense> create(@RequestBody Expense expense) {
        if (expense.getProject() == null || expense.getProject().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Project project = projectRepository.findById(expense.getProject().getId()).orElse(null);
        if (project == null) {
            return ResponseEntity.badRequest().build();
        }
        expense.setProject(project);

        if (expense.getUser() != null && expense.getUser().getId() != null) {
            User user = userRepository.findById(expense.getUser().getId()).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().build();
            }
            expense.setUser(user);
        } else {
            // Should probably get from SecurityContext, but for now require ID
            return ResponseEntity.badRequest().build();
        }

        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());
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
