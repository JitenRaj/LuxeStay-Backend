package com.luxestay.service;

import com.luxestay.dto.RegistrationDto;
import com.luxestay.model.User;
import com.luxestay.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegistrationDto request) {
        logger.info("Attempting to register new user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Registration failed. Username '{}' already exists", request.getUsername());
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setBalance(BigDecimal.valueOf(0));

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully. ID: {}", savedUser.getId());
        return savedUser;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found: {}", username);
                    return new RuntimeException("User not found");
                });
    }

    @Transactional
    public void addFunds(String username, BigDecimal amount) {
        User user = findByUsername(username);
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
        logger.info("Funds added for user: {}. Amount: {}. New Balance: {}", username, amount, user.getBalance());
    }

    @Transactional
    public void deductBalance(User user, BigDecimal amount) {
        logger.debug("Deducting balance for user: {}. Current: {}, Amount: {}", user.getUsername(), user.getBalance(), amount);

        if (user.getBalance().compareTo(amount) < 0) {
            logger.warn("Insufficient funds for user: {}. Required: {}, Available: {}", user.getUsername(), amount, user.getBalance());
            throw new RuntimeException("Insufficient funds");
        }
        user.setBalance(user.getBalance().subtract(amount));
        userRepository.save(user);
        logger.info("Balance deducted. New Balance: {}", user.getBalance());
    }

    @Transactional
    public void refundBalance(User user, BigDecimal amount) {
        logger.debug("Refunding balance for user: {}. Amount: {}", user.getUsername(), amount);
        user.setBalance(user.getBalance().add(amount));
        userRepository.save(user);
        logger.info("Balance refunded. New Balance: {}", user.getBalance());
    }
}