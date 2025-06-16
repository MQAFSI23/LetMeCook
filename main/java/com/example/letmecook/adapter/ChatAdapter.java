package com.example.letmecook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.letmecook.databinding.ItemChatUserBinding;
import com.example.letmecook.databinding.ItemRecipeCardBinding;
import com.example.letmecook.db.entity.Recipe;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER_MESSAGE = 1;
    private static final int VIEW_TYPE_AI_RECIPE = 2;

    private final List<Object> chatItems;
    private final RecipeAdapter.OnFavoriteClickListener favoriteClickListener;

    public ChatAdapter(List<Object> chatItems, RecipeAdapter.OnFavoriteClickListener listener) {
        this.chatItems = chatItems;
        this.favoriteClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatItems.get(position) instanceof String) {
            return VIEW_TYPE_USER_MESSAGE;
        } else if (chatItems.get(position) instanceof Recipe) {
            return VIEW_TYPE_AI_RECIPE;
        }
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER_MESSAGE) {
            ItemChatUserBinding binding = ItemChatUserBinding.inflate(inflater, parent, false);
            return new UserMessageViewHolder(binding);
        } else { // VIEW_TYPE_AI_RECIPE
            ItemRecipeCardBinding binding = ItemRecipeCardBinding.inflate(inflater, parent, false);
            // Kita menggunakan kembali ViewHolder dari RecipeAdapter
            return new RecipeAdapter.RecipeViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = chatItems.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_USER_MESSAGE) {
            ((UserMessageViewHolder) holder).bind((String) item);
        } else { // VIEW_TYPE_AI_RECIPE
            ((RecipeAdapter.RecipeViewHolder) holder).bind((Recipe) item, favoriteClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return chatItems.size();
    }

    // ViewHolder untuk pesan pengguna
    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatUserBinding binding;

        public UserMessageViewHolder(ItemChatUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String message) {
            binding.textUserMessage.setText(message);
        }
    }
}