package com.chrona.repository;

import com.chrona.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    @Query("select distinct r from Role r left join fetch r.permissions")
    List<Role> findAllWithPermissions();
}
