package com.chrona.controller;

import com.chrona.dto.TenantDto;
import com.chrona.dto.TenantRegistrationDto;
import com.chrona.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/tenants")
@RequiredArgsConstructor
public class TenantRegistrationController {

    private final TenantService tenantService;

    @PostMapping("/register")
    public ResponseEntity<TenantDto> registerTenant(@RequestBody TenantRegistrationDto registrationDto) {
        var tenant = tenantService.registerTenant(registrationDto);
        return ResponseEntity.ok(TenantDto.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .build());
    }
}
