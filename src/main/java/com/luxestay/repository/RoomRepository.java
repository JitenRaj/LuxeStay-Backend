package com.luxestay.repository;

import com.luxestay.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // Custom JPQL Query to find available rooms
    // Logic: Select room R where category matches AND R.id is NOT in the list of booked rooms for these dates
    @Query("SELECT r FROM Room r WHERE r.categoryName = :category " +
            "AND r.id NOT IN (" +
            "  SELECT b.room.id FROM Booking b " +
            "  WHERE b.checkInDate < :checkOut " +
            "  AND b.checkOutDate > :checkIn " + // Overlap condition
            "  AND b.status = 'CONFIRMED'" +      // Only count confirmed bookings
            ")")
    List<Room> findAvailableRooms(
            @Param("category") String category,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );
}