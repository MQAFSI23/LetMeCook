package com.example.letmecookapp.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.os.Handler;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;
import com.example.letmecookapp.model.InventoryItem;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.TextPart;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.example.letmecookapp.BuildConfig;
import com.example.letmecookapp.R;
import com.example.letmecookapp.adapter.ChatAdapter;
import com.example.letmecookapp.model.ChatMessage;
import com.example.letmecookapp.utils.ChatHistoryManager;
import com.example.letmecookapp.data.InventoryDataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class AiChatFragment extends Fragment {
    private RecyclerView recyclerViewChat;
    private EditText editTextChatInput;
    private MaterialButton buttonSendChat;
    private LottieAnimationView lottieLoaderChat;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessageList;
    private ChatHistoryManager chatHistoryManager;
    private InventoryDataSource inventoryDataSource;

    private SearchView searchView;
    private ImageButton buttonSearchChat;
    private CardView searchContainer;
    private List<ChatMessage> fullChatMessageList;
    private Handler searchHandler;
    private FloatingActionButton buttonClearChat;
    private Runnable searchRunnable;
    private TextView noResultsTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatHistoryManager = new ChatHistoryManager(requireContext());
        inventoryDataSource = new InventoryDataSource(requireContext());
        searchHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ai_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewChat = view.findViewById(R.id.recycler_view_chat);
        editTextChatInput = view.findViewById(R.id.edit_text_chat_input);
        buttonSendChat = view.findViewById(R.id.button_send_chat);
        lottieLoaderChat = view.findViewById(R.id.lottie_loader_chat);
        searchView = view.findViewById(R.id.search_view_chat);
        buttonSearchChat = view.findViewById(R.id.button_search_chat);
        buttonClearChat = view.findViewById(R.id.button_clear_chat);
        searchContainer = view.findViewById(R.id.search_container);
        noResultsTextView = view.findViewById(R.id.text_view_no_search_results);

        setupChat();
        setupLottieTheme();
        setupSearch();

        buttonClearChat.setOnClickListener(v -> {
            if (chatMessageList.size() > 1) {
                showDeleteConfirmationDialog();
            } else {
                Toast.makeText(getContext(), "Chat history is already empty.", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSendChat.setOnClickListener(v -> handleSendEvent());
    }

    @Override
    public void onResume() {
        super.onResume();
        inventoryDataSource.open();
    }

    @Override
    public void onPause() {
        super.onPause();
        inventoryDataSource.close();
    }

    private String getInventoryAsString() {
        List<InventoryItem> items = inventoryDataSource.getAllIngredients();
        if (items.isEmpty()) {
            return "The user's inventory is currently empty.";
        }
        return items.stream()
                .map(item -> {
                    String quantityStr;
                    if (item.getQuantity() == (long) item.getQuantity()) {
                        quantityStr = String.format(Locale.US, "%d", (long) item.getQuantity());
                    } else {
                        quantityStr = String.format(Locale.US, "%.4f", item.getQuantity());
                    }
                    return "- " + item.getName() + ": " + quantityStr + " " + item.getUnit();
                })
                .collect(Collectors.joining("\n"));
    }

    private void setupLottieTheme() {
        if (getContext() == null) return;
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            KeyPath keyPath = new KeyPath("**", "Stroke 1", "Color");
            int colorForDarkMode = ContextCompat.getColor(getContext(), R.color.md_theme_onSurface);
            LottieValueCallback<Integer> colorCallback = new LottieValueCallback<>(colorForDarkMode);
            lottieLoaderChat.addValueCallback(keyPath, LottieProperty.STROKE_COLOR, colorCallback);
        }
    }

    private void setupChat() {
        chatMessageList = chatHistoryManager.loadChatHistory();
        if (chatMessageList.isEmpty()) {
            chatMessageList.add(new ChatMessage(" Hello! I'm your cooking assistant. Ask me anything about recipes!", false));
        }
        fullChatMessageList = new ArrayList<>(chatMessageList);
        chatAdapter = new ChatAdapter(requireContext(), chatMessageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewChat.setLayoutManager(layoutManager);
        recyclerViewChat.setAdapter(chatAdapter);
        recyclerViewChat.scrollToPosition(chatMessageList.size() - 1);
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Chat")
                .setMessage("Apakah Anda yakin ingin menghapus seluruh riwayat chat?")
                .setPositiveButton("Hapus", (dialog, which) -> clearChatHistory())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void setupSearch() {
        buttonSearchChat.setOnClickListener(v -> {
            searchContainer.setVisibility(searchContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            if (searchContainer.getVisibility() == View.GONE) {
                searchView.setQuery("", false);
                filterChatHistory("");
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchHandler.removeCallbacks(searchRunnable);
                filterChatHistory(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> filterChatHistory(newText);
                searchHandler.postDelayed(searchRunnable, 1000);
                return true;
            }
        });

        searchView.setOnCloseListener(() -> {
            searchContainer.setVisibility(View.GONE);
            filterChatHistory("");
            return true;
        });
    }

    private void filterChatHistory(String query) {
        List<ChatMessage> filteredList = new ArrayList<>();
        chatAdapter.setSearchQuery(query);

        if (query.isEmpty()) {
            filteredList.addAll(fullChatMessageList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (ChatMessage message : fullChatMessageList) {
                if (message.getMessage().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(message);
                }
            }
        }

        if (filteredList.isEmpty() && !query.isEmpty()) {
            recyclerViewChat.setVisibility(View.GONE);
            noResultsTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerViewChat.setVisibility(View.VISIBLE);
            noResultsTextView.setVisibility(View.GONE);
        }

        chatAdapter.filterList(filteredList);
        if (!filteredList.isEmpty()) {
            recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    private void clearChatHistory() {
        chatHistoryManager.clearChatHistory();
        chatMessageList.clear();
        fullChatMessageList.clear();

        ChatMessage initialMessage = new ChatMessage("Hello! I'm your cooking assistant. Ask me anything about recipes!", false);
        chatMessageList.add(initialMessage);
        fullChatMessageList.add(initialMessage);

        recyclerViewChat.setVisibility(View.VISIBLE);
        noResultsTextView.setVisibility(View.GONE);

        if (searchView != null) {
            searchView.setQuery("", false);
        }

        chatAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Chat history has been cleared.", Toast.LENGTH_SHORT).show();
    }

    private void handleSendEvent() {
        String userInput = editTextChatInput.getText().toString().trim();
        if (!userInput.isEmpty()) {
            addMessageToChat(userInput, true);
            callGeminiApi(userInput);
            editTextChatInput.setText("");
        } else {
            Toast.makeText(getContext(), "Please write a message.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addMessageToChat(String message, boolean isFromUser) {
        ChatMessage newMessage = new ChatMessage(message, isFromUser);

        fullChatMessageList.add(newMessage);

        if (searchView.getQuery().toString().isEmpty()) {
            chatMessageList.add(newMessage);
            chatAdapter.notifyItemInserted(chatMessageList.size() - 1);
        }

        if (searchView.getQuery().toString().isEmpty() || isFromUser) {
            recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
        }

        chatHistoryManager.saveChatHistory(fullChatMessageList);
    }

    private void callGeminiApi(String query) {
        setLoading(true);

        GenerativeModel gm = new GenerativeModel("gemini-2.0-flash", BuildConfig.GEMINI_API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        List<Content> historyForApi = new ArrayList<>();

        String aiInstructionText = getInstructionText();
        historyForApi.add(new Content("user", Collections.singletonList(new TextPart(aiInstructionText))));

        historyForApi.add(new Content("model", Collections.singletonList(new TextPart("Of course, I understand. I am LetMeCook Assistant. Ready to help!"))));

        for (ChatMessage message : chatMessageList) {
            if (message.getMessage().equals("Hello! I'm your cooking assistant. Ask me anything about recipes!")) {
                continue;
            }
            if (message.isFromUser()) {
                historyForApi.add(new Content("user", Collections.singletonList(new TextPart(message.getMessage()))));
            } else {
                historyForApi.add(new Content("model", Collections.singletonList(new TextPart(message.getMessage()))));
            }
        }

        com.google.ai.client.generativeai.java.ChatFutures chat = model.startChat(historyForApi);

        Content userMessageContent = new Content.Builder().addText(query).build();

        ListenableFuture<GenerateContentResponse> response = chat.sendMessage(userMessageContent);
        Executor mainExecutor = ContextCompat.getMainExecutor(requireContext());

        Futures.addCallback(response, new FutureCallback<>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                addMessageToChat(resultText, false);
                setLoading(false);
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                t.printStackTrace();
                addMessageToChat("Failed to connect to the AI. Maybe try checking your network?  ", false);
                setLoading(false);
            }
        }, mainExecutor);
    }

    @NonNull
    private String getInstructionText() {
        String inventoryInfo = getInventoryAsString();
        return "CONTEXT: The user has the following ingredients in their inventory (or whatever it is called in any languages):\n" +
                inventoryInfo +
                "\n\nTASK: You are LetMeCook Assistant, a friendly cooking assistant with an expert in all kinds of recipes. " +
                "When a user asks for recipes, you MUST prioritize using the ingredients from their inventory. " +
                "If some ingredients are missing for a recipe, clearly state which ones are from the inventory and which ones are needed. " +
                "Always provide clear and easy-to-follow answers. Use Markdown format for lists and steps. " +
                "If the user talks about anything too off-topic from cooking (except greetings), being the friendly chef " +
                "if user ask about recipes, just give the the ingredients, step-by-step to do and tips. dont use opening and closig statement" +
                "You can talk in any language too, like Indonesian or english etc.";
    }

    private void setLoading(boolean isLoading) {
        lottieLoaderChat.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            recyclerViewChat.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
        buttonSendChat.setEnabled(!isLoading);
        editTextChatInput.setEnabled(!isLoading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchHandler.removeCallbacksAndMessages(null);
    }
}