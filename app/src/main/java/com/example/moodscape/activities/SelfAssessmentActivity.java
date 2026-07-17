package com.example.moodscape.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moodscape.R;
import com.example.moodscape.models.MoodEntry;
import com.example.moodscape.utils.DatabaseHelper;
import com.example.moodscape.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SelfAssessmentActivity extends AppCompatActivity {
    Spinner spinnerMood, spinnerTrigger;
    SeekBar seekIntensity;
    TextView tvIntensityLabel;
    EditText etReason;
    Button btnSubmit, btnHistory;
    DatabaseHelper db;
    SessionManager session;

    final String[] MOODS = {"😊 Happy", "😌 Peaceful", "🌟 Motivated", "😢 Sad",
            "😰 Anxious", "😤 Angry", "😔 Lonely", "😩 Exhausted",
            "🥰 Loved", "💪 Confident", "😶 Numb", "🎉 Excited"};
    final String[] TRIGGERS = {"Home", "Studies/Work", "Relationship", "Social",
            "Health", "Financial", "Personal Growth", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_assessment);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        spinnerMood = findViewById(R.id.spinnerMood);
        spinnerTrigger = findViewById(R.id.spinnerTrigger);
        seekIntensity = findViewById(R.id.seekIntensity);
        tvIntensityLabel = findViewById(R.id.tvIntensityLabel);
        etReason = findViewById(R.id.etReason);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnHistory = findViewById(R.id.btnMoodHistory);

        spinnerMood.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, MOODS));
        spinnerTrigger.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, TRIGGERS));

        seekIntensity.setMax(2); // 0=Low, 1=Medium, 2=High
        seekIntensity.setProgress(1);
        tvIntensityLabel.setText("Medium");
        seekIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean f) {
                tvIntensityLabel.setText(p == 0 ? "Low 🟢" : p == 1 ? "Medium 🟡" : "High 🔴");
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        btnSubmit.setOnClickListener(v -> submitAssessment());
        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, MoodHistoryActivity.class)));
    }

    private void submitAssessment() {
        String mood = spinnerMood.getSelectedItem().toString();
        String trigger = spinnerTrigger.getSelectedItem().toString();
        String reason = etReason.getText().toString().trim();
        int intensityVal = seekIntensity.getProgress();
        String intensity = intensityVal == 0 ? "Low" : intensityVal == 1 ? "Medium" : "High";

        boolean isPositive = mood.contains("Happy") || mood.contains("Motivated") ||
                mood.contains("Peaceful") || mood.contains("Loved") ||
                mood.contains("Confident") || mood.contains("Excited");

        MoodEntry entry = new MoodEntry();
        entry.setPhone(session.getUserPhone());
        entry.setMood(mood);
        entry.setIntensity(intensity);
        entry.setTriggerPoint(trigger);
        entry.setReason(reason.isEmpty() ? "Not specified" : reason);
        entry.setDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date()));
        entry.setPositive(isPositive);

        if (db.saveMoodEntry(entry)) {
            Intent intent = new Intent(this, SuggestionActivity.class);
            intent.putExtra("mood", mood);
            intent.putExtra("trigger", trigger);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Failed to save. Try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
