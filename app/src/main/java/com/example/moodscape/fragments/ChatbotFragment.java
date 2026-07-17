package com.example.moodscape.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.moodscape.R;

import java.util.*;
import com.example.moodscape.adapters.ChatAdapter;
import com.example.moodscape.adapters.ChatAdapter.ChatMessage;
public class ChatbotFragment extends Fragment {
    RecyclerView rvChat;
    EditText etMessage;
    ImageButton btnSend;
    ChatAdapter adapter;
    List<ChatMessage> messages = new ArrayList<>();

    // Simple NLP keyword-response map
    static final Map<String, String> RESPONSES = new LinkedHashMap<String, String>() {{
        put("sad|depressed|unhappy|down", "I'm really sorry you're feeling that way 💙. Remember, it's okay to not be okay. Would you like to talk about what's making you feel this way?");
        put("happy|great|wonderful|excited|joy", "That's amazing! 🎉 Your happiness is contagious! What's making you feel so good today?");
        put("anxious|worried|nervous|stress|anxiety", "I understand anxiety can be tough 😰. Try this: Take 5 deep breaths slowly. Inhale for 4 counts, hold for 4, exhale for 4. Want some more tips?");
        put("angry|frustrat|mad|annoyed", "I hear you 😤. Anger is a valid feeling. Try to step away from the situation for a few minutes. What triggered this feeling?");
        put("lonely|alone|isolated|no friends", "You're not alone – I'm here with you 🤗. Feeling lonely is something many people experience. Want to talk about what's going on?");
        put("tired|exhaust|sleep|fatigue", "Rest is so important for your well-being 😴. Are you getting enough sleep? Even 7-8 hours can make a huge difference.");
        put("motivation|motivat|lazy|unmotivated", "I believe in you! 💪 Sometimes starting small is the key. What's one tiny step you can take right now toward your goal?");
        put("love|relation|partner|heart", "Relationships can bring so much joy and also pain 💕. How are things going for you?");
        put("hello|hi|hey|good morning|good evening", "Hey there! 👋 I'm MoodBot, your mental wellness companion. How are you feeling today?");
        put("help|support|advice|suggest", "I'm here to help! 🌟 You can share your feelings, ask for coping tips, or just chat. What's on your mind?");
        put("thanks|thank you|grateful", "You're so welcome! 😊 Remember, you can always talk to me whenever you need support. Take care! 🌈");
        put("bye|goodbye|see you|later", "Take care of yourself! 💙 Remember, I'm always here whenever you need to talk. Goodbye! 🌟");
    }};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        rvChat = view.findViewById(R.id.rvChat);
        etMessage = view.findViewById(R.id.etChatMessage);
        btnSend = view.findViewById(R.id.btnSendChat);

        adapter = new ChatAdapter(requireContext(), messages);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);

        // Initial bot message
        adapter.addMessage(new ChatMessage(
            "👋 Hello! I'm MoodBot, your personal mental wellness companion.\n\n" +
            "Feel free to share how you're feeling today. I'm here to listen, support, and guide you! 💙",
            false));

        btnSend.setOnClickListener(v -> sendMessage());
        return view;
    }

    private void sendMessage() {
        String msg = etMessage.getText().toString().trim();
        if (msg.isEmpty()) return;

        adapter.addMessage(new ChatMessage(msg, true));
        rvChat.scrollToPosition(adapter.getItemCount() - 1);
        etMessage.setText("");

        rvChat.postDelayed(() -> {
            String response = getBotResponse(msg.toLowerCase());
            adapter.addMessage(new ChatMessage(response, false));
            rvChat.scrollToPosition(adapter.getItemCount() - 1);
        }, 800);
    }

    private String getBotResponse(String input) {
        for (Map.Entry<String, String> entry : RESPONSES.entrySet()) {
            String[] keywords = entry.getKey().split("\\|");
            for (String keyword : keywords) {
                if (input.contains(keyword)) return entry.getValue();
            }
        }
        return "I understand 💙. It sounds like you're going through something. Would you like to:\n" +
                "1️⃣ Track your mood\n2️⃣ Do a quick breathing exercise\n3️⃣ Just keep talking\n\n" +
                "I'm here for you no matter what! 🌟";
    }

}
