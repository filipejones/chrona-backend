package com.chrona.service;

import com.chrona.domain.Tenant;
import com.chrona.repository.TenantRepository;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final DataSource dataSource;

    public TenantService(TenantRepository tenantRepository, DataSource dataSource) {
        this.tenantRepository = tenantRepository;
        this.dataSource = dataSource;
    }

    public Tenant createTenant(String tenantId, String name) {
        String schemaName = "tenant_" + tenantId;

        // Create schema
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .defaultSchema(schemaName)
                .locations("classpath:db/migration/tenants")
                .load();

        flyway.migrate();

        // Save tenant metadata
        Tenant tenant = Tenant.builder()
                .id(tenantId)
                .name(name)
                .schemaName(schemaName)
                .build();

        return tenantRepository.save(tenant);
    }

    public java.util.Optional<Tenant> findById(String id) {
        return tenantRepository.findById(id);
    }

    public java.util.List<Tenant> findAll() {
        return tenantRepository.findAll();
    }
}
