package com.luxestay.controller;

import com.luxestay.dto.BookingRequestDto;
import com.luxestay.model.Booking;
import com.luxestay.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDto request, Principal principal) {
        logger.info("Received booking request from user: {}", principal.getName());
        try {
            Booking booking = bookingService.createBooking(principal.getName(), request);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            logger.error("Error processing booking for user {}: {}", principal.getName(), e.getMessage());
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable String username, Principal principal) {
        if (!principal.getName().equals(username) && !username.equals("admin")) {
            logger.warn("Unauthorized access attempt. User {} tried to view bookings of {}", principal.getName(), username);
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(bookingService.getUserBookings(username));
    }

    // NEW ENDPOINT: Recent Bookings
    @GetMapping("/recent")
    public ResponseEntity<List<Booking>> getRecentBookings(Principal principal) {
        // Automatically uses the logged-in user's name
        String username = principal.getName();
        logger.info("Fetching recent bookings for user: {}", username);
        return ResponseEntity.ok(bookingService.getRecentBookings(username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id, Principal principal) {
        logger.info("User {} requested cancellation for booking ID: {}", principal.getName(), id);
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok().body("{\"message\": \"Cancelled\"}");
        } catch (Exception e) {
            logger.error("Error cancelling booking {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}