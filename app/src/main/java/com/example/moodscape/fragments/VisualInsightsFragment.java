package com.example.moodscape.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.*;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.example.moodscape.R;
import com.example.moodscape.models.MoodEntry;
import com.example.moodscape.utils.DatabaseHelper;
import com.example.moodscape.utils.SessionManager;

import java.io.*;
import java.util.*;

public class VisualInsightsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visual_insights, container, false);

        DatabaseHelper db = new DatabaseHelper(requireContext());
        SessionManager session = new SessionManager(requireContext());
        List<MoodEntry> entries = db.getMoodHistory(session.getUserPhone());

        setupPieChart(view, entries);
        setupBarChart(view, entries);
        setupTriggerChart(view, entries);

        view.findViewById(R.id.btnExportCSV).setOnClickListener(v -> exportCSV(entries));

        return view;
    }

    private void setupPieChart(View view, List<MoodEntry> entries) {
        PieChart pieChart = view.findViewById(R.id.pieChartMoods);
        int positive = 0, negative = 0;
        for (MoodEntry e : entries) {
            if (e.isPositive()) positive++; else negative++;
        }
        if (positive + negative == 0) { pieChart.setVisibility(View.GONE); return; }
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(positive, "😊 Positive"));
        pieEntries.add(new PieEntry(negative, "😢 Negative"));
        PieDataSet dataSet = new PieDataSet(pieEntries, "Mood Distribution");
        dataSet.setColors(
                ContextCompat.getColor(requireContext(), R.color.positive_green),
                ContextCompat.getColor(requireContext(), R.color.negative_red)
        );
        dataSet.setValueTextSize(14f);
        int textColor = ContextCompat.getColor(requireContext(), R.color.text_primary);
        dataSet.setValueTextColor(textColor);

        pieChart.setData(new PieData(dataSet));
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Mood\nBalance");
        pieChart.setCenterTextSize(14f);
        pieChart.setCenterTextColor(textColor);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.getLegend().setTextColor(textColor);

        pieChart.animate();
        pieChart.invalidate();
    }

    private void setupBarChart(View view, List<MoodEntry> entries) {
        BarChart barChart = view.findViewById(R.id.barChartIntensity);
        int low = 0, med = 0, high = 0;
        for (MoodEntry e : entries) {
            if (e.getIntensity().equals("Low")) low++;
            else if (e.getIntensity().equals("Medium")) med++;
            else high++;
        }
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0f, low));
        barEntries.add(new BarEntry(1f, med));
        barEntries.add(new BarEntry(2f, high));
        BarDataSet dataSet = new BarDataSet(barEntries, "Intensity Levels");
        dataSet.setColors(Color.parseColor("#81C784"), Color.parseColor("#FFD54F"), Color.parseColor("#E57373"));

        int textColor = ContextCompat.getColor(requireContext(), R.color.text_primary);
        dataSet.setValueTextColor(textColor);

        barChart.setData(new BarData(dataSet));
        barChart.getDescription().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                new String[]{"Low", "Medium", "High"}));
        xAxis.setTextColor(textColor);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        barChart.getAxisLeft().setTextColor(textColor);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setTextColor(textColor);

        barChart.animate();
        barChart.invalidate();
    }

    private void setupTriggerChart(View view, List<MoodEntry> entries) {
        PieChart triggerChart = view.findViewById(R.id.pieChartTriggers);
        Map<String, Integer> triggerCount = new HashMap<>();
        for (MoodEntry e : entries) {
            String t = e.getTriggerPoint();
            triggerCount.put(t, triggerCount.getOrDefault(t, 0) + 1);
        }
        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : triggerCount.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }
        if (pieEntries.isEmpty()) { triggerChart.setVisibility(View.GONE); return; }
        PieDataSet ds = new PieDataSet(pieEntries, "Trigger Points");
        ds.setColors(ColorTemplate.MATERIAL_COLORS);
        ds.setValueTextSize(12f);
        int textColor = ContextCompat.getColor(requireContext(), R.color.text_primary);
        ds.setValueTextColor(textColor);

        triggerChart.setData(new PieData(ds));
        triggerChart.getDescription().setEnabled(false);
        triggerChart.setCenterText("Triggers");
        triggerChart.setCenterTextSize(12f);
        triggerChart.setCenterTextColor(textColor);
        triggerChart.setHoleColor(Color.TRANSPARENT);
        triggerChart.getLegend().setTextColor(textColor);

        triggerChart.animate();
        triggerChart.invalidate();
    }

    private void exportCSV(List<MoodEntry> entries) {
        try {
            File dir = new File(requireContext().getExternalFilesDir(null), "MoodScape");
            dir.mkdirs();
            File file = new File(dir, "mood_history_" + System.currentTimeMillis() + ".csv");
            FileWriter fw = new FileWriter(file);
            fw.write("Mood,Intensity,Trigger,Reason,DateTime,IsPositive\n");
            for (MoodEntry e : entries) {
                fw.write(e.getMood() + "," + e.getIntensity() + "," +
                        e.getTriggerPoint() + "," + e.getReason() + "," +
                        e.getDateTime() + "," + e.isPositive() + "\n");
            }
            fw.close();
            Toast.makeText(getContext(), "Exported to: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException ex) {
            Toast.makeText(getContext(), "Export failed: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
