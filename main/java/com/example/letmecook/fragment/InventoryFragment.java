package com.example.letmecook.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.letmecook.R;
import com.example.letmecook.adapter.InventoryAdapter;
import com.example.letmecook.databinding.DialogAddInventoryBinding;
import com.example.letmecook.databinding.FragmentInventoryBinding;
import com.example.letmecook.db.entity.InventoryItem;
import com.example.letmecook.viewmodel.InventoryViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class InventoryFragment extends Fragment {

    private FragmentInventoryBinding binding;
    private InventoryViewModel inventoryViewModel;
    private InventoryAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInventoryBinding.inflate(inflater, container, false);
        inventoryViewModel = new ViewModelProvider(this).get(InventoryViewModel.class);

        setupRecyclerView();
        setupObservers();

        binding.fabAddItem.setOnClickListener(v -> showAddItemDialog(null));

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new InventoryAdapter(new InventoryAdapter.InventoryDiff(), new InventoryAdapter.OnItemInteractionListener() {
            @Override
            public void onItemClick(InventoryItem item) {
                // Klik untuk mengedit item
                showAddItemDialog(item);
            }

            @Override
            public void onDeleteClick(InventoryItem item) {
                // Konfirmasi sebelum menghapus
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Hapus Bahan")
                        .setMessage("Anda yakin ingin menghapus " + item.itemName + "?")
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("Hapus", (dialog, which) -> inventoryViewModel.delete(item))
                        .show();
            }
        });
        binding.recyclerviewInventory.setAdapter(adapter);
        binding.recyclerviewInventory.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setupObservers() {
        inventoryViewModel.getAllItems().observe(getViewLifecycleOwner(), items -> {
            if (items != null && !items.isEmpty()) {
                adapter.submitList(items);
                binding.recyclerviewInventory.setVisibility(View.VISIBLE);
                binding.textInventoryEmpty.setVisibility(View.GONE);
            } else {
                binding.recyclerviewInventory.setVisibility(View.GONE);
                binding.textInventoryEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showAddItemDialog(@Nullable InventoryItem itemToEdit) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        DialogAddInventoryBinding dialogBinding = DialogAddInventoryBinding.inflate(LayoutInflater.from(getContext()));

        // Setup dropdown untuk satuan
        String[] units = getResources().getStringArray(R.array.inventory_units);
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, units);
        dialogBinding.autocompleteUnit.setAdapter(unitAdapter);

        // Jika mode edit, isi field yang sudah ada
        if (itemToEdit != null) {
            builder.setTitle("Ubah Bahan");
            dialogBinding.edittextItemName.setText(itemToEdit.itemName);
            dialogBinding.edittextItemName.setEnabled(false); // Nama tidak bisa diubah karena primary key
            dialogBinding.edittextItemQuantity.setText(String.valueOf(itemToEdit.quantity));
            dialogBinding.autocompleteUnit.setText(itemToEdit.unit, false);
        } else {
            builder.setTitle("Tambah Bahan Baru");
        }

        builder.setView(dialogBinding.getRoot());
        builder.setPositiveButton(itemToEdit != null ? "Simpan" : "Tambah", (dialog, which) -> {
            // Logika disimpan di listener di bawah agar bisa validasi tanpa menutup dialog
        });
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override positive button untuk validasi
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = Objects.requireNonNull(dialogBinding.edittextItemName.getText()).toString().trim();
            String quantityStr = Objects.requireNonNull(dialogBinding.edittextItemQuantity.getText()).toString().trim();
            String unit = dialogBinding.autocompleteUnit.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(unit)) {
                Toast.makeText(getContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double quantity = Double.parseDouble(quantityStr);
                InventoryItem newItem = new InventoryItem();
                newItem.itemName = name;
                newItem.quantity = quantity;
                newItem.unit = unit;

                inventoryViewModel.insert(newItem); // Room akan handle insert or replace
                dialog.dismiss();

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Jumlah tidak valid", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}