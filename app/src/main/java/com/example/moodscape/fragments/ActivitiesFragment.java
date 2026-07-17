package com.example.moodscape.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.moodscape.R;
import com.example.moodscape.utils.SessionManager;

import java.util.*;

public class ActivitiesFragment extends Fragment {
    SessionManager session;
    TextView tvScore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activities, container, false);
        session = new SessionManager(requireContext());
        tvScore = view.findViewById(R.id.tvActivitiesScore);
        updateScore();

        // Daily Breathing Exercise
        view.findViewById(R.id.btnBreathing).setOnClickListener(v -> showBreathingExercise());

        // Gratitude Journal
        view.findViewById(R.id.btnGratitude).setOnClickListener(v -> showGratitudeActivity());

        // Memory Game
        view.findViewById(R.id.btnMemoryGame).setOnClickListener(v -> showMemoryGame());

        // Balloon Popping Game
        view.findViewById(R.id.btnBalloon).setOnClickListener(v -> showBalloonGame());

        return view;
    }

    private void updateScore() {
        tvScore.setText("🏆 Mood Score: " + session.getMoodScore() + " / 1000");
    }

    private void showBreathingExercise() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_breathing, null);
        TextView tvBreathState = dialogView.findViewById(R.id.tvBreathState);
        ProgressBar pbBreath = dialogView.findViewById(R.id.pbBreathing);

        String[] states = {"Inhale... 🌬️", "Hold... ⏸️", "Exhale... 💨", "Hold... ⏸️"};
        int[] durations = {4000, 4000, 4000, 4000};
        final int[] currentState = {0};
        final int[] cycles = {0};

        android.app.AlertDialog dialog = builder.setTitle("Breathing Exercise 🧘")
                .setView(dialogView)
                .setNegativeButton("Done", (d, w) -> {
                    if (cycles[0] >= 3) {
                        session.addMoodPoints(10);
                        updateScore();
                        Toast.makeText(getContext(), "+10 Points! 🎉", Toast.LENGTH_SHORT).show();
                    }
                }).create();
        dialog.show();

        Runnable[] runner = {null};
        runner[0] = () -> {
            tvBreathState.setText(states[currentState[0]]);
            if (currentState[0] == 0 || currentState[0] == 2) {
                pbBreath.setIndeterminate(false);
                new CountDownTimer(durations[currentState[0]], 100) {
                    int progress = 0;
                    @Override public void onTick(long ms) {
                        pbBreath.setProgress((int)((durations[currentState[0]] - ms) * 100 / durations[currentState[0]]));
                    }
                    @Override public void onFinish() {
                        pbBreath.setProgress(100);
                        currentState[0] = (currentState[0] + 1) % 4;
                        if (currentState[0] == 0) cycles[0]++;
                        if (dialog.isShowing()) tvBreathState.postDelayed(runner[0], 200);
                    }
                }.start();
            } else {
                tvBreathState.postDelayed(() -> {
                    currentState[0] = (currentState[0] + 1) % 4;
                    if (currentState[0] == 0) cycles[0]++;
                    if (dialog.isShowing()) runner[0].run();
                }, durations[currentState[0]]);
            }
        };
        tvBreathState.post(runner[0]);
    }

    private void showGratitudeActivity() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_gratitude, null);
        EditText et1 = dialogView.findViewById(R.id.etGratitude1);
        EditText et2 = dialogView.findViewById(R.id.etGratitude2);
        EditText et3 = dialogView.findViewById(R.id.etGratitude3);

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("🙏 Gratitude Journal")
                .setMessage("Write 3 things you are grateful for today:")
                .setView(dialogView)
                .setPositiveButton("Submit", (d, w) -> {
                    if (!et1.getText().toString().trim().isEmpty() &&
                            !et2.getText().toString().trim().isEmpty() &&
                            !et3.getText().toString().trim().isEmpty()) {
                        session.addMoodPoints(10);
                        updateScore();
                        Toast.makeText(getContext(), "Gratitude recorded! +10 Points 🌟", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Please fill all 3 gratitude fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showMemoryGame() {
        // Simple number memory game
        int[] sequence = new int[4];
        Random rand = new Random();
        StringBuilder sb = new StringBuilder("Remember this sequence:\n\n");
        for (int i = 0; i < 4; i++) {
            sequence[i] = rand.nextInt(9) + 1;
            sb.append(sequence[i]).append("  ");
        }

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_memory_game, null);
        TextView tvSeq = dialogView.findViewById(R.id.tvMemorySequence);
        EditText etAnswer = dialogView.findViewById(R.id.etMemoryAnswer);
        Button btnReveal = dialogView.findViewById(R.id.btnRevealSequence);

        tvSeq.setText("? ? ? ?");
        btnReveal.setOnClickListener(v -> {
            tvSeq.setText(sb.toString());
            tvSeq.postDelayed(() -> tvSeq.setText("? ? ? ?"), 3000);
        });

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("🧠 Memory Game")
                .setView(dialogView)
                .setPositiveButton("Check", (d, w) -> {
                    String answer = etAnswer.getText().toString().trim().replaceAll("\\s+", "");
                    StringBuilder correct = new StringBuilder();
                    for (int n : sequence) correct.append(n);
                    if (answer.equals(correct.toString())) {
                        session.addMoodPoints(10);
                        updateScore();
                        Toast.makeText(getContext(), "Correct! 🎉 +10 Points!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Wrong! The sequence was: " + correct, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showBalloonGame() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_balloon_game, null);
        TextView tvBalloon = dialogView.findViewById(R.id.tvBalloon);
        TextView tvBalloonScore = dialogView.findViewById(R.id.tvBalloonScore);
        final int[] score = {0};
        final int[] target = {5};

        tvBalloon.setOnClickListener(v -> {
            score[0]++;
            tvBalloonScore.setText("Popped: " + score[0] + " / " + target[0]);
            float scale = 0.9f + (score[0] * 0.1f);
            tvBalloon.setScaleX(Math.min(scale, 1.8f));
            tvBalloon.setScaleY(Math.min(scale, 1.8f));
            if (score[0] >= target[0]) {
                tvBalloon.setText("💥");
                tvBalloonScore.setText("Burst! Great job! 🎉");
            }
        });

        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("🎈 Balloon Popping Game")
                .setMessage("Tap the balloon to pop it 5 times!")
                .setView(dialogView)
                .setPositiveButton("Done", (d, w) -> {
                    if (score[0] >= target[0]) {
                        session.addMoodPoints(10);
                        updateScore();
                        Toast.makeText(getContext(), "Well done! +10 Points! 🎊", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
