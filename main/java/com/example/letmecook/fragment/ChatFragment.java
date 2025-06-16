package com.example.letmecook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.letmecook.adapter.ChatAdapter;
import com.example.letmecook.databinding.FragmentChatBinding;
import com.example.letmecook.viewmodel.RecipeViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private RecipeViewModel recipeViewModel;
    private ChatAdapter chatAdapter;
    private final List<Object> chatHistory = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);

        setupRecyclerView();
        setupObservers();

        binding.buttonSend.setOnClickListener(v -> handleSendClick());

        return root;
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(chatHistory, recipe -> recipeViewModel.toggleFavorite(recipe));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerviewChat.setLayoutManager(layoutManager);
        binding.recyclerviewChat.setAdapter(chatAdapter);
    }

    private void handleSendClick() {
        String query = Objects.requireNonNull(binding.edittextChatInput.getText()).toString().trim();
        if (!query.isEmpty()) {
            // Tampilkan pesan pengguna di UI
            addMessageToChat(query);
            binding.edittextChatInput.setText("");

            // Tampilkan progress bar dan panggil AI
            binding.progressbar.setVisibility(View.VISIBLE);
            binding.buttonSend.setEnabled(false);
            recipeViewModel.getRecipeFromAi(query);
        }
    }

    private void setupObservers() {
        // Observer untuk resep yang berhasil dibuat AI
        recipeViewModel.getAiGeneratedRecipe().observe(getViewLifecycleOwner(), recipe -> {
            if (recipe != null) {
                binding.progressbar.setVisibility(View.GONE);
                binding.buttonSend.setEnabled(true);

                // Tambahkan kartu resep ke riwayat chat
                addMessageToChat(recipe);

                // Konsumsi event agar tidak muncul lagi
                recipeViewModel.onAiRecipeConsumed();
            }
        });

        // Observer untuk error dari AI
        recipeViewModel.getAiError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                binding.progressbar.setVisibility(View.GONE);
                binding.buttonSend.setEnabled(true);
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addMessageToChat(Object message) {
        chatHistory.add(message);
        chatAdapter.notifyItemInserted(chatHistory.size() - 1);
        binding.recyclerviewChat.scrollToPosition(chatHistory.size() - 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}