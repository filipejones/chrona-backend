package com.chrona.multitenancy;

import com.chrona.security.JwtService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Set;

public class TenantFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/auth/login",
            "/api/v1/bootstrap",
            "/api/public"
    );

    private final JwtService jwtService;

    public TenantFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String authHeader = req.getHeader("Authorization");
        String tenantFromToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.validateToken(token)) {
                tenantFromToken = jwtService.extractTenant(token);
            }
        }

        String tenantHeader = req.getHeader("X-Tenant-ID");
        String tenantId = tenantFromToken != null ? tenantFromToken : tenantHeader;

        System.out.println("TenantFilter: " + req.getMethod() + " " + req.getRequestURI() + " | Tenant ID: " + tenantId);

        boolean isApiPath = req.getRequestURI().startsWith("/api");
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path -> req.getRequestURI().startsWith(path));

        if (isApiPath && !"OPTIONS".equalsIgnoreCase(req.getMethod()) && !isPublic && tenantId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("X-Tenant-ID header is required.");
            return;
        }

        if (tenantId != null) {
            TenantContext.setCurrentTenant(tenantId);
        }

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
