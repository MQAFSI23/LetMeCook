package com.example.letmecook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letmecook.R;
import com.example.letmecook.adapter.InventoryAdapter;
import com.example.letmecook.data.InventoryDataSource;
import com.example.letmecook.model.InventoryItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;

public class InventoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private List<InventoryItem> inventoryItems;
    private InventoryDataSource dataSource;
    private TextView emptyView;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataSource = new InventoryDataSource(requireContext());
        dataSource.open();
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_inventory);
        emptyView = view.findViewById(R.id.text_view_empty_inventory);
        fab = view.findViewById(R.id.fab_add_ingredient);

        int numberOfColumns = 2;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        fab.setOnClickListener(v -> showAddEditDialog(null, -1));

        loadInventory();
    }

    @Override
    public void onResume() {
        dataSource.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        dataSource.close();
        super.onPause();
    }


    private void loadInventory() {
        inventoryItems = dataSource.getAllIngredients(); // Fetch from SQLite
        adapter = new InventoryAdapter(requireContext(), inventoryItems, new InventoryAdapter.OnItemInteractionListener() {
            @Override
            public void onEditClick(InventoryItem item, int position) {
                showAddEditDialog(item, position);
            }

            @Override
            public void onDeleteClick(InventoryItem item, int position) {
                showDeleteConfirmationDialog(item, position);
            }
        });
        recyclerView.setAdapter(adapter);
        toggleEmptyView();
    }

    private void toggleEmptyView() {
        if (inventoryItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showDeleteConfirmationDialog(InventoryItem item, int position) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Ingredient")
                .setMessage("Are you sure you want to delete " + item.getName() + "?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    dataSource.deleteIngredient(item);
                    inventoryItems.remove(position);
                    adapter.notifyItemRemoved(position);
                    toggleEmptyView();
                    Toast.makeText(getContext(), item.getName() + " deleted", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void showAddEditDialog(@Nullable final InventoryItem item, final int position) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_ingredient, null);

        final TextInputEditText nameEditText = dialogView.findViewById(R.id.edit_text_name);
        final TextInputEditText quantityEditText = dialogView.findViewById(R.id.edit_text_quantity);
        final AutoCompleteTextView unitSpinner = dialogView.findViewById(R.id.spinner_unit);
        final TextInputLayout nameInputLayout = dialogView.findViewById(R.id.input_layout_name);

        String[] units = {"gram", "kg", "mL", "L", "pcs", "tbsp", "tsp"};
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, units);
        unitSpinner.setAdapter(unitAdapter);

        String dialogTitle = (item == null) ? "Add Ingredient" : "Edit Ingredient";
        if (item != null) {
            nameEditText.setText(item.getName());
            quantityEditText.setText(String.valueOf(item.getQuantity()));
            unitSpinner.setText(item.getUnit(), false);
        } else {
            unitSpinner.setText(units[0], false);
        }

        final AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(dialogTitle)
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null)
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = Objects.requireNonNull(nameEditText.getText()).toString().trim();
            String quantityStr = Objects.requireNonNull(quantityEditText.getText()).toString().trim();
            String unit = unitSpinner.getText().toString();

            if (name.isEmpty()) {
                nameInputLayout.setError("Ingredient name cannot be empty");
                return;
            } else {
                nameInputLayout.setError(null);
            }

            if (quantityStr.isEmpty()) {
                quantityEditText.setError("Quantity cannot be empty");
                return;
            }

            double quantity = Double.parseDouble(quantityStr);

            if (item == null) {
                InventoryItem newItem = dataSource.addIngredient(name, quantity, unit);
                inventoryItems.add(newItem);
                adapter.notifyItemInserted(inventoryItems.size() - 1);
                Toast.makeText(getContext(), name + " added", Toast.LENGTH_SHORT).show();
            } else {
                item.setName(name);
                item.setQuantity(quantity);
                item.setUnit(unit);
                dataSource.updateIngredient(item);
                inventoryItems.set(position, item);
                adapter.notifyItemChanged(position);
                Toast.makeText(getContext(), name + " updated", Toast.LENGTH_SHORT).show();
            }
            toggleEmptyView();
            dialog.dismiss();
        });
    }
}