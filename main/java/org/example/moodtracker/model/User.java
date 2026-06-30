package org.example.moodtracker.model;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String displayName;
    private String role;

    public User() {}

    public User(int id, String username, String displayName, String role) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
    }

    public boolean isGuest() {
        return "guest".equals(role);
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return displayName != null ? displayName : username;
    }
}