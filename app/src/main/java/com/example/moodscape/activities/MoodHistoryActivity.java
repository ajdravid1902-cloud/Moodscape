package com.example.moodscape.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.moodscape.R;
import com.example.moodscape.adapters.MoodAdapter;
import com.example.moodscape.models.MoodEntry;
import com.example.moodscape.utils.DatabaseHelper;
import com.example.moodscape.utils.SessionManager;

import java.util.List;

public class MoodHistoryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood_history);

        RecyclerView rv = findViewById(R.id.rvMoodHistory);
        DatabaseHelper db = new DatabaseHelper(this);
        SessionManager session = new SessionManager(this);

        List<MoodEntry> entries = db.getMoodHistory(session.getUserPhone());
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new MoodAdapter(this, entries));

        findViewById(R.id.btnBackFromHistory).setOnClickListener(v -> finish());
    }
}
