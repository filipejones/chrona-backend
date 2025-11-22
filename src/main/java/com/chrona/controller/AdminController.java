package com.chrona.controller;

import com.chrona.domain.Tenant;
import com.chrona.dto.TenantDto;
import com.chrona.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/tenants")
@RequiredArgsConstructor
public class AdminController {

    private final TenantService tenantService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TenantDto>> getAllTenants() {
        List<TenantDto> tenants = tenantService.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tenants);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantDto> createTenant(@RequestBody TenantDto tenantDto) {
        Tenant tenant = tenantService.createTenant(tenantDto.getId(), tenantDto.getName());
        return ResponseEntity.ok(toDto(tenant));
    }

    @PostMapping("/{id}/impersonate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantDto> impersonate(@PathVariable String id) {
        return tenantService.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private TenantDto toDto(Tenant tenant) {
        return TenantDto.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .build();
    }
}
