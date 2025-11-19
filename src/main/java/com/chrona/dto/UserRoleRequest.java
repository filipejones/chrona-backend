package com.chrona.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserRoleRequest {
    @NotNull
    private Long roleId;
}
