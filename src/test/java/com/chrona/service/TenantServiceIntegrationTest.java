package com.chrona.service;

import com.chrona.domain.Tenant;
import com.chrona.repository.TenantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TenantServiceIntegrationTest {

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private DataSource dataSource;

    @Test
    public void testCreateTenant() throws Exception {
        String tenantId = "test_tenant_1";
        String tenantName = "Test Tenant 1";

        // Create tenant
        Tenant createdTenant = tenantService.createTenant(tenantId, tenantName);

        // Verify tenant entity
        assertNotNull(createdTenant);
        assertEquals(tenantId, createdTenant.getId());
        assertEquals("tenant_" + tenantId, createdTenant.getSchemaName());

        // Verify schema creation
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {

            // Check if schema exists
            ResultSet resultSet = statement.executeQuery(
                    "SELECT schema_name FROM information_schema.schemata WHERE schema_name = 'tenant_" + tenantId
                            + "'");
            assertTrue(resultSet.next(), "Schema should exist");

            // Check if a table exists in the new schema (e.g., users)
            resultSet = statement.executeQuery(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = 'tenant_" + tenantId
                            + "' AND table_name = 'users'");
            assertTrue(resultSet.next(), "Users table should exist in the new schema");
        }
    }
}
