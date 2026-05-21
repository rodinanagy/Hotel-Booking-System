package com.hotel.service;

import com.hotel.model.Room;
import javafx.concurrent.Task;

import java.time.LocalDate;
import java.util.List;

public class RoomAvailabilityTask extends Task<List<Room>> {
    private final BookingService bookingService;
    private final LocalDate checkIn;
    private final LocalDate checkOut;

    public RoomAvailabilityTask(BookingService bookingService, LocalDate checkIn, LocalDate checkOut) {
        this.bookingService = bookingService;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    @Override
    protected List<Room> call() throws Exception {
        updateMessage("Checking room availability...");
        return bookingService.getAvailableRooms(checkIn, checkOut);
    }
}
