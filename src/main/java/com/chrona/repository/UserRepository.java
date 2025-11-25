package com.chrona.repository;

import com.chrona.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.permissions WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role")
    List<User> findAllWithRoles();
}
