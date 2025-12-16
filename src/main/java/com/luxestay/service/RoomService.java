package com.luxestay.service;

import com.luxestay.dto.RoomDto;
import com.luxestay.model.Room;
import com.luxestay.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<RoomDto> findAvailableRooms(String category, LocalDate checkIn, LocalDate checkOut) {
        List<Room> rooms = roomRepository.findAvailableRooms(category, checkIn, checkOut);
        return rooms.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public List<RoomDto> getAllRooms() { // For Admin Panel
        return roomRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public Room addRoom(Room room) {
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    // Mapper utility
    private RoomDto mapToDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setPricePerNight(room.getPricePerNight());
        // Reconstruct the nested object structure the frontend expects
        dto.setCategory(new RoomDto.CategoryDto(room.getCategoryName(), room.getDescription()));
        return dto;
    }
}