package com.hotel.model;

import java.util.Arrays;
import java.util.List;

public class Suite extends Room {
    public Suite(int id, int roomNumber, int floor) {
        super(id, roomNumber, floor);
    }

    @Override public double getPricePerNight() { return 300.0; }
    @Override public int getMaxGuests() { return 4; }
    @Override public String getRoomType() { return "Suite"; }

    @Override
    public List<String> getAmenities() {
        return Arrays.asList("WiFi", "TV", "Air Conditioning", "Jacuzzi", "Room Service", "King Bed", "Mini-Bar");
    }
}
