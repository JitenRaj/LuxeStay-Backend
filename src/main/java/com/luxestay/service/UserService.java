package com.luxestay.service;

import com.luxestay.dto.RegistrationDto;
import com.luxestay.model.User;
import com.luxestay.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegistrationDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        // Hash the password before saving!
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setBalance(BigDecimal.valueOf(0)); // Default balance

        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Helper to verify funds logic is handled cleanly
    @Transactional
    public void deductBalance(User user, BigDecimal amount) {
        if (user.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);
    }

    @Transactional
    public void refundBalance(User user, BigDecimal amount) {
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
    }
}