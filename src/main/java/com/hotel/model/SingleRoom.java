package com.hotel.model;

import java.util.Arrays;
import java.util.List;

public class SingleRoom extends Room {
    public SingleRoom(int id, int roomNumber, int floor) {
        super(id, roomNumber, floor);
    }

    @Override public double getPricePerNight() { return 80.0; }
    @Override public int getMaxGuests() { return 1; }
    @Override public String getRoomType() { return "Single"; }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("WiFi", "TV", "Air Conditioning");
    }
}
