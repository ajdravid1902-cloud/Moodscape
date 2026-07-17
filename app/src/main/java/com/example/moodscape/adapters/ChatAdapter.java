package com.example.moodscape.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodscape.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // ─── View Types ────────────────────────────────────────────────────────
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT  = 0;

    // ─── Data model ────────────────────────────────────────────────────────
    public static class ChatMessage {
        public final String  text;
        public final boolean isUser;
        public final String  timestamp;

        public ChatMessage(String text, boolean isUser) {
            this.text      = text;
            this.isUser    = isUser;
            this.timestamp = new SimpleDateFormat("hh:mm a", Locale.getDefault())
                    .format(new Date());
        }
    }

    // ─── Fields ────────────────────────────────────────────────────────────
    private final Context           context;
    private final List<ChatMessage> messages;

    // ─── Constructor ───────────────────────────────────────────────────────
    public ChatAdapter(Context context, List<ChatMessage> messages) {
        this.context  = context;
        this.messages = messages;
    }

    // ─── RecyclerView overrides ────────────────────────────────────────────
    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE_USER) {
            View v = inflater.inflate(R.layout.item_chat_user, parent, false);
            return new UserVH(v);
        } else {
            View v = inflater.inflate(R.layout.item_chat_bot, parent, false);
            return new BotVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);

        // Subtle slide animation
        holder.itemView.startAnimation(
                AnimationUtils.loadAnimation(context,
                        msg.isUser ? android.R.anim.slide_in_left
                                : android.R.anim.fade_in));

        if (holder instanceof UserVH) {
            ((UserVH) holder).bind(msg);
        } else if (holder instanceof BotVH) {
            ((BotVH) holder).bind(msg);
        }
    }

    // ─── ViewHolder: User bubble ───────────────────────────────────────────
    static class UserVH extends RecyclerView.ViewHolder {
        TextView tvMsg, tvTime;
        CardView card;

        UserVH(@NonNull View v) {
            super(v);
            tvMsg  = v.findViewById(R.id.tvChatMsg);
            tvTime = v.findViewById(R.id.tvChatTime);
            card   = v.findViewById(R.id.chatCard);
        }

        void bind(ChatMessage msg) {
            tvMsg.setText(msg.text);
            if (tvTime != null) tvTime.setText(msg.timestamp);
        }
    }

    // ─── ViewHolder: Bot bubble ────────────────────────────────────────────
    static class BotVH extends RecyclerView.ViewHolder {
        TextView tvMsg, tvTime, tvBotLabel;
        CardView card;

        BotVH(@NonNull View v) {
            super(v);
            tvMsg      = v.findViewById(R.id.tvChatMsg);
            tvTime     = v.findViewById(R.id.tvChatTime);
            tvBotLabel = v.findViewById(R.id.tvBotLabel);
            card       = v.findViewById(R.id.chatCard);
        }

        void bind(ChatMessage msg) {
            tvMsg.setText(msg.text);
            if (tvTime != null)     tvTime.setText(msg.timestamp);
            if (tvBotLabel != null) tvBotLabel.setText("🤖 MoodBot");
        }
    }

    // ─── Public helpers ────────────────────────────────────────────────────

    /** Add a single message and notify (use from Fragment, not notifyDataSetChanged) */
    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    /** Clear all messages */
    public void clearChat() {
        messages.clear();
        notifyDataSetChanged();
    }
}
