package com.chrona.multitenancy;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null) {
            return "tenant_" + tenantId;
        }
        return "public"; // Default tenant
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
