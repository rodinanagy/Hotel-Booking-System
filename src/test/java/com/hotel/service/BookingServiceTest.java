package com.hotel.service;

import com.hotel.exception.*;
import com.hotel.model.*;
import com.hotel.repository.DatabaseManager;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTest {
    private BookingService service;

    @BeforeEach
    void setUp() {
        DatabaseManager.configure("jdbc:sqlite:test_hotel.db");
        service = new BookingService();
    }

    @AfterEach
    void tearDown() {
        new File("test_hotel.db").delete();
    }

    @Test
    void createBooking_validInput_succeeds() throws Exception {
        List<Room> rooms = service.getAvailableRooms(
            LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        assertFalse(rooms.isEmpty());

        Room room = rooms.get(0);
        Guest guest = new Guest(0, "Alice Smith", "alice@test.com", "0501234567");
        Booking booking = service.createBooking(room, guest,
            LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), 1);

        assertTrue(booking.getId() > 0);
        assertEquals("ACTIVE", booking.getStatus());
        assertEquals(room.getPricePerNight() * 2, booking.getTotalCost(), 0.001);
    }

    @Test
    void createBooking_pastCheckIn_throwsInvalidDateException() {
        Room room = new SingleRoom(1, 101, 1);
        Guest guest = new Guest(0, "Bob", "bob@test.com", "050");

        assertThrows(InvalidDateException.class, () ->
            service.createBooking(room, guest,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1), 1));
    }

    @Test
    void createBooking_checkOutBeforeCheckIn_throwsInvalidDateException() {
        Room room = new SingleRoom(1, 101, 1);
        Guest guest = new Guest(0, "Bob", "bob@test.com", "050");

        assertThrows(InvalidDateException.class, () ->
            service.createBooking(room, guest,
                LocalDate.now().plusDays(3), LocalDate.now().plusDays(1), 1));
    }

    @Test
    void createBooking_guestCountExceedsMax_throwsInvalidGuestCountException() {
        Room room = new SingleRoom(1, 101, 1);
        Guest guest = new Guest(0, "Bob", "bob@test.com", "050");

        assertThrows(InvalidGuestCountException.class, () ->
            service.createBooking(room, guest,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), 5));
    }

    @Test
    void createBooking_doubleBookSameRoom_throwsRoomNotAvailableException() throws Exception {
        List<Room> rooms = service.getAvailableRooms(
            LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));
        Room room = rooms.get(0);

        service.createBooking(room, new Guest(0, "Alice", "a@test.com", "050"),
            LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), 1);

        assertThrows(RoomNotAvailableException.class, () ->
            service.createBooking(room, new Guest(0, "Bob", "b@test.com", "051"),
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), 1));
    }

    @Test
    void cancelBooking_setsStatusToCancelled() throws Exception {
        List<Room> rooms = service.getAvailableRooms(
            LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));
        Room room = rooms.get(0);

        Booking booking = service.createBooking(room,
            new Guest(0, "Carol", "c@test.com", "052"),
            LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), 1);

        service.cancelBooking(booking.getId());

        Booking cancelled = service.getAllBookings().stream()
            .filter(b -> b.getId() == booking.getId())
            .findFirst().orElseThrow();
        assertEquals("CANCELLED", cancelled.getStatus());
    }

    @Test
    void roomPricing_suiteMoreExpensiveThanSingle() {
        Room single = new SingleRoom(1, 101, 1);
        Room suite = new Suite(2, 401, 4);
        assertTrue(suite.getPricePerNight() > single.getPricePerNight());
    }

    @Test
    void calculateTotalCost_correctForAllRoomTypes() {
        assertEquals(240.0, new SingleRoom(1, 101, 1).calculateTotalCost(3), 0.001);
        assertEquals(360.0, new DoubleRoom(1, 201, 2).calculateTotalCost(3), 0.001);
        assertEquals(540.0, new DeluxeRoom(1, 301, 3).calculateTotalCost(3), 0.001);
        assertEquals(900.0, new Suite(1, 401, 4).calculateTotalCost(3), 0.001);
    }
}
