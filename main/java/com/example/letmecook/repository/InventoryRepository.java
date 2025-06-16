package com.example.letmecook.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.letmecook.db.AppDatabase;
import com.example.letmecook.db.dao.InventoryDao;
import com.example.letmecook.db.entity.InventoryItem;
import java.util.List;

public class InventoryRepository {

    private final InventoryDao mInventoryDao;
    private final LiveData<List<InventoryItem>> mAllItems;

    public InventoryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mInventoryDao = db.inventoryDao();
        mAllItems = mInventoryDao.getAllItems();
    }

    public LiveData<List<InventoryItem>> getAllItems() {
        return mAllItems;
    }

    public void insert(InventoryItem item) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mInventoryDao.insert(item);
        });
    }

    public void delete(InventoryItem item) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mInventoryDao.delete(item);
        });
    }
}