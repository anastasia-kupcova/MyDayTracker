package org.example.moodtracker.model;

import java.time.LocalDate;

public class DayRecord {
    private int id;
    private LocalDate date;
    private Integer rating;           // 0-5
    private Integer energyMorning;    // 0-100
    private Integer energyEvening;    // 0-100
    private Integer socialContact;    // 0-5
    private Integer anxiety;          // 0-3
    private Integer weather;          // 1-8
    private Integer sleepHours;       // 1-6
    private Integer dreamType;        // 1-9

    // Конструкторы
    public DayRecord() {}

    public DayRecord(LocalDate date) {
        this.date = date;
    }

    // Геттеры и сеттеры (сгенерируйте через IDE)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public Integer getEnergyMorning() { return energyMorning; }
    public void setEnergyMorning(Integer energyMorning) { this.energyMorning = energyMorning; }

    public Integer getEnergyEvening() { return energyEvening; }
    public void setEnergyEvening(Integer energyEvening) { this.energyEvening = energyEvening; }

    public Integer getSocialContact() { return socialContact; }
    public void setSocialContact(Integer socialContact) { this.socialContact = socialContact; }

    public Integer getAnxiety() { return anxiety; }
    public void setAnxiety(Integer anxiety) { this.anxiety = anxiety; }

    public Integer getWeather() { return weather; }
    public void setWeather(Integer weather) { this.weather = weather; }

    public Integer getSleepHours() { return sleepHours; }
    public void setSleepHours(Integer sleepHours) { this.sleepHours = sleepHours; }

    public Integer getDreamType() { return dreamType; }
    public void setDreamType(Integer dreamType) { this.dreamType = dreamType; }

    // Проверка, заполнены ли данные
    public boolean isComplete() {
        return rating != null && energyMorning != null && energyEvening != null &&
                socialContact != null && anxiety != null && weather != null &&
                sleepHours != null && dreamType != null;
    }
}