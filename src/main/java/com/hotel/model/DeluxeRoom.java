package com.hotel.model;

import java.util.Arrays;
import java.util.List;

public class DeluxeRoom extends Room {
    public DeluxeRoom(int id, int roomNumber, int floor) {
        super(id, roomNumber, floor);
    }

    @Override public double getPricePerNight() { return 180.0; }
    @Override public int getMaxGuests() { return 3; }
    @Override public String getRoomType() { return "Deluxe"; }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("WiFi", "TV", "Air Conditioning", "Balcony", "Mini-Bar");
    }
}
