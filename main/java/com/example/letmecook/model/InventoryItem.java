package com.example.letmecook.model;

import java.io.Serializable;
import java.util.Objects;

public class InventoryItem implements Serializable {

    private long id; // For SQLite primary key
    private String name;
    private double quantity;
    private String unit;

    public InventoryItem() {
    }

    public InventoryItem(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return id == that.id; // Primary key is sufficient for equality check
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}