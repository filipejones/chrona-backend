package com.chrona.dto;

import lombok.Data;

@Data
public class TenantRegistrationDto {
    private String organizationName;
    private String adminName;
    private String adminEmail;
    private String password;
}
