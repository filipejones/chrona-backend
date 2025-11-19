package com.chrona.repository;

import com.chrona.domain.OrganizationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationSettingsRepository extends JpaRepository<OrganizationSettings, Integer> {
}
