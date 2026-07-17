package com.example.moodscape;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.moodscape.utils.SessionManager;

public class MoodScapeApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SessionManager session = new SessionManager(this);
        String theme = session.getTheme();
        if (theme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
