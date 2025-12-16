package com.luxestay.service;

import com.luxestay.dto.BookingRequestDto;
import com.luxestay.model.Booking;
import com.luxestay.model.Room;
import com.luxestay.model.User;
import com.luxestay.repository.BookingRepository;
import com.luxestay.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);

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
        logger.info("Attempting to create booking for user: {} for room ID: {}", username, request.getRoomId());

        User user = userService.findByUsername(username);
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> {
                    logger.error("Booking failed. Room not found with ID: {}", request.getRoomId());
                    return new RuntimeException("Room not found");
                });

        // 1. Calculate Cost
        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        if (nights < 1) {
            logger.warn("Booking failed. Invalid duration: {} nights", nights);
            throw new RuntimeException("Booking must be for at least 1 night");
        }

        BigDecimal totalCost = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        logger.debug("Calculated total cost: {} for {} nights", totalCost, nights);

        // 2. Validate Balance & Deduct
        try {
            userService.deductBalance(user, totalCost);
        } catch (RuntimeException e) {
            logger.error("Booking failed for user: {}. Reason: Insufficient funds.", username);
            throw e;
        }

        // 3. Save Booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(request.getCheckIn());
        booking.setCheckOutDate(request.getCheckOut());
        booking.setTotalPrice(totalCost);
        booking.setStatus("CONFIRMED");

        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking created successfully. Booking ID: {}", savedBooking.getId());
        return savedBooking;
    }

    public List<Booking> getUserBookings(String username) {
        logger.debug("Fetching all bookings for user: {}", username);
        return bookingRepository.findByUserUsername(username);
    }

    // Recent Bookings
    // Filters the user's bookings to return the latest 5 based on booking ID (most recently created)
    public List<Booking> getRecentBookings(String username) {
        logger.debug("Fetching recent bookings for user: {}", username);
        List<Booking> allBookings = bookingRepository.findByUserUsername(username);

        return allBookings.stream()
                .sorted(Comparator.comparing(Booking::getId).reversed()) // Newest ID first
                .limit(5)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        logger.info("Attempting to cancel booking ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.error("Cancellation failed. Booking ID {} not found", bookingId);
                    return new RuntimeException("Booking not found");
                });

        if ("CANCELLED".equals(booking.getStatus())) {
            logger.warn("Booking ID {} is already cancelled", bookingId);
            throw new RuntimeException("Booking is already cancelled");
        }

        // Refund the user
        logger.info("Refunding user: {} amount: {}", booking.getUser().getUsername(), booking.getTotalPrice());
        userService.refundBalance(booking.getUser(), booking.getTotalPrice());

        // Update status
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
        logger.info("Booking ID {} cancelled successfully", bookingId);
    }
}