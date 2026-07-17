package com.example.moodscape.models;

public class MoodEntry {
    private String phone, mood, intensity, triggerPoint, reason, dateTime;
    private boolean isPositive;
    // Getters and Setters
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public String getIntensity() { return intensity; }
    public void setIntensity(String intensity) { this.intensity = intensity; }
    public String getTriggerPoint() { return triggerPoint; }
    public void setTriggerPoint(String triggerPoint) { this.triggerPoint = triggerPoint; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }
    public boolean isPositive() { return isPositive; }
    public void setPositive(boolean positive) { isPositive = positive; }
}
