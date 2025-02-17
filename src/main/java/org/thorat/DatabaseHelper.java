package org.thorat;

import java.sql.*;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:movies.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void createTables() {
        String createMoviesTable = "CREATE TABLE IF NOT EXISTS movies (id INTEGER PRIMARY KEY, name TEXT, showtime TEXT)";
        String createSeatsTable = "CREATE TABLE IF NOT EXISTS seats (id INTEGER PRIMARY KEY, movie_id INTEGER, seat_number TEXT, is_booked INTEGER DEFAULT 0, FOREIGN KEY(movie_id) REFERENCES movies(id))";
        String createBookingsTable = "CREATE TABLE IF NOT EXISTS bookings (id INTEGER PRIMARY KEY, movie_id INTEGER, seat_id INTEGER, FOREIGN KEY(movie_id) REFERENCES movies(id), FOREIGN KEY(seat_id) REFERENCES seats(id))";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createMoviesTable);
            stmt.execute(createSeatsTable);
            stmt.execute(createBookingsTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertSampleData() {
        String insertMovies = "INSERT INTO movies (name, showtime) VALUES ('Avatar 2', '18:00'), ('The Batman', '20:30')";
        String insertSeats = "INSERT INTO seats (movie_id, seat_number) VALUES (1, 'A1'), (1, 'A2'), (2, 'B1')";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(insertMovies);
            stmt.execute(insertSeats);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
