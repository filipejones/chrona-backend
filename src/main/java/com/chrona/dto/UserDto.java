package com.chrona.dto;

import com.chrona.domain.User;

public record UserDto(Long id, String name, String email, String roleName) {
    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().getName() : null
        );
    }
}
