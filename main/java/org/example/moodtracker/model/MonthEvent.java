package org.example.moodtracker.model;

public class MonthEvent {
    private int id;
    private int year;
    private int month;        // 1-12
    private String eventText;
    private boolean hasEvent; // отметка

    // Конструкторы
    public MonthEvent() {}

    public MonthEvent(int year, int month) {
        this.year = year;
        this.month = month;
        this.hasEvent = false;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public String getEventText() { return eventText; }
    public void setEventText(String eventText) {
        this.eventText = eventText;
        this.hasEvent = (eventText != null && !eventText.trim().isEmpty());
    }

    public boolean isHasEvent() { return hasEvent; }
    public void setHasEvent(boolean hasEvent) { this.hasEvent = hasEvent; }
}