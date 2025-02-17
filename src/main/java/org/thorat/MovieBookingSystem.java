package org.thorat;

import java.sql.*;
import java.util.Scanner;

public class MovieBookingSystem {
    public static void main(String[] args) {
        DatabaseHelper.createTables();
        DatabaseHelper.insertSampleData();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. View Movies\n2. Book Ticket\n3. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    displayMovies();
                    break;
                case 2:
                    bookTicket(scanner);
                    break;
                case 3:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void displayMovies() {
        String query = "SELECT * FROM movies";
        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("name") + " - " + rs.getString("showtime"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void bookTicket(Scanner scanner) {
        System.out.print("Enter movie ID: ");
        int movieId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter seat number: ");
        String seatNumber = scanner.nextLine();

        String checkSeat = "SELECT id, is_booked FROM seats WHERE movie_id = ? AND seat_number = ?";
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(checkSeat)) {

            pstmt.setInt(1, movieId);
            pstmt.setString(2, seatNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() && rs.getInt("is_booked") == 0) {
                int seatId = rs.getInt("id");
                String updateSeat = "UPDATE seats SET is_booked = 1 WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSeat)) {
                    updateStmt.setInt(1, seatId);
                    updateStmt.executeUpdate();
                }

                String insertBooking = "INSERT INTO bookings (movie_id, seat_id) VALUES (?, ?)";
                try (PreparedStatement bookingStmt = conn.prepareStatement(insertBooking)) {
                    bookingStmt.setInt(1, movieId);
                    bookingStmt.setInt(2, seatId);
                    bookingStmt.executeUpdate();
                }

                System.out.println("Ticket booked successfully!");
            } else {
                System.out.println("Seat not available!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
