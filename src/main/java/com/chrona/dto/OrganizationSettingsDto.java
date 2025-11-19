package com.chrona.dto;

import com.chrona.domain.OrganizationSettings;

public record OrganizationSettingsDto(Integer id, Integer backdatingDays) {
    public static OrganizationSettingsDto from(OrganizationSettings settings) {
        if (settings == null) {
            return null;
        }
        return new OrganizationSettingsDto(settings.getId(), settings.getBackdatingDays());
    }
}
