package com.luxestay.controller;

import com.luxestay.model.Room;
import com.luxestay.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final RoomService roomService;

    public AdminController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Endpoint: POST /api/admin/rooms
    @PostMapping("/rooms")
    @PreAuthorize("hasRole('ADMIN')") // Double security check
    public ResponseEntity<Room> addRoom(@RequestBody Room room) {
        return ResponseEntity.ok(roomService.addRoom(room));
    }

    // Endpoint: DELETE /api/admin/rooms/{id}
    @DeleteMapping("/rooms/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok().body("{\"message\": \"Deleted\"}");
    }
}