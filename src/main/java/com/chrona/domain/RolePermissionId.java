package com.chrona.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolePermissionId {
    @jakarta.persistence.Column(name = "role_id")
    private Long roleId;

    @jakarta.persistence.Column(name = "permission_id")
    private String permissionId;
}
