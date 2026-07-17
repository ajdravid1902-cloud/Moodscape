package com.example.moodscape.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moodscape.R;
import com.example.moodscape.adapters.MoodAdapter;

import java.util.Arrays;
import java.util.List;

public class MoodSelectActivity extends AppCompatActivity {

    static final String[][] POSITIVE_MOODS = {
            {"😊","Happy","Feeling joyful and content"},
            {"🌟","Motivated","Driven and full of energy"},
            {"😌","Peaceful","Calm and relaxed"},
            {"🥰","Loved","Feeling appreciated and cared for"},
            {"🎉","Excited","Enthusiastic about something"},
            {"💪","Confident","Feeling strong and capable"},
            {"🙏","Grateful","Appreciating the good in life"},
            {"😄","Cheerful","Upbeat and positive"},
            {"🌈","Hopeful","Optimistic about the future"},
            {"🎯","Focused","Clear minded and determined"},
            {"🤗","Content","Satisfied with life"},
            {"✨","Inspired","Creative and imaginative"}
    };

    static final String[][] NEGATIVE_MOODS = {
            {"😢","Sad","Feeling down and unhappy"},
            {"😰","Anxious","Worried and nervous"},
            {"😤","Angry","Frustrated or irritated"},
            {"😔","Lonely","Feeling isolated"},
            {"😩","Exhausted","Tired and burnt out"},
            {"😞","Disappointed","Expectations not met"},
            {"😨","Scared","Feeling fearful"},
            {"😶","Numb","Feeling empty inside"},
            {"😒","Bored","Lack of interest"},
            {"🤢","Overwhelmed","Too much to handle"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_select);

        boolean isPositive = getIntent().getBooleanExtra("isPositive", true);
        RecyclerView rv = findViewById(R.id.rvMoods);
        TextView tvTitle = findViewById(R.id.tvSelectTitle);

        String[][] moods = isPositive ? POSITIVE_MOODS : NEGATIVE_MOODS;
        tvTitle.setText(isPositive ? "🌟 Select Your Positive Mood" : "💙 Select Your Current Mood");

        rv.setLayoutManager(new GridLayoutManager(this, 3));
        rv.setAdapter(new MoodAdapter(this, moods, (mood) -> {
            Intent intent = new Intent(this, MoodActivityPage.class);
            intent.putExtra("moodEmoji", mood[0]);
            intent.putExtra("moodName", mood[1]);
            intent.putExtra("moodDesc", mood[2]);
            intent.putExtra("isPositive", isPositive);
            startActivity(intent);
        }));
    }
}
