package com.loganalysis;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // SECURE: Points only to your new research database
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/log_research_db";
    private static final String DB_USER = "researcher_rs";
    private static final String DB_PASSWORD = "research_pass_123"; 

    private static Connection connection = null;

    private static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }

    public static synchronized void log(String level, String event, String message) {
        String timestampStr = LocalDateTime.now().format(formatter);
        String logEntry = timestampStr + "|" + level + "|main|loganalysis|" + event + "|" + message;
        System.out.println(logEntry);

        // Still write to logs.txt for Hadoop
        try (FileWriter writer = new FileWriter("logs.txt", true)) {
            writer.write(logEntry + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Insert ONLY into the research database
        try (Connection conn = getConnection()) {
            // Auto-create table if it doesn't exist in the new DB
            String createTable = "CREATE TABLE IF NOT EXISTS logs (id SERIAL, timestamp TIMESTAMP, level TEXT, event TEXT, message TEXT)";
            conn.createStatement().execute(createTable);

            String sql = "INSERT INTO logs (timestamp, level, event, message) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(2, level);
                ps.setString(3, event);
                ps.setString(4, message);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Database Error: " + e.getMessage());
        }
    }
}