package com.luxestay.controller;

import com.luxestay.dto.BookingRequestDto;
import com.luxestay.model.Booking;
import com.luxestay.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Endpoint: POST /api/bookings
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDto request, Principal principal) {
        try {
            // principal.getName() gets the username from the Basic Auth token automatically
            Booking booking = bookingService.createBooking(principal.getName(), request);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"message\": \"" + e.getMessage() + "\"}");
        }
    }

    // Endpoint: GET /api/bookings/user/{username} (Matches your api.js)
    @GetMapping("/user/{username}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable String username, Principal principal) {
        // Security check: Ensure logged-in user can only see their own bookings
        if (!principal.getName().equals(username) && !username.equals("admin")) { // simplistic admin check
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(bookingService.getUserBookings(username));
    }

    // Endpoint: DELETE /api/bookings/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.ok().body("{\"message\": \"Cancelled\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}