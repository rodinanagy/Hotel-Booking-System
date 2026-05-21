package com.hotel.exception;

public class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException(int roomNumber, String checkIn, String checkOut) {
        super("Room " + roomNumber + " is not available from " + checkIn + " to " + checkOut + ".");
    }
}
