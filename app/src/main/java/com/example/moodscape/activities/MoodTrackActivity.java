package com.example.moodscape.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.moodscape.R;

import java.util.ArrayList;
import java.util.List;

public class MoodTrackActivity extends AppCompatActivity {
    private static final String[][] QUESTIONS = {
            {"I feel energetic and enthusiastic today.", "Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"I am able to focus on tasks easily.", "Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"I feel connected and supported by people around me.", "Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"I feel anxious or worried about something.", "Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"I feel hopeful about the future.", "Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"I feel overwhelmed by my responsibilities.", "Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"},
            {"Overall, I feel happy and content right now.", "Strongly Agree", "Agree", "Neutral", "Disagree", "Strongly Disagree"}
    };

    // For Q1,2,3,5,7 → positive if Agree/Strongly Agree; for Q4,6 → positive if Disagree/Strongly Disagree
    private static final boolean[] POSITIVE_AGREE = {true, true, true, false, true, false, true};

    int currentQ = 0;
    int positiveCount = 0;
    TextView tvQuestion, tvProgress;
    RadioGroup rgOptions;
    Button btnNext;
    LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_track);

        tvQuestion = findViewById(R.id.tvQuestion);
        tvProgress = findViewById(R.id.tvProgress);
        rgOptions = findViewById(R.id.rgOptions);
        btnNext = findViewById(R.id.btnNext);
        container = findViewById(R.id.quizContainer);

        loadQuestion(0);

        btnNext.setOnClickListener(v -> {
            int selectedId = rgOptions.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton selected = findViewById(selectedId);
            String answer = selected.getText().toString();
            evaluateAnswer(answer);

            currentQ++;
            if (currentQ < QUESTIONS.length) {
                loadQuestion(currentQ);
            } else {
                // Go to results
                Intent intent = new Intent(this, MoodResultActivity.class);
                intent.putExtra("positiveCount", positiveCount);
                intent.putExtra("totalQuestions", QUESTIONS.length);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadQuestion(int index) {
        tvQuestion.setText(QUESTIONS[index][0]);
        tvProgress.setText("Question " + (index + 1) + " / " + QUESTIONS.length);
        rgOptions.removeAllViews();
        rgOptions.clearCheck();

        String[] options = {QUESTIONS[index][1], QUESTIONS[index][2], QUESTIONS[index][3],
                QUESTIONS[index][4], QUESTIONS[index][5]};

        for (String opt : options) {
            RadioButton rb = new RadioButton(this);
            rb.setText(opt);
            rb.setTextSize(15);
            rb.setPadding(16, 12, 16, 12);
            rb.setTextColor(ContextCompat.getColor(this, R.color.text_primary));

            rb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) updateColor(rb.getText().toString(), index);
            });
            rgOptions.addView(rb);
        }
    }

    private void updateColor(String answer, int qIndex) {
        boolean isPositiveAnswer;
        if (POSITIVE_AGREE[qIndex]) {
            isPositiveAnswer = answer.contains("Agree");
        } else {
            isPositiveAnswer = answer.contains("Disagree");
        }
        container.setBackgroundColor(isPositiveAnswer ?
                ContextCompat.getColor(this, R.color.mood_score_bg) :
                ContextCompat.getColor(this, R.color.background_light));
    }

    private void evaluateAnswer(String answer) {
        boolean isPositiveAnswer;
        if (POSITIVE_AGREE[currentQ]) {
            isPositiveAnswer = answer.contains("Agree");
        } else {
            isPositiveAnswer = answer.contains("Disagree");
        }
        if (isPositiveAnswer) positiveCount++;
    }
}
