package com.luxestay.controller;

import com.luxestay.dto.RegistrationDto;
import com.luxestay.model.User;
import com.luxestay.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    // GET USER DETAILS
    // Used for Login verification and getting Balance
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username, Principal principal) {
        // Security Check: Ensure logged-in user matches requested data (or is Admin)
        if (!principal.getName().equals(username) && !principal.getName().equals("admin")) {
            logger.warn("Unauthorized access: {} tried to read {}'s profile", principal.getName(), username);
            return ResponseEntity.status(403).body("{\"message\": \"Unauthorized\"}");
        }

        try {
            User user = userService.findByUsername(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ADD FUNDS
    @PostMapping("/users/{username}/funds")
    public ResponseEntity<?> addFunds(@PathVariable String username, @RequestBody Map<String, BigDecimal> payload, Principal principal) {
        if (!principal.getName().equals(username)) {
            return ResponseEntity.status(403).build();
        }

        BigDecimal amount = payload.get("amount");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body("{\"message\": \"Invalid amount\"}");
        }

        try {
            userService.addFunds(username, amount);
            return ResponseEntity.ok().body("{\"message\": \"Funds added successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("{\"message\": \"Error adding funds\"}");
        }
    }
}