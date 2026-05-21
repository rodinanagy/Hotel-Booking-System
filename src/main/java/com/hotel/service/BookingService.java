package com.hotel.service;

import com.hotel.exception.*;
import com.hotel.model.*;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public BookingService() {
        this.bookingRepository = new BookingRepository();
        this.roomRepository = new RoomRepository();
    }

    BookingService(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    public Booking createBooking(Room room, Guest guest, LocalDate checkIn, LocalDate checkOut, int guestCount)
            throws RoomNotAvailableException, InvalidDateException, InvalidGuestCountException, SQLException {

        validateDates(checkIn, checkOut);

        if (guestCount > room.getMaxGuests()) {
            throw new InvalidGuestCountException(guestCount, room.getMaxGuests(), room.getRoomType());
        }

        if (!bookingRepository.isRoomAvailable(room.getId(), checkIn, checkOut)) {
            throw new RoomNotAvailableException(room.getRoomNumber(), checkIn.toString(), checkOut.toString());
        }

        return bookingRepository.save(room, guest, checkIn, checkOut);
    }

    public void cancelBooking(int bookingId) throws SQLException {
        bookingRepository.cancelBooking(bookingId);
    }

    public List<Booking> getAllBookings() throws SQLException, RoomNotFoundException {
        return bookingRepository.findAll();
    }

    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut)
            throws SQLException, InvalidDateException {
        validateDates(checkIn, checkOut);
        List<Room> all = roomRepository.findAll();
        List<Room> available = new ArrayList<>();
        for (Room room : all) {
            if (bookingRepository.isRoomAvailable(room.getId(), checkIn, checkOut)) {
                available.add(room);
            }
        }
        return available;
    }

    public void validateDates(LocalDate checkIn, LocalDate checkOut) throws InvalidDateException {
        if (checkIn == null || checkOut == null) {
            throw new InvalidDateException("Check-in and check-out dates are required.");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new InvalidDateException("Check-out date must be after check-in date.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new InvalidDateException("Check-in date cannot be in the past.");
        }
    }
}
