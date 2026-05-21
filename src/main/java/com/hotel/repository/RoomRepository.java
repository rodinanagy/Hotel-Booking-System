package com.hotel.repository;

import com.hotel.exception.RoomNotFoundException;
import com.hotel.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomRepository {
    private final DatabaseManager db = DatabaseManager.getInstance();

    public Room findById(int id) throws RoomNotFoundException, SQLException {
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM rooms WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new RoomNotFoundException(id);
                return mapRoom(rs);
            }
        }
    }

    public List<Room> findAll() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms ORDER BY room_number")) {
            while (rs.next()) rooms.add(mapRoom(rs));
        }
        return rooms;
    }

    private Room mapRoom(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int roomNumber = rs.getInt("room_number");
        int floor = rs.getInt("floor");
        String type = rs.getString("type");

        switch (type) {
            case "SINGLE": return new SingleRoom(id, roomNumber, floor);
            case "DOUBLE": return new DoubleRoom(id, roomNumber, floor);
            case "DELUXE": return new DeluxeRoom(id, roomNumber, floor);
            case "SUITE":  return new Suite(id, roomNumber, floor);
            default: throw new IllegalStateException("Unknown room type: " + type);
        }
    }
}
