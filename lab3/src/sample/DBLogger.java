package sample;

import java.sql.*;

public class DBLogger {
    private static final String URL = "jdbc:postgresql://localhost:5432/alarm_logger";
    private static final String USER = "postgres";
    private static final String PASSWORD = "135admin";

    public static void log(String state, String message) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO alarm_logs (state, message) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, state);
                stmt.setString(2, message);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void clearLogs() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM alarm_logs");
            System.out.println("Таблица alarm_logs очищена при закрытии приложения.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
