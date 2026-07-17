package com.example.moodscape.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moodscape.R;
import com.example.moodscape.models.User;
import com.example.moodscape.utils.DatabaseHelper;
import com.example.moodscape.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {
    EditText etPhone, etPassword;
    Button btnLogin;
    TextView tvSignup;
    DatabaseHelper db;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);

        btnLogin.setOnClickListener(v -> attemptLogin());
        tvSignup.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));
    }

    private void attemptLogin() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone number required");
            return;
        }
        if (phone.length() != 10) {
            etPhone.setError("Enter valid 10-digit phone number");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password required");
            return;
        }
        if (!db.phoneExists(phone)) {
            Toast.makeText(this, "Phone not registered. Please sign up first.", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = db.loginUser(phone, password);
        if (user != null) {
            session.createLoginSession(phone, user.getName());
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Incorrect password!", Toast.LENGTH_SHORT).show();
        }
    }
}