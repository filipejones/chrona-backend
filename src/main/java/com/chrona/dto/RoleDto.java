package com.chrona.dto;

import com.chrona.domain.Role;
import com.chrona.domain.RolePermission;

import java.util.stream.Collectors;

public record RoleDto(Long id, String name, java.util.Set<String> permissions) {
    public static RoleDto from(Role role) {
        java.util.Set<String> perms = role.getPermissions().stream()
                .map(RolePermission::getId)
                .map(id -> id != null ? id.getPermissionId() : null)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        return new RoleDto(role.getId(), role.getName(), perms);
    }
}
