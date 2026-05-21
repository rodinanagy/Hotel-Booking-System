package com.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking {
    private final int id;
    private final Room room;
    private final Guest guest;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private double totalCost;
    private String status;

    public Booking(int id, Room room, Guest guest, LocalDate checkIn, LocalDate checkOut) {
        this.id = id;
        this.room = room;
        this.guest = guest;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalCost = room.calculateTotalCost((int) ChronoUnit.DAYS.between(checkIn, checkOut));
        this.status = "ACTIVE";
    }

    public int getId() { return id; }
    public Room getRoom() { return room; }
    public Guest getGuest() { return guest; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getTotalCost() { return totalCost; }
    public String getStatus() { return status; }
    public long getNights() { return ChronoUnit.DAYS.between(checkIn, checkOut); }

    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public void setStatus(String status) { this.status = status; }
}
