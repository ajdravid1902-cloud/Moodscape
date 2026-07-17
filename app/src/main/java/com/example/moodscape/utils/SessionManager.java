package com.example.moodscape.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String phone, String name) {
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.putString(Constants.KEY_USER_PHONE, phone);
        editor.putString(Constants.KEY_USER_NAME, name);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public String getUserPhone() {
        return pref.getString(Constants.KEY_USER_PHONE, "");
    }

    public String getUserName() {
        return pref.getString(Constants.KEY_USER_NAME, "User");
    }

    public int getMoodScore() {
        checkAndResetWeeklyScore();
        return pref.getInt(Constants.KEY_MOOD_SCORE, 0);
    }

    private void checkAndResetWeeklyScore() {
        long lastReset = pref.getLong(Constants.KEY_LAST_RESET_TIME, 0);
        long currentTime = System.currentTimeMillis();
        
        // 7 days in milliseconds: 7 * 24 * 60 * 60 * 1000 = 604,800,000
        if (lastReset == 0) {
            editor.putLong(Constants.KEY_LAST_RESET_TIME, currentTime);
            editor.apply();
        } else if (currentTime - lastReset >= 604800000L) {
            editor.putInt(Constants.KEY_MOOD_SCORE, 0);
            editor.putLong(Constants.KEY_LAST_RESET_TIME, currentTime);
            editor.apply();
        }
    }

    public void addMoodPoints(int points) {
        checkAndResetWeeklyScore();
        int current = getMoodScore();
        int newScore = Math.min(current + points, Constants.MAX_SCORE);
        editor.putInt(Constants.KEY_MOOD_SCORE, newScore);
        editor.apply();
    }

    public String getTheme() {
        return pref.getString(Constants.KEY_THEME, "light");
    }

    public void setTheme(String theme) {
        editor.putString(Constants.KEY_THEME, theme);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
