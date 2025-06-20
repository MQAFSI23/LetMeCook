package com.example.letmecook.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.letmecook.database.DatabaseHelper;
import com.example.letmecook.model.InventoryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all database operations for the Inventory table.
 */
public class InventoryDataSource {

    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;
    private final String[] allColumns = {
            DatabaseHelper.COLUMN_INVENTORY_ID,
            DatabaseHelper.COLUMN_INGREDIENT_NAME,
            DatabaseHelper.COLUMN_INGREDIENT_QUANTITY,
            DatabaseHelper.COLUMN_INGREDIENT_UNIT
    };

    public InventoryDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public InventoryItem addIngredient(String name, double quantity, String unit) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_INGREDIENT_NAME, name);
        values.put(DatabaseHelper.COLUMN_INGREDIENT_QUANTITY, quantity);
        values.put(DatabaseHelper.COLUMN_INGREDIENT_UNIT, unit);

        long insertId = database.insert(DatabaseHelper.TABLE_INVENTORY, null, values);

        try (Cursor cursor = database.query(DatabaseHelper.TABLE_INVENTORY, allColumns,
                DatabaseHelper.COLUMN_INVENTORY_ID + " = " + insertId,
                null, null, null, null)) {
            cursor.moveToFirst();
            return cursorToInventoryItem(cursor);
        }
    }

    public void deleteIngredient(InventoryItem item) {
        long id = item.getId();
        database.delete(DatabaseHelper.TABLE_INVENTORY, DatabaseHelper.COLUMN_INVENTORY_ID + " = " + id, null);
    }

    public void updateIngredient(InventoryItem item) {
        long id = item.getId();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_INGREDIENT_NAME, item.getName());
        values.put(DatabaseHelper.COLUMN_INGREDIENT_QUANTITY, item.getQuantity());
        values.put(DatabaseHelper.COLUMN_INGREDIENT_UNIT, item.getUnit());
        database.update(DatabaseHelper.TABLE_INVENTORY, values, DatabaseHelper.COLUMN_INVENTORY_ID + " = " + id, null);
    }

    public List<InventoryItem> getAllIngredients() {
        List<InventoryItem> items = new ArrayList<>();
        try (Cursor cursor = database.query(DatabaseHelper.TABLE_INVENTORY, allColumns,
                null, null, null, null, DatabaseHelper.COLUMN_INGREDIENT_NAME + " ASC")) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                InventoryItem item = cursorToInventoryItem(cursor);
                items.add(item);
                cursor.moveToNext();
            }
        }
        return items;
    }

    private InventoryItem cursorToInventoryItem(Cursor cursor) {
        InventoryItem item = new InventoryItem();
        item.setId(cursor.getLong(0));
        item.setName(cursor.getString(1));
        item.setQuantity(cursor.getDouble(2));
        item.setUnit(cursor.getString(3));
        return item;
    }
}