package org.example.moodtracker.dao;

import org.example.moodtracker.database.DatabaseConnection;
import org.example.moodtracker.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class UserDAO {

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND is_active = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");

                if (username.equals("guest") || BCrypt.checkpw(password, storedHash)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(storedHash);
                    user.setDisplayName(rs.getString("display_name"));
                    user.setRole(rs.getString("role"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка авторизации: " + e.getMessage());
        }
        return null;
    }

    public boolean register(String username, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        // display_name = username (по умолчанию)
        String sql = "INSERT INTO users (username, password_hash, display_name, role) VALUES (?, ?, ?, 'user')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, username); // display_name = login

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Ошибка регистрации: " + e.getMessage());
            return false;
        }
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public User getGuestUser() {
        String sql = "SELECT * FROM users WHERE username = 'guest'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername("guest");
                user.setDisplayName("Гость");
                user.setRole("guest");
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}