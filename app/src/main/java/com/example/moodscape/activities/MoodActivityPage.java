package com.example.moodscape.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moodscape.R;
import com.example.moodscape.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

public class MoodActivityPage extends AppCompatActivity {
    // Map mood → {thought, activity}
    static final Map<String, String[]> MOOD_DATA = new HashMap<String, String[]>() {{
        put("Happy", new String[]{
                "💭 \"Happiness is not something ready made. It comes from your own actions.\" - Dalai Lama",
                "🎵 Activity: Create a 5-song happy playlist right now and dance to the first song for 2 minutes!"});
        put("Motivated", new String[]{
                "💭 \"The secret of getting ahead is getting started.\" - Mark Twain",
                "📋 Activity: Write down 3 goals you want to achieve this week and put a star next to the most important one."});
        put("Peaceful", new String[]{
                "💭 \"Peace is the result of retraining your mind to process life as it is, rather than as you think it should be.\"",
                "🧘 Activity: Do a 5-minute breathing exercise – inhale 4s, hold 4s, exhale 4s. Repeat 5 times."});
        put("Loved", new String[]{
                "💭 \"The best thing to hold onto in life is each other.\" - Audrey Hepburn",
                "💌 Activity: Send a heartfelt thank-you message to someone who has made a difference in your life."});
        put("Excited", new String[]{
                "💭 \"Enthusiasm is the electricity of life!\"",
                "✍️ Activity: Write about what excites you most right now in 5 sentences. Be as vivid as possible!"});
        put("Confident", new String[]{
                "💭 \"Believe you can and you're halfway there.\" - Theodore Roosevelt",
                "🪞 Activity: Stand in front of a mirror and say 3 affirmations loudly with full confidence!"});
        put("Sad", new String[]{
                "💭 \"Even the darkest night will end and the sun will rise.\" - Victor Hugo",
                "🎨 Activity: Draw or doodle how you feel on paper for 5 minutes. No rules, just express!"});
        put("Anxious", new String[]{
                "💭 \"You don't have to control your thoughts. You just have to stop letting them control you.\"",
                "🌿 Activity: Name 5 things you can see, 4 you can touch, 3 you can hear, 2 you can smell, 1 you can taste."});
        put("Angry", new String[]{
                "💭 \"For every minute you remain angry, you give up sixty seconds of peace of mind.\"",
                "💥 Activity: Write down what's making you angry on paper, then tear it into tiny pieces and throw it away."});
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_activity);

        String moodEmoji = getIntent().getStringExtra("moodEmoji");
        String moodName = getIntent().getStringExtra("moodName");
        String moodDesc = getIntent().getStringExtra("moodDesc");

        TextView tvEmoji = findViewById(R.id.tvActivityEmoji);
        TextView tvMoodName = findViewById(R.id.tvActivityMoodName);
        TextView tvMoodDesc = findViewById(R.id.tvActivityMoodDesc);
        TextView tvThought = findViewById(R.id.tvThought);
        TextView tvActivity = findViewById(R.id.tvActivity);
        Button btnDone = findViewById(R.id.btnActivityDone);

        tvEmoji.setText(moodEmoji);
        tvMoodName.setText(moodName);
        tvMoodDesc.setText(moodDesc);

        String[] data = MOOD_DATA.getOrDefault(moodName,
                new String[]{"💭 Take a moment to reflect on your feelings.",
                        "🌟 Activity: Take a 5 minute walk outside and breathe fresh air."});

        tvThought.setText(data[0]);
        tvActivity.setText(data[1]);

        btnDone.setOnClickListener(v -> {
            new SessionManager(this).addMoodPoints(10);
            Toast.makeText(this, "🎉 +10 Mood Points! Activity completed!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
