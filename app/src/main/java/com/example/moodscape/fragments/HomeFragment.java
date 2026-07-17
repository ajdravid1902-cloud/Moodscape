package com.example.moodscape.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.moodscape.R;
import com.example.moodscape.activities.*;
import com.example.moodscape.utils.SessionManager;

import java.util.Calendar;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SessionManager session = new SessionManager(requireContext());
        String name = session.getUserName();
        int moodScore = session.getMoodScore();
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = hour < 12 ? "Good Morning ☀️" : hour < 17 ? "Good Afternoon 🌤️" : "Good Evening 🌙";

        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        TextView tvWelcome = view.findViewById(R.id.tvWelcomeName);
        TextView tvMoodScoreValue = view.findViewById(R.id.tvMoodScoreValue);
        TextView tvMessage = view.findViewById(R.id.tvDailyMessage);

        tvGreeting.setText(greeting + ", " + name + "! 👋");
        tvWelcome.setText("How are you feeling today? Your journey to wellness starts here 🌟");
        tvMoodScoreValue.setText(moodScore + "/1000");

        String[] messages = {
                "\"Every day is a new beginning. 🌈\"",
                "\"Small steps lead to big changes. 💪\"",
                "\"You are stronger than you think. ✨\"",
                "\"Take care of your mind, body and soul. 🧘\"",
                "\"Progress, not perfection. 🎯\""
        };
        tvMessage.setText(messages[(int)(Math.random() * messages.length)]);

        view.findViewById(R.id.cardTrackMood).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MoodTrackActivity.class)));
        view.findViewById(R.id.cardSelfAssessment).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), SelfAssessmentActivity.class)));
        view.findViewById(R.id.cardVisualInsights).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new VisualInsightsFragment())
                        .addToBackStack(null).commit());
        view.findViewById(R.id.ivSettings).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), SettingsActivity.class)));

        return view;
    }
}
