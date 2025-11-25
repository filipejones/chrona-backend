package com.chrona.controller;

import com.chrona.dto.AuthRequest;
import com.chrona.dto.AuthResponse;
import com.chrona.security.JwtService;
import com.chrona.service.TenantService;
import com.chrona.multitenancy.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
    private final JwtService jwtService;
    private final TenantService tenantService;
    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtService jwtService, TenantService tenantService, DataSource dataSource, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.tenantService = tenantService;
        this.dataSource = dataSource;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        // Sempre resolve o tenant pelo e-mail para evitar cair no schema errado
        String effectiveTenant = tenantService.resolveTenantByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant não encontrado para este e-mail"));
        TenantContext.setCurrentTenant(effectiveTenant);

        try {
            String schema = "tenant_" + effectiveTenant;
            String hash = null;
            try (Connection conn = dataSource.getConnection()) {
                conn.setSchema(schema);
                try (PreparedStatement ps = conn.prepareStatement("select password_hash from users where email = ? limit 1")) {
                    ps.setString(1, request.getEmail());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            hash = rs.getString(1);
                        }
                    }
                }
            } catch (java.sql.SQLException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao autenticar", e);
            }

            if (hash == null || !passwordEncoder.matches(request.getPassword(), hash)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
            }

            String token = jwtService.generateToken(request.getEmail(), effectiveTenant);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } finally {
            TenantContext.clear();
        }
    }
}
