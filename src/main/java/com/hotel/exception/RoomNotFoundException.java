package com.hotel.exception;

public class RoomNotFoundException extends Exception {
    public RoomNotFoundException(int roomId) {
        super("Room with ID " + roomId + " was not found.");
    }
}
