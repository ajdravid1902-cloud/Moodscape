package com.example.moodscape.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodscape.R;
import com.example.moodscape.models.MoodEntry;

import java.util.List;

public class MoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // ─── View type constants ───────────────────────────────────────────────
    public static final int TYPE_MOOD_CARD   = 0;   // emoji grid (MoodSelectActivity)
    public static final int TYPE_HISTORY_ROW = 1;   // history list (MoodHistoryActivity)

    // ─── Callback interfaces ───────────────────────────────────────────────
    public interface OnMoodCardClickListener {
        void onMoodCardClick(String[] moodData); // [emoji, name, description]
    }

    // ─── Data ──────────────────────────────────────────────────────────────
    private final Context           context;
    private final int               viewType;
    private       String[][]        moodCards;     // used when TYPE_MOOD_CARD
    private       List<MoodEntry>   historyList;   // used when TYPE_HISTORY_ROW
    private       OnMoodCardClickListener clickListener;

    // ─── Constructor: Mood Card Grid ───────────────────────────────────────
    public MoodAdapter(Context context, String[][] moodCards,
                       OnMoodCardClickListener listener) {
        this.context       = context;
        this.viewType      = TYPE_MOOD_CARD;
        this.moodCards     = moodCards;
        this.clickListener = listener;
    }

    // ─── Constructor: Mood History List ────────────────────────────────────
    public MoodAdapter(Context context, List<MoodEntry> historyList) {
        this.context     = context;
        this.viewType    = TYPE_HISTORY_ROW;
        this.historyList = historyList;
    }

    // ─── Boilerplate ───────────────────────────────────────────────────────
    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @Override
    public int getItemCount() {
        return viewType == TYPE_MOOD_CARD
                ? (moodCards  != null ? moodCards.length  : 0)
                : (historyList != null ? historyList.size() : 0);
    }

    // ─── onCreateViewHolder ────────────────────────────────────────────────
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int vType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (vType == TYPE_MOOD_CARD) {
            View v = inflater.inflate(R.layout.item_mood_card, parent, false);
            return new MoodCardVH(v);
        } else {
            View v = inflater.inflate(R.layout.item_mood_history, parent, false);
            return new HistoryRowVH(v);
        }
    }

    // ─── onBindViewHolder ──────────────────────────────────────────────────
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Slide-in animation for every item
        holder.itemView.startAnimation(
                AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left));

        if (holder instanceof MoodCardVH) {
            bindMoodCard((MoodCardVH) holder, position);
        } else if (holder instanceof HistoryRowVH) {
            bindHistoryRow((HistoryRowVH) holder, position);
        }
    }

    // ─── Bind: Mood Card ───────────────────────────────────────────────────
    private void bindMoodCard(MoodCardVH vh, int position) {
        String[] mood = moodCards[position];
        // mood[0] = emoji, mood[1] = name, mood[2] = description

        vh.tvEmoji.setText(mood[0]);
        vh.tvName.setText(mood[1]);
        vh.tvDesc.setText(mood[2]);

        // Alternate pastel card colours for visual variety
        int[] pastelBg = {
                R.color.card_track_bg, R.color.card_assessment_bg, R.color.card_insights_bg, R.color.mood_score_bg
        };
        vh.card.setCardBackgroundColor(
                ContextCompat.getColor(context, pastelBg[position % pastelBg.length]));

        vh.itemView.setOnClickListener(v -> {
            // Scale-pulse feedback
            vh.itemView.animate().scaleX(0.92f).scaleY(0.92f).setDuration(100)
                    .withEndAction(() -> vh.itemView.animate()
                            .scaleX(1f).scaleY(1f).setDuration(100).start())
                    .start();
            if (clickListener != null) {
                clickListener.onMoodCardClick(mood);
            }
        });
    }

    // ─── Bind: History Row ─────────────────────────────────────────────────
    private void bindHistoryRow(HistoryRowVH vh, int position) {
        MoodEntry entry = historyList.get(position);

        vh.tvMood.setText(entry.getMood());
        vh.tvDateTime.setText("🕐 " + entry.getDateTime());
        vh.tvTrigger.setText("⚡ Trigger: " + entry.getTriggerPoint());
        vh.tvIntensity.setText("📊 Intensity: " + entry.getIntensity());
        vh.tvReason.setText("📝 " + (entry.getReason().equals("Not specified")
                ? "No reason noted" : entry.getReason()));

        // Semantic card backgrounds based on sentiment
        vh.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_white));

        vh.tvSentiment.setText(entry.isPositive() ? "😊 Positive" : "😞 Negative");
        vh.tvSentiment.setTextColor(entry.isPositive()
                ? ContextCompat.getColor(context, R.color.positive_green)
                : ContextCompat.getColor(context, R.color.negative_red));
    }

    // ─── ViewHolder: Mood Card Grid ────────────────────────────────────────
    public static class MoodCardVH extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvEmoji, tvName, tvDesc;

        public MoodCardVH(@NonNull View itemView) {
            super(itemView);
            card    = (CardView) itemView;
            tvEmoji = itemView.findViewById(R.id.tvMoodEmoji);
            tvName  = itemView.findViewById(R.id.tvMoodName);
            tvDesc  = itemView.findViewById(R.id.tvMoodDesc);
        }
    }

    // ─── ViewHolder: History Row ───────────────────────────────────────────
    public static class HistoryRowVH extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvMood, tvDateTime, tvTrigger, tvIntensity, tvReason, tvSentiment;

        public HistoryRowVH(@NonNull View itemView) {
            super(itemView);
            card        = itemView.findViewById(R.id.cardHistoryItem);
            tvMood      = itemView.findViewById(R.id.tvHistoryMood);
            tvDateTime  = itemView.findViewById(R.id.tvHistoryDateTime);
            tvTrigger   = itemView.findViewById(R.id.tvHistoryTrigger);
            tvIntensity = itemView.findViewById(R.id.tvHistoryIntensity);
            tvReason    = itemView.findViewById(R.id.tvHistoryReason);
            tvSentiment = itemView.findViewById(R.id.tvHistorySentiment);
        }
    }

    // ─── Public helpers ────────────────────────────────────────────────────

    /** Refresh history list data (call after new mood is saved) */
    public void updateHistoryList(List<MoodEntry> newList) {
        this.historyList = newList;
        notifyDataSetChanged();
    }

    /** Refresh mood card data */
    public void updateMoodCards(String[][] newCards) {
        this.moodCards = newCards;
        notifyDataSetChanged();
    }
}
