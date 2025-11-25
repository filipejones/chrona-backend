package com.chrona.dto;

import com.chrona.domain.User;

public record UserDto(Long id, String name, String email, String roleName, Long roleId) {
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().getName() : null,
                user.getRole() != null ? user.getRole().getId() : null
        );
    }
}
