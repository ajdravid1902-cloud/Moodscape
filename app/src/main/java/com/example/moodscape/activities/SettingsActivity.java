package com.example.moodscape.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moodscape.R;
import com.example.moodscape.utils.DatabaseHelper;
import com.example.moodscape.utils.SessionManager;

public class SettingsActivity extends AppCompatActivity {
    SessionManager session;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        session = new SessionManager(this);
        db = new DatabaseHelper(this);

        Switch switchTheme = findViewById(R.id.switchTheme);
        switchTheme.setChecked(session.getTheme().equals("dark"));
        switchTheme.setOnCheckedChangeListener((v, checked) -> {
            String theme = checked ? "dark" : "light";
            session.setTheme(theme);
            if (checked) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        findViewById(R.id.btnChangePassword).setOnClickListener(v -> showChangePasswordDialog());
        findViewById(R.id.btnAbout).setOnClickListener(v -> showAboutDialog());
        findViewById(R.id.btnLogout).setOnClickListener(v -> showLogoutDialog());
        findViewById(R.id.btnPrivacy).setOnClickListener(v -> showPrivacyDialog());
        findViewById(R.id.btnBackSettings).setOnClickListener(v -> finish());
    }

    private void showChangePasswordDialog() {
        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText etOld = view.findViewById(R.id.etOldPassword);
        EditText etNew = view.findViewById(R.id.etNewPassword);
        EditText etConfirm = view.findViewById(R.id.etConfirmNewPassword);

        new AlertDialog.Builder(this)
                .setTitle("Change Password")
                .setView(view)
                .setPositiveButton("Update", (d, w) -> {
                    String oldPass = etOld.getText().toString().trim();
                    String newPass = etNew.getText().toString().trim();
                    String confirm = etConfirm.getText().toString().trim();
                    if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPass)) {
                        Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!newPass.equals(confirm)) {
                        Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String phone = session.getUserPhone();
                    if (db.loginUser(phone, oldPass) != null) {
                        db.updatePassword(phone, newPass);
                        Toast.makeText(this, "Password updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Old password incorrect", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About MoodScape")
                .setMessage("MoodScape v1.0\n\nYour personal mental wellness companion.\n\n" +
                        "Track moods, gain insights, and improve your emotional well-being through " +
                        "guided activities, journaling, and AI-powered support.\n\n" +
                        "Built with ❤️ for better mental health.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showPrivacyDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Privacy Policy")
                .setMessage("Your data is stored securely on your device. MoodScape does not " +
                        "share your personal mood data with third parties. " +
                        "All mood records are private and only accessible by you.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (d, w) -> {
                    session.logout();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
