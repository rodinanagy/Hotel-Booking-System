package com.hotel.repository;

import java.sql.*;

public class DatabaseManager {
    private static String DB_URL = "jdbc:sqlite:hotel_booking.db";
    private static DatabaseManager instance;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static synchronized void configure(String url) {
        DB_URL = url;
        instance = null;
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initializeDatabase() {
        String createRooms =
            "CREATE TABLE IF NOT EXISTS rooms (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "room_number INTEGER UNIQUE NOT NULL," +
            "floor INTEGER NOT NULL," +
            "type TEXT NOT NULL)";

        String createBookings =
            "CREATE TABLE IF NOT EXISTS bookings (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "room_id INTEGER NOT NULL," +
            "guest_name TEXT NOT NULL," +
            "guest_email TEXT NOT NULL," +
            "guest_phone TEXT NOT NULL," +
            "check_in TEXT NOT NULL," +
            "check_out TEXT NOT NULL," +
            "total_cost REAL NOT NULL," +
            "status TEXT NOT NULL DEFAULT 'ACTIVE'," +
            "FOREIGN KEY (room_id) REFERENCES rooms(id))";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createRooms);
            stmt.execute(createBookings);
            seedRooms(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed: " + e.getMessage(), e);
        }
    }

    private void seedRooms(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM rooms")) {
            if (rs.getInt(1) > 0) return;
        }

        Object[][] rooms = {
            {101, 1, "SINGLE"}, {102, 1, "SINGLE"}, {103, 1, "SINGLE"},
            {201, 2, "DOUBLE"}, {202, 2, "DOUBLE"}, {203, 2, "DOUBLE"},
            {301, 3, "DELUXE"}, {302, 3, "DELUXE"},
            {401, 4, "SUITE"},  {402, 4, "SUITE"}
        };

        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO rooms (room_number, floor, type) VALUES (?, ?, ?)")) {
            for (Object[] r : rooms) {
                ps.setInt(1, (Integer) r[0]);
                ps.setInt(2, (Integer) r[1]);
                ps.setString(3, (String) r[2]);
                ps.executeUpdate();
            }
        }
    }
}
