package com.chrona.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;

@Getter
public class OrganizationSettingsRequest {
    @Min(0)
    private Integer backdatingDays;
}
