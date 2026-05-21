package com.hotel.exception;

public class InvalidGuestCountException extends Exception {
    public InvalidGuestCountException(int requested, int max, String roomType) {
        super("Cannot book " + requested + " guests in a " + roomType + " room (max: " + max + ").");
    }
}
