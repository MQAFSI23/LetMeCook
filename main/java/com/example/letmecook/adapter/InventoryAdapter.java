package com.example.letmecook.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.letmecook.databinding.ItemInventoryBinding;
import com.example.letmecook.db.entity.InventoryItem;

public class InventoryAdapter extends ListAdapter<InventoryItem, InventoryAdapter.InventoryViewHolder> {

    private final OnItemInteractionListener listener;

    public interface OnItemInteractionListener {
        void onItemClick(InventoryItem item);
        void onDeleteClick(InventoryItem item);
    }

    public InventoryAdapter(@NonNull DiffUtil.ItemCallback<InventoryItem> diffCallback, OnItemInteractionListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInventoryBinding binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new InventoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        InventoryItem current = getItem(position);
        holder.bind(current, listener);
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemInventoryBinding binding;

        public InventoryViewHolder(ItemInventoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(InventoryItem item, OnItemInteractionListener listener) {
            binding.textItemName.setText(item.itemName);
            String quantityText = item.quantity + " " + item.unit;
            binding.textItemQuantity.setText(quantityText);

            binding.buttonDelete.setOnClickListener(v -> listener.onDeleteClick(item));
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }

    public static class InventoryDiff extends DiffUtil.ItemCallback<InventoryItem> {
        @Override
        public boolean areItemsTheSame(@NonNull InventoryItem oldItem, @NonNull InventoryItem newItem) {
            return oldItem.itemName.equals(newItem.itemName);
        }

        @Override
        public boolean areContentsTheSame(@NonNull InventoryItem oldItem, @NonNull InventoryItem newItem) {
            return oldItem.itemName.equals(newItem.itemName) &&
                    oldItem.quantity == newItem.quantity &&
                    oldItem.unit.equals(newItem.unit);
        }
    }
}