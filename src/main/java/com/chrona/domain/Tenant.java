package com.chrona.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tenants", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    @Id
    private String id;

    private String name;

    @Column(name = "schema_name")
    private String schemaName;
}
