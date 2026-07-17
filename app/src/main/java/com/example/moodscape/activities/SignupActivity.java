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

public class SignupActivity extends AppCompatActivity {
    EditText etName, etAge, etPhone, etPassword, etConfirm;
    Spinner spinnerGender;
    Button btnSignup;
    TextView tvLogin;
    DatabaseHelper db;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnSignup = findViewById(R.id.btnSignup);
        tvLogin = findViewById(R.id.tvLogin);

        String[] genders = {"Select Gender", "Male", "Female", "Other", "Prefer not to say"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);

        btnSignup.setOnClickListener(v -> attemptSignup());
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptSignup() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm = etConfirm.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) { etName.setError("Name required"); return; }
        if (TextUtils.isEmpty(ageStr)) { etAge.setError("Age required"); return; }
        int age = Integer.parseInt(ageStr);
        if (age < 5 || age > 120) { etAge.setError("Enter valid age"); return; }
        if (gender.equals("Select Gender")) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show(); return;
        }
        if (TextUtils.isEmpty(phone) || phone.length() != 10) {
            etPhone.setError("Enter valid 10-digit phone"); return;
        }
        if (db.phoneExists(phone)) {
            etPhone.setError("Phone already registered. Please login."); return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters"); return;
        }
        if (!password.equals(confirm)) {
            etConfirm.setError("Passwords do not match"); return;
        }

        User user = new User();
        user.setName(name);
        user.setAge(age);
        user.setPhone(phone);
        user.setPassword(password);
        user.setGender(gender);

        if (db.registerUser(user)) {
            session.createLoginSession(phone, name);
            Toast.makeText(this, "Account created! Welcome 🎉", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}