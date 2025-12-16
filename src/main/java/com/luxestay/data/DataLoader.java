package com.luxestay.data;

import com.luxestay.model.Room;
import com.luxestay.model.User;
import com.luxestay.repository.RoomRepository;
import com.luxestay.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, RoomRepository roomRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        loadUsers();
        loadRooms();
    }

    private void loadUsers() {
        if (userRepository.count() == 0) {
            // Create Admin
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setBalance(new BigDecimal("99999.00")); // Rich admin
            userRepository.save(admin);

            // Create Standard User
            User user = new User();
            user.setUsername("jiten");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("USER");
            user.setBalance(new BigDecimal("500.00")); // Starting balance
            userRepository.save(user);

            System.out.println("✅ Users Loaded: admin/admin123, jiten/password");
        }
    }

    private void loadRooms() {
        if (roomRepository.count() == 0) {
            // These match your frontend MOCK_ROOMS exactly
            List<Room> rooms = List.of(
                    createRoom("101", "100.00", "Standard", "Basic amenities"),
                    createRoom("102", "100.00", "Standard", "Basic amenities"),
                    createRoom("201", "250.00", "Deluxe", "High-end suite"),
                    createRoom("202", "250.00", "Deluxe", "High-end suite"),
                    createRoom("301", "50.00", "Economy", "Budget friendly")
            );

            roomRepository.saveAll(rooms);
            System.out.println("✅ Rooms Loaded: " + rooms.size() + " rooms created.");
        }
    }

    private Room createRoom(String number, String price, String category, String description) {
        Room room = new Room();
        room.setRoomNumber(number);
        room.setPricePerNight(new BigDecimal(price));
        room.setCategoryName(category);
        room.setDescription(description);
        return room;
    }
}