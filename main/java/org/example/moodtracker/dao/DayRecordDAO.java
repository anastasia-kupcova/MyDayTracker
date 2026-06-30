package org.example.moodtracker.dao;

import org.example.moodtracker.database.DatabaseConnection;
import org.example.moodtracker.model.DayRecord;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DayRecordDAO {

    private Connection getConnection() {
        return DatabaseConnection.getConnection();
    }

    public void saveOrUpdate(DayRecord record) {
        String sql = "INSERT INTO daily_records (record_date, rating, energy_morning, " +
                "energy_evening, social_contact, anxiety, weather, sleep_hours, dream_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE rating=?, energy_morning=?, energy_evening=?, " +
                "social_contact=?, anxiety=?, weather=?, sleep_hours=?, dream_type=?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(record.getDate()));
            setParams(pstmt, record, 2);
            setParams(pstmt, record, 9);

            pstmt.executeUpdate();
            System.out.println("✅ Сохранено: " + record.getDate());
        } catch (SQLException e) {
            System.err.println("❌ Ошибка сохранения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setParams(PreparedStatement pstmt, DayRecord record, int startIndex) throws SQLException {
        pstmt.setInt(startIndex, record.getRating() != null ? record.getRating() : -1);
        pstmt.setInt(startIndex + 1, record.getEnergyMorning() != null ? record.getEnergyMorning() : -1);
        pstmt.setInt(startIndex + 2, record.getEnergyEvening() != null ? record.getEnergyEvening() : -1);
        pstmt.setInt(startIndex + 3, record.getSocialContact() != null ? record.getSocialContact() : -1);
        pstmt.setInt(startIndex + 4, record.getAnxiety() != null ? record.getAnxiety() : -1);
        pstmt.setInt(startIndex + 5, record.getWeather() != null ? record.getWeather() : -1);
        pstmt.setInt(startIndex + 6, record.getSleepHours() != null ? record.getSleepHours() : -1);
        pstmt.setInt(startIndex + 7, record.getDreamType() != null ? record.getDreamType() : -1);
    }

    public DayRecord getByDate(LocalDate date) {
        String sql = "SELECT * FROM daily_records WHERE record_date = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка чтения: " + e.getMessage());
        }
        return null;
    }

    public List<DayRecord> getByMonth(int year, int month) {
        List<DayRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM daily_records WHERE YEAR(record_date) = ? AND MONTH(record_date) = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                records.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка чтения: " + e.getMessage());
        }
        return records;
    }

    private DayRecord mapResultSet(ResultSet rs) throws SQLException {
        DayRecord record = new DayRecord();
        record.setId(rs.getInt("id"));
        record.setDate(rs.getDate("record_date").toLocalDate());

        int val = rs.getInt("rating");
        record.setRating(val == -1 ? null : val);

        val = rs.getInt("energy_morning");
        record.setEnergyMorning(val == -1 ? null : val);

        val = rs.getInt("energy_evening");
        record.setEnergyEvening(val == -1 ? null : val);

        val = rs.getInt("social_contact");
        record.setSocialContact(val == -1 ? null : val);

        val = rs.getInt("anxiety");
        record.setAnxiety(val == -1 ? null : val);

        val = rs.getInt("weather");
        record.setWeather(val == -1 ? null : val);

        val = rs.getInt("sleep_hours");
        record.setSleepHours(val == -1 ? null : val);

        val = rs.getInt("dream_type");
        record.setDreamType(val == -1 ? null : val);

        return record;
    }
}