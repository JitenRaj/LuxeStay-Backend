package com.luxestay.service;

import com.luxestay.dto.RoomDto;
import com.luxestay.model.Room;
import com.luxestay.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<RoomDto> findAvailableRooms(String category, LocalDate checkIn, LocalDate checkOut) {
        logger.debug("Searching available rooms. Category: {}, CheckIn: {}, CheckOut: {}", category, checkIn, checkOut);
        List<Room> rooms = roomRepository.findAvailableRooms(category, checkIn, checkOut);
        logger.info("Found {} available rooms for category: {}", rooms.size(), category);
        return rooms.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<RoomDto> getAllRooms() {
        logger.debug("Fetching all rooms for admin");
        return roomRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public Room addRoom(Room room) {
        logger.info("Adding new room: Number {}, Price {}", room.getRoomNumber(), room.getPricePerNight());
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        logger.info("Deleting room with ID: {}", id);
        roomRepository.deleteById(id);
    }

    private RoomDto mapToDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setPricePerNight(room.getPricePerNight());
        dto.setCategory(new RoomDto.CategoryDto(room.getCategoryName(), room.getDescription()));
        return dto;
    }
}