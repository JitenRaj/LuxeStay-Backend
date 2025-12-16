package com.luxestay.controller;

import com.luxestay.dto.RegistrationDto;
import com.luxestay.dto.RoomDto;
import com.luxestay.service.RoomService;
import com.luxestay.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private static final Logger logger = LoggerFactory.getLogger(PublicController.class);

    private final RoomService roomService;
    private final UserService userService;

    public PublicController(RoomService roomService, UserService userService) {
        this.roomService = roomService;
        this.userService = userService;
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDto>> searchRooms(
            @RequestParam String category,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        logger.info("Public search request. Category: {}, Dates: {} to {}", category, checkIn, checkOut);
        List<RoomDto> rooms = roomService.findAvailableRooms(category, checkIn, checkOut);
        return ResponseEntity.ok(rooms);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationDto registrationDto) {
        logger.info("Public registration request for username: {}", registrationDto.getUsername());
        try {
            userService.registerUser(registrationDto);
            return ResponseEntity.ok().body("{\"message\": \"User registered successfully\"}");
        } catch (RuntimeException e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }
}