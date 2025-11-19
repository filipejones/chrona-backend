package com.chrona.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "organization_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationSettings {
    @Id
    private Integer id = 1;

    @Column(name = "backdating_days", nullable = false)
    private Integer backdatingDays = 7;
}
