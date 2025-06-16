package com.example.letmecook.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.letmecook.db.entity.InventoryItem;
import java.util.List;

@Dao
public interface InventoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(InventoryItem item);

    @Delete
    void delete(InventoryItem item);

    @Query("SELECT * FROM inventory ORDER BY itemName ASC")
    LiveData<List<InventoryItem>> getAllItems();

    @Query("SELECT * FROM inventory")
    List<InventoryItem> getAllItemsList(); // Versi non-LiveData untuk pengecekan sinkron
}