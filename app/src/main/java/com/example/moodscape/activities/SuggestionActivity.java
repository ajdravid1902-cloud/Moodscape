package com.example.moodscape.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moodscape.R;

import java.util.HashMap;
import java.util.Map;

public class SuggestionActivity extends AppCompatActivity {
    // mood → {thought, song_url, video_url, book}
    static final Map<String, String[]> SUGGESTIONS = new HashMap<String, String[]>() {{
        put("😊 Happy", new String[]{
                "\"Happiness is a choice, not a result. Nothing will make you happy until you choose to be happy.\"",
                "https://open.spotify.com/track/60nZcImufyMA1MKQY3dcCH",
                "https://www.youtube.com/watch?v=ZbZSe6N_BXs",
                "\"The Happiness Advantage\" by Shawn Achor"});
        put("😢 Sad", new String[]{
                "\"Even the darkest night will end and the sun will rise.\" - Victor Hugo",
                "https://open.spotify.com/track/2takcwOaAZWiXQijPHIx7B",
                "https://www.youtube.com/watch?v=GOkYOroa5x4",
                "\"Feeling Good\" by David D. Burns"});
        put("😰 Anxious", new String[]{
                "\"Nothing in life is to be feared, only to be understood.\" - Marie Curie",
                "https://open.spotify.com/playlist/37i9dQZF1DWXe9gFZP0gtP",
                "https://www.youtube.com/watch?v=1ZYbU82GVz4",
                "\"The Anxiety and Worry Workbook\" by Clark & Beck"});
        put("😤 Angry", new String[]{
                "\"Speak when you are angry and you will make the best speech you will ever regret.\"",
                "https://open.spotify.com/track/3FAJ6O0NOHQV8Mc5Ri6ENp",
                "https://www.youtube.com/watch?v=HO56yOGQXvk",
                "\"Anger: Wisdom for Cooling the Flames\" by Thich Nhat Hanh"});
        put("🌟 Motivated", new String[]{
                "\"The secret of getting ahead is getting started.\" - Mark Twain",
                "https://open.spotify.com/track/0TDLuuLlV54CkRRUOahJb4",
                "https://www.youtube.com/watch?v=mgmVOuLgFB0",
                "\"Atomic Habits\" by James Clear"});
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        String mood = getIntent().getStringExtra("mood");
        String trigger = getIntent().getStringExtra("trigger");

        TextView tvMoodTitle = findViewById(R.id.tvSuggMood);
        TextView tvThought = findViewById(R.id.tvSuggThought);
        TextView tvBook = findViewById(R.id.tvSuggBook);
        Button btnSong = findViewById(R.id.btnOpenSong);
        Button btnVideo = findViewById(R.id.btnOpenVideo);
        Button btnBack = findViewById(R.id.btnBackFromSugg);

        tvMoodTitle.setText("Suggestions for: " + mood);

        String[] data = SUGGESTIONS.getOrDefault(mood,
                new String[]{"\"Every day is a new beginning. Take a deep breath and start again.\"",
                        "https://open.spotify.com/",
                        "https://www.youtube.com/",
                        "\"The Power of Now\" by Eckhart Tolle"});

        tvThought.setText(data[0]);
        tvBook.setText("📚 " + data[3]);

        String songUrl = data[1];
        String videoUrl = data[2];

        btnSong.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(songUrl))));
        btnVideo.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))));
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
