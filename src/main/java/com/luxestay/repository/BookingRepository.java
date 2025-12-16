package com.luxestay.repository;

import com.luxestay.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Fetch all bookings for a specific user
    List<Booking> findByUserUsername(String username);
}