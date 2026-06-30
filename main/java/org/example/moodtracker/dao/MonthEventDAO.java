package org.example.moodtracker.dao;

import org.example.moodtracker.database.DatabaseConnection;
import org.example.moodtracker.model.MonthEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonthEventDAO {

    public void saveOrUpdate(MonthEvent event) {
        int userId = DatabaseConnection.getCurrentUserId();

        String sql = "INSERT INTO month_events (user_id, year, month, event_text, has_event) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE event_text=?, has_event=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, event.getYear());
            pstmt.setInt(3, event.getMonth());
            pstmt.setString(4, event.getEventText());
            pstmt.setBoolean(5, event.isHasEvent());
            pstmt.setString(6, event.getEventText());
            pstmt.setBoolean(7, event.isHasEvent());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public MonthEvent getByYearMonth(int year, int month) {
        int userId = DatabaseConnection.getCurrentUserId();
        String sql = "SELECT * FROM month_events WHERE user_id = ? AND year = ? AND month = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            pstmt.setInt(3, month);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<MonthEvent> getAllForYear(int year) {
        int userId = DatabaseConnection.getCurrentUserId();
        List<MonthEvent> events = new ArrayList<>();
        String sql = "SELECT * FROM month_events WHERE user_id = ? AND year = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                events.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    private MonthEvent mapResultSet(ResultSet rs) throws SQLException {
        MonthEvent event = new MonthEvent();
        event.setId(rs.getInt("id"));
        event.setYear(rs.getInt("year"));
        event.setMonth(rs.getInt("month"));
        event.setEventText(rs.getString("event_text"));
        event.setHasEvent(rs.getBoolean("has_event"));
        return event;
    }
}