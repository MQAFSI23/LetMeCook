package com.example.letmecook.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.R;
import com.example.letmecook.model.ChatMessage;

import java.util.List;
import java.util.Locale;

import io.noties.markwon.Markwon;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private final List<ChatMessage> chatMessages;
    private final Markwon markwon;
    private String searchQuery = "";
    private final Context context;

    public ChatAdapter(Context context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
        this.markwon = Markwon.create(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).isFromUser()) {
            return 1; // VIEW_TYPE_SENT
        } else {
            return 2; // VIEW_TYPE_RECEIVED
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = (viewType == 1) ? R.layout.item_chat_message_sent : R.layout.item_chat_message_received;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new ChatViewHolder(view, markwon);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        holder.bind(chatMessage, searchQuery);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void filterList(List<ChatMessage> filteredList) {
        chatMessages.clear();
        chatMessages.addAll(filteredList);
        notifyDataSetChanged();
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query.toLowerCase(Locale.getDefault());
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final Markwon markwon;

        public ChatViewHolder(@NonNull View itemView, Markwon markwon) {
            super(itemView);
            this.messageText = itemView.findViewById(R.id.text_chat_message);
            this.markwon = markwon;
        }

        void bind(ChatMessage chatMessage, String query) {
            String originalMessage = chatMessage.getMessage();

            if (!chatMessage.isFromUser()) {
                markwon.setMarkdown(messageText, originalMessage);
            } else {
                messageText.setText(originalMessage);
            }

            highlightText(messageText, query);
        }

        private void highlightText(TextView textView, String query) {
            CharSequence text = textView.getText();

            if (query.isEmpty() || text.length() == 0) {
                return;
            }

            SpannableString spannableString = new SpannableString(text);
            String textAsString = text.toString().toLowerCase(Locale.getDefault());

            int startIdx = 0;
            while ((startIdx = textAsString.indexOf(query, startIdx)) >= 0) {
                int endIdx = startIdx + query.length();

                BackgroundColorSpan highlightSpan = new BackgroundColorSpan(
                        ContextCompat.getColor(context, R.color.search_highlight_color)
                );

                spannableString.setSpan(highlightSpan, startIdx, endIdx, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                startIdx = endIdx;
            }

            textView.setText(spannableString);
        }
    }
}