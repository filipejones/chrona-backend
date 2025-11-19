package com.chrona.controller;

import com.chrona.domain.Client;
import com.chrona.dto.ClientRequest;
import com.chrona.repository.ClientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/clients")
@Validated
public class ClientsController {
    private final ClientRepository repository;

    public ClientsController(ClientRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Client> create(@Valid @RequestBody ClientRequest request) {
        Client client = Client.builder()
                .name(request.getName())
                .status(request.getStatus() != null ? request.getStatus() : "Ativo")
                .contactName(request.getContactName())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .address(request.getAddress())
                .notes(request.getNotes())
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
        return ResponseEntity.ok(repository.save(client));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(request.getName());
                    existing.setStatus(request.getStatus() != null ? request.getStatus() : existing.getStatus());
                    existing.setContactName(request.getContactName());
                    existing.setContactEmail(request.getContactEmail());
                    existing.setContactPhone(request.getContactPhone());
                    existing.setAddress(request.getAddress());
                    existing.setNotes(request.getNotes());
                    existing.setUpdatedAt(OffsetDateTime.now());
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
