package com.luxestay.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RoomDto {
    private Long id;
    private String roomNumber;
    private BigDecimal pricePerNight;
    private CategoryDto category; // Nested object for Frontend compatibility

    @Data
    public static class CategoryDto {
        private String name;
        private String description;

        public CategoryDto(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }
}