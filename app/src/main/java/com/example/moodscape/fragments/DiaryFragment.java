package com.example.moodscape.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.moodscape.R;
import com.example.moodscape.models.DiaryEntry;
import com.example.moodscape.utils.DatabaseHelper;
import com.example.moodscape.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryFragment extends Fragment {
    CalendarView calendarView;
    EditText etDiaryContent;
    Spinner spinnerEmotion;
    Button btnSaveDiary;
    TextView tvSelectedDate, tvEntryTitle;
    DatabaseHelper db;
    SessionManager session;
    String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diary, container, false);

        db = new DatabaseHelper(requireContext());
        session = new SessionManager(requireContext());

        calendarView = view.findViewById(R.id.calendarView);
        etDiaryContent = view.findViewById(R.id.etDiaryContent);
        spinnerEmotion = view.findViewById(R.id.spinnerEmotion);
        btnSaveDiary = view.findViewById(R.id.btnSaveDiary);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        tvEntryTitle = view.findViewById(R.id.tvEntryTitle);

        String[] emotions = {"😊 Happy", "😢 Sad", "😌 Calm", "😤 Angry",
                "🤔 Thoughtful", "😰 Anxious", "🥰 Grateful", "😴 Tired"};
        spinnerEmotion.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, emotions));

        // Default to today
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvSelectedDate.setText("📅 " + selectedDate);
        loadEntry(selectedDate);

        calendarView.setOnDateChangeListener((v, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);
            tvSelectedDate.setText("📅 " + selectedDate);
            loadEntry(selectedDate);
        });

        btnSaveDiary.setOnClickListener(v -> saveDiaryEntry());

        // Highlight dates with entries
        List<String> datesWithEntries = db.getDatesWithEntries(session.getUserPhone());
        // CalendarView doesn't natively support highlights without custom implementation
        // Entries are loaded on date click

        return view;
    }

    private void loadEntry(String date) {
        DiaryEntry entry = db.getDiaryEntryByDate(session.getUserPhone(), date);
        if (entry != null) {
            etDiaryContent.setText(entry.getContent());
            tvEntryTitle.setText("📖 Entry for " + date + " (tap to edit)");
        } else {
            etDiaryContent.setText("");
            tvEntryTitle.setText("✏️ Write your entry for " + date);
        }
    }

    private void saveDiaryEntry() {
        String content = etDiaryContent.getText().toString().trim();
        if (content.isEmpty()) {
            Toast.makeText(getContext(), "Please write something!", Toast.LENGTH_SHORT).show();
            return;
        }
        DiaryEntry entry = new DiaryEntry();
        entry.setPhone(session.getUserPhone());
        entry.setDate(selectedDate);
        entry.setContent(content);
        entry.setEmotion(spinnerEmotion.getSelectedItem().toString());

        if (db.saveDiaryEntry(entry)) {
            Toast.makeText(getContext(), "Diary entry saved! 📔", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to save.", Toast.LENGTH_SHORT).show();
        }
    }
}
