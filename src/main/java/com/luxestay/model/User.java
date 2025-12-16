
package com.luxestay.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data // Lombok generates Getters, Setters, ToString, etc.
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // "ADMIN" or "USER"

    // Using BigDecimal for money is standard practice to avoid floating point errors
    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;
}