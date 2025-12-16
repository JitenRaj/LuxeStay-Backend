package com.luxestay.service;

import com.luxestay.dto.BookingRequestDto;
import com.luxestay.model.Booking;
import com.luxestay.model.Room;
import com.luxestay.model.User;
import com.luxestay.repository.BookingRepository;
import com.luxestay.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserService userService;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository, UserService userService) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userService = userService;
    }

    @Transactional
    public Booking createBooking(String username, BookingRequestDto request) {
        User user = userService.findByUsername(username);
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // 1. Calculate Cost
        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        if (nights < 1) throw new RuntimeException("Booking must be for at least 1 night");

        BigDecimal totalCost = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        // 2. Validate Balance & Deduct
        userService.deductBalance(user, totalCost);

        // 3. Save Booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckIn());
        booking.setCheckOutDate(request.getCheckOut());
        booking.setTotalPrice(totalCost);
        booking.setStatus("CONFIRMED");

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(String username) {
        return bookingRepository.findByUserUsername(username);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is already cancelled");
        }

        // Refund the user
        userService.refundBalance(booking.getUser(), booking.getTotalPrice());

        // Update status (Soft delete logic)
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        // Alternatively: bookingRepository.delete(booking) if you want hard delete
    }
}