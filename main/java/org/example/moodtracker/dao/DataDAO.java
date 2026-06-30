package org.example.moodtracker.dao;

import org.example.moodtracker.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DataDAO {

    public void saveValue(LocalDate date, String trackerType, Integer value, String subType) {
        if (value == null) return;

        int userId = DatabaseConnection.getCurrentUserId();

        String sql = "INSERT INTO tracker_data (user_id, record_date, tracker_type, value, sub_type) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE value = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setString(3, trackerType);
            pstmt.setInt(4, value);
            if (subType != null) {
                pstmt.setString(5, subType);
            } else {
                pstmt.setNull(5, java.sql.Types.VARCHAR);
            }
            pstmt.setInt(6, value);

            int affected = pstmt.executeUpdate();
            System.out.println("✅ Сохранено в БД: user=" + userId + ", " + date + " | " + trackerType + " | " + value);

        } catch (SQLException e) {
            System.err.println("❌ Ошибка сохранения в БД: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Map<String, Integer> loadDataForYear(int year, String trackerType, String subType) {
        int userId = DatabaseConnection.getCurrentUserId();
        Map<String, Integer> data = new HashMap<>();

        String sql = "SELECT record_date, value FROM tracker_data " +
                "WHERE user_id = ? AND YEAR(record_date) = ? AND tracker_type = ? " +
                (subType != null ? "AND sub_type = ?" : "");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);
            pstmt.setString(3, trackerType);
            if (subType != null) {
                pstmt.setString(4, subType);
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LocalDate date = rs.getDate("record_date").toLocalDate();
                int value = rs.getInt("value");
                String key = date.getYear() + "-" + date.getMonthValue() + "-" + date.getDayOfMonth();
                if (subType != null) {
                    key += "-" + subType;
                }
                data.put(key, value);
                System.out.println("Загружено: user=" + userId + " | " + trackerType + " | " + key + " = " + value);
            }

        } catch (SQLException e) {
            System.err.println("❌ Ошибка загрузки: " + e.getMessage());
        }

        return data;
    }
}