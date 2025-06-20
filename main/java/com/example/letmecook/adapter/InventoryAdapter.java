package com.example.letmecook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.letmecook.R;
import com.example.letmecook.model.InventoryItem;
import java.util.List;
import java.util.Locale;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private final List<InventoryItem> inventoryItems;
    private final OnItemInteractionListener listener;
    private final Context context;


    public interface OnItemInteractionListener {
        void onEditClick(InventoryItem item, int position);
        void onDeleteClick(InventoryItem item, int position);
    }

    public InventoryAdapter(Context context, List<InventoryItem> inventoryItems, OnItemInteractionListener listener) {
        this.context = context;
        this.inventoryItems = inventoryItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inventory, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem item = inventoryItems.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return inventoryItems.size();
    }

    class InventoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textIngredientName;
        private final TextView textIngredientQuantity;
        private final ImageButton buttonEdit;
        private final ImageButton buttonDelete;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textIngredientName = itemView.findViewById(R.id.text_ingredient_name);
            textIngredientQuantity = itemView.findViewById(R.id.text_ingredient_quantity);
            buttonEdit = itemView.findViewById(R.id.button_edit_ingredient);
            buttonDelete = itemView.findViewById(R.id.button_delete_ingredient);
        }

        public void bind(final InventoryItem item, final OnItemInteractionListener listener) {
            textIngredientName.setText(item.getName());

            String quantityStr;
            if (item.getQuantity() == (long) item.getQuantity()) {
                quantityStr = String.format(Locale.US, "%d", (long) item.getQuantity());
            } else {
                quantityStr = String.format(Locale.US, "%.2f", item.getQuantity());
            }

            textIngredientQuantity.setText(String.format("%s %s", quantityStr, item.getUnit()));

            buttonEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onEditClick(inventoryItems.get(position), position);
                }
            });

            buttonDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(inventoryItems.get(position), position);
                }
            });
        }
    }
}