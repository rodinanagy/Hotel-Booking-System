package com.hotel.repository;

import com.hotel.exception.RoomNotFoundException;
import com.hotel.model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BookingRepository {
    private final DatabaseManager db = DatabaseManager.getInstance();
    private final RoomRepository roomRepository = new RoomRepository();

    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) throws SQLException {
        String sql =
            "SELECT COUNT(*) FROM bookings " +
            "WHERE room_id = ? AND status = 'ACTIVE' AND check_in < ? AND check_out > ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setString(2, checkOut.toString());
            ps.setString(3, checkIn.toString());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.getInt(1) == 0;
            }
        }
    }

    public Booking save(Room room, Guest guest, LocalDate checkIn, LocalDate checkOut) throws SQLException {
        String sql =
            "INSERT INTO bookings (room_id, guest_name, guest_email, guest_phone, " +
            "check_in, check_out, total_cost, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        int nights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
        double totalCost = room.calculateTotalCost(nights);

        try (Connection conn = db.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, room.getId());
                ps.setString(2, guest.getName());
                ps.setString(3, guest.getEmail());
                ps.setString(4, guest.getPhone());
                ps.setString(5, checkIn.toString());
                ps.setString(6, checkOut.toString());
                ps.setDouble(7, totalCost);
                ps.executeUpdate();
            }
            int id;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                id = rs.next() ? rs.getInt(1) : -1;
            }
            Booking booking = new Booking(id, room, guest, checkIn, checkOut);
            booking.setTotalCost(totalCost);
            return booking;
        }
    }

    public void cancelBooking(int bookingId) throws SQLException {
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE bookings SET status = 'CANCELLED' WHERE id = ?")) {
            ps.setInt(1, bookingId);
            ps.executeUpdate();
        }
    }

    public List<Booking> findAll() throws SQLException, RoomNotFoundException {
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM bookings ORDER BY id DESC")) {
            while (rs.next()) bookings.add(mapBooking(rs));
        }
        return bookings;
    }

    private Booking mapBooking(ResultSet rs) throws SQLException, RoomNotFoundException {
        int id = rs.getInt("id");
        Room room = roomRepository.findById(rs.getInt("room_id"));
        Guest guest = new Guest(0,
            rs.getString("guest_name"),
            rs.getString("guest_email"),
            rs.getString("guest_phone"));
        LocalDate checkIn = LocalDate.parse(rs.getString("check_in"));
        LocalDate checkOut = LocalDate.parse(rs.getString("check_out"));

        Booking booking = new Booking(id, room, guest, checkIn, checkOut);
        booking.setTotalCost(rs.getDouble("total_cost"));
        booking.setStatus(rs.getString("status"));
        return booking;
    }
}
