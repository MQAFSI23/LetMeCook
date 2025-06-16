package com.example.letmecook.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "inventory")
public class InventoryItem {
    @PrimaryKey
    @NonNull
    public String itemName;
    public double quantity;
    public String unit; // e.g., "mL", "gram", "buah"
}