package com.example.moodscape.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moodscape.R;

public class MoodResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_result);

        int positive = getIntent().getIntExtra("positiveCount", 0);
        int total = getIntent().getIntExtra("totalQuestions", 7);
        int percent = (positive * 100) / total;
        boolean isPositiveMood = percent >= 50;

        TextView tvTitle = findViewById(R.id.tvResultTitle);
        TextView tvScore = findViewById(R.id.tvScore);
        TextView tvDesc = findViewById(R.id.tvDesc);
        Button btnNext = findViewById(R.id.btnSelectMood);

        tvScore.setText(percent + "%");
        if (isPositiveMood) {
            tvTitle.setText("🌟 Positive Mood Detected!");
            tvDesc.setText("Great! You are feeling " + percent + "% positive. Let's explore your mood further!");
            tvScore.setTextColor(getResources().getColor(R.color.positive_green));
        } else {
            tvTitle.setText("🌧 Negative Mood Detected");
            tvDesc.setText("You're feeling " + (100 - percent) + "% negative. That's okay. Let's work through it together.");
            tvScore.setTextColor(getResources().getColor(R.color.negative_red));
        }

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(this, MoodSelectActivity.class);
            intent.putExtra("isPositive", isPositiveMood);
            startActivity(intent);
        });

        Button btnBack = findViewById(R.id.btnBackHome);
        btnBack.setOnClickListener(v -> finish());
    }
}
