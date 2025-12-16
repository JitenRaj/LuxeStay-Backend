package com.luxestay.repository;

import com.luxestay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA automatically generates the SQL for this method name
    Optional<User> findByUsername(String username);

    // Check if user exists before registering
    boolean existsByUsername(String username);
}
