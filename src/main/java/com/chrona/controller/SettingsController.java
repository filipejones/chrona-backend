package com.chrona.controller;

import com.chrona.domain.OrganizationSettings;
import com.chrona.dto.OrganizationSettingsRequest;
import com.chrona.repository.OrganizationSettingsRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/settings")
@Validated
public class SettingsController {
    private final OrganizationSettingsRepository repository;

    public SettingsController(OrganizationSettingsRepository repository) {
        this.repository = repository;
    }

    @PutMapping
    public ResponseEntity<OrganizationSettings> update(@Valid @RequestBody OrganizationSettingsRequest request) {
        OrganizationSettings settings = repository.findById(1).orElseGet(() ->
                OrganizationSettings.builder().id(1).build());
        if (request.getBackdatingDays() != null) {
            settings.setBackdatingDays(request.getBackdatingDays());
        }
        return ResponseEntity.ok(repository.save(settings));
    }
}
