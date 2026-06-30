package org.example.moodtracker.database;

import org.example.moodtracker.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/mood_tracker?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "234789%Flol&";

    private static Connection connection = null;
    private static User currentUser = null;  // ← новое

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Подключение к БД установлено");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Ошибка подключения к БД: " + e.getMessage());
        }
        return connection;
    }

    // ← новые методы
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : 1; // 1 = guest
    }

    public static boolean isGuest() {
        return currentUser == null || currentUser.isGuest();
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔌 Соединение с БД закрыто");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}