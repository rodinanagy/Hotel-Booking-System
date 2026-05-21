package com.hotel.model;

import java.util.Arrays;
import java.util.List;

public class DoubleRoom extends Room {
    public DoubleRoom(int id, int roomNumber, int floor) {
        super(id, roomNumber, floor);
    }

    @Override public double getPricePerNight() { return 120.0; }
    @Override public int getMaxGuests() { return 2; }
    @Override public String getRoomType() { return "Double"; }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("WiFi", "TV", "Air Conditioning", "Mini-Fridge");
    }
}
