package com.chrona.controller;

import com.chrona.domain.Role;
import com.chrona.dto.RoleRequest;
import com.chrona.repository.RoleRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@Validated
public class RolesController {
    private final RoleRepository repository;

    public RolesController(RoleRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Role> create(@Valid @RequestBody RoleRequest request) {
        Role role = Role.builder().name(request.getName()).build();
        return ResponseEntity.ok(repository.save(role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> update(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(request.getName());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
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
}
