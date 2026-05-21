package com.hotel.model;

import java.util.List;

public abstract class Room {
    private final int id;
    private final int roomNumber;
    private final int floor;

    protected Room(int id, int roomNumber, int floor) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.floor = floor;
    }

    public abstract double getPricePerNight();
    public abstract int getMaxGuests();
    public abstract String getRoomType();
    public abstract List<String> getAmenities();

    public double calculateTotalCost(int nights) {
        return getPricePerNight() * nights;
    }

    public int getId() { return id; }
    public int getRoomNumber() { return roomNumber; }
    public int getFloor() { return floor; }

    @Override
    public String toString() {
        return getRoomType() + " #" + roomNumber;
    }
}
