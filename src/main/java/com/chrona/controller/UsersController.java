package com.chrona.controller;

import com.chrona.domain.User;
import com.chrona.dto.UserRoleRequest;
import com.chrona.repository.RoleRepository;
import com.chrona.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UsersController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UsersController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<User> changeRole(@PathVariable Long userId,
                                           @Valid @RequestBody UserRoleRequest request) {
        return userRepository.findById(userId)
                .map(user -> roleRepository.findById(request.getRoleId())
                        .map(role -> {
                            user.setRole(role);
                            return ResponseEntity.ok(userRepository.save(user));
                        })
                        .orElse(ResponseEntity.badRequest().build()))
                .orElse(ResponseEntity.notFound().build());
    }
}
